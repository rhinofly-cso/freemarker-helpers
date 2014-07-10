package nl.rhinofly.freemarker

import freemarker.template._
import org.specs2.execute.Result
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification
import scala.collection.JavaConversions._

class ComponentModelTest extends Specification with ComponentMock {

  def run(test: (TemplateHashModelEx,TemplateHashModelEx) => Result):Result = {
    test(
      new RailoObjectWrapper(facade).wrap(aComponent).asInstanceOf[TemplateHashModelEx],
      new RailoObjectWrapper(facade).wrap(bComponent).asInstanceOf[TemplateHashModelEx]
    )
  }

  implicit def templateModelIteratorToScalaIterator(it:TemplateModelIterator) = new Iterator[TemplateModel] {
    override def hasNext: Boolean = it.hasNext
    override def next(): TemplateModel = it.next
  }

  implicit def templateCollectionModelToListOfTemplateModels (coll:TemplateCollectionModel):Set[TemplateModel] =
    coll.iterator.toSet

  implicit def templateModelToTemplateMethodModelEx (x:TemplateModel) =
    x.asInstanceOf[TemplateMethodModelEx]

  def matchSetOfSimpleScalars(ys:Set[String]):Matcher[TemplateCollectionModel] =
    { xs:TemplateCollectionModel => xs.map { x:TemplateModel => x.asInstanceOf[SimpleScalar].toString }} ^^ equalTo(ys)

  def matchSimpleScalar(y:String):Matcher[Object] =
    beAnInstanceOf[SimpleScalar] and { x:Object => x.asInstanceOf[SimpleScalar].toString } ^^ equalTo(y)

  def matchTemplateMethodModelEx:Matcher[Object] =
    beAnInstanceOf[TemplateMethodModelEx]

  "A ComponentModel" should {
    "return a property" in run { (a, b) =>
      a.get("x") must matchSimpleScalar("valueOfX")
    }

    "return an inherited property" in run { (a,b) =>
      b.get("x") must matchSimpleScalar("inheritedValueOfX")
    }

    "return the type of a component when asked for the cfcType property" in run { (a,b) =>
      a.get("cfcType") must matchSimpleScalar("A")
      b.get("cfcType") must matchSimpleScalar("B")
    }

    "call the getter method of a property if one is available" in run { (a, b) =>
      a.get("y") must matchSimpleScalar("getterY")
    }

    "call the inherited getter method of a property if one is available" in run { (a,b) =>
      b.get("y") must matchSimpleScalar("inheritedGetterY")
    }

    "get the size of a component" in run { (a, b) =>
      a.size() must be equalTo 3
    }

    "get the size of an extended component" in run { (a,b) =>
      b.size() must be equalTo 5
    }

    "get the keys of a component" in run { (a, b) =>
      a.keys() must matchSetOfSimpleScalars(Set("cfcType", "x", "y"))
    }

    "get the keys of an extended component" in run { (a,b) =>
      b.keys() must matchSetOfSimpleScalars(Set("cfcType","x","y","s","t"))
    }

    "get the values of a component" in run { (a,b) =>
      a.values() must matchSetOfSimpleScalars(Set("A","valueOfX", "getterY"))
    }

    "get the values of an extended component" in run { (a,b) =>
      b.values() must matchSetOfSimpleScalars(Set("B","inheritedValueOfX", "inheritedGetterY", "valueOfS", "valueOfT"))
    }

    "return whether a component is empty" in run { (a,b) =>
      a.isEmpty must be equalTo false
    }

    "return whether an extended component is empty" in run { (a,b) =>
      b.isEmpty must be equalTo false
    }

    "call a method on a component" in run { (a, b) =>
      a.get("f") must matchTemplateMethodModelEx
      a.get("f").exec(List()) must matchSimpleScalar("resultOfF")
    }

    "call an inherited method on a component" in run { (a,b) =>
      b.get("f") must matchTemplateMethodModelEx
      b.get("f").exec(List()) must matchSimpleScalar("inheritedResultOfF")
    }
  }
}
