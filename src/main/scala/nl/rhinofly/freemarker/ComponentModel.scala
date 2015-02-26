package nl.rhinofly.freemarker

import scala.collection.JavaConverters.asJavaIteratorConverter

import freemarker.template.ObjectWrapper
import freemarker.template.SimpleCollection
import freemarker.template.TemplateCollectionModel
import freemarker.template.TemplateHashModelEx
import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModel

import lucee.loader.engine.CFMLEngineFactory
import lucee.runtime.Component
import lucee.runtime.exp.PageException

class ComponentModel(component: Component, wrapper: ObjectWrapper) extends TemplateHashModelEx {

  def get(key: String): TemplateModel =
    properties.get(key).map(_()).getOrElse {
      val actualKey = creation.createKey(key)
      if (component.contains(pageContext, actualKey))
        new TemplateMethodModelEx {
          def exec(arguments: java.util.List[_]) =
            component.call(pageContext, actualKey, arguments.toArray)
        }
      else null
    }

  def isEmpty(): Boolean = properties.isEmpty

  def keys(): TemplateCollectionModel =
    new SimpleCollection(properties.keys.iterator.asJava)

  def size(): Int = properties.size

  def values(): TemplateCollectionModel =
    new SimpleCollection(properties.values.map(_()).iterator.asJava)

  private type Lazy[T] = () => T
  private def asLazy(value: => AnyRef): Lazy[TemplateModel] = {
    lazy val initialized = value
    () => wrapper wrap initialized
  }

  private lazy val (pageContext, creation) = {
    val engine = CFMLEngineFactory.getInstance
    (engine.getThreadPageContext, engine.getCreationUtil)
  }
  private lazy val properties = {
    def properties(definition: Component): Map[String, Lazy[TemplateModel]] =
      definition
        .getProperties( /* onlyPeristent = */ false)
        .map(_.getName)
        .map(addLazyValue)
        .toMap ++ propertiesFromParent(definition)

    def addLazyValue(name: String): (String, Lazy[TemplateModel]) =
      name -> asLazy {
        try component.call(pageContext, "get" + name, Array.empty[AnyRef])
        catch {
          case _: PageException =>
            component.getComponentScope.get(creation createKey name)
        }
      }

    def propertiesFromParent(definition: Component) =
      Option(definition.getExtends)
        .filter(_.nonEmpty)
        .map(pageContext.loadComponent)
        .map(properties)
        .getOrElse(Map.empty)

    val componentName =
      ComponentModel.TYPE_PROPERTY_NAME -> asLazy(component.getName)

    properties(component) + componentName
  }
}

object ComponentModel {
  private val TYPE_PROPERTY_NAME = "cfcType"
}