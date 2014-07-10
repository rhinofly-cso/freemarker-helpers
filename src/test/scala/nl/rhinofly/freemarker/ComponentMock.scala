package nl.rhinofly.freemarker

import org.specs2.mock.Mockito
import railo.loader.engine.CFMLEngine
import railo.runtime.`type`.Collection.Key
import railo.runtime.component.Property
import railo.runtime.exp.PageException
import railo.runtime.{Component, ComponentScope, PageContext}

trait ComponentMock extends Mockito {
  def createKey(name:String):Key =
    Util.createKey(name)


  def createProperty(name:String):Property = {
    val p = mock[Property]
    p.getName returns name
    p
  }

  val pc = mock[PageContext]
  val pe = mock[PageException]

  val aComponentScope = mock[ComponentScope]
  aComponentScope.get(createKey("x")) returns "valueOfX"
  aComponentScope.get(createKey("y")) returns "valueOfY"

  val aComponent = mock[Component]
  aComponent.getName returns "A"
  aComponent.getProperties(false) returns Array(createProperty("x"), createProperty("y"))
  aComponent.getComponentScope returns aComponentScope
  aComponent.call(any,any,any) answers { (args,mock)  => {
    val key = args.asInstanceOf[Array[Any]](1).asInstanceOf[String]
    key match {
      case "gety" => "getterY"
      case "f"    => "resultOfF"
      case _      => throw pe
    }
  }}
  aComponent.contains(pc,createKey("f")) returns true



  val bComponentScope = mock[ComponentScope]
  bComponentScope.get(createKey("x")) returns "inheritedValueOfX"
  bComponentScope.get(createKey("y")) returns "inheritedValueOfY"
  bComponentScope.get(createKey("s")) returns "valueOfS"
  bComponentScope.get(createKey("t")) returns "valueOfT"

  val bComponent = mock[Component]
  bComponent.getName returns "B"
  bComponent.getExtends returns "A"
  bComponent.getComponentScope returns bComponentScope
  bComponent.getProperties(false) returns Array(createProperty("s"), createProperty("t"))
  bComponent.call(any,any,any) answers { (args,mock)  => {
    val key = args.asInstanceOf[Array[Any]](1).asInstanceOf[String]
    key match {
      case "gety" => "inheritedGetterY"
      case "f"    => "inheritedResultOfF"
      case _      => throw pe
    }
  }}
  bComponent.contains(pc,createKey("f")) returns true

  pc.loadComponent("A") returns aComponent
  pc.loadComponent("B") returns bComponent

  val engine = mock[CFMLEngine]
  engine.getThreadPageContext returns pc

  val facade = mock[CFMLEngineFacade]
  facade.getCFMLEngine returns engine
}
