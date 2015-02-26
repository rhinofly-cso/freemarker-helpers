package nl.rhinofly.freemarker

import java.util.UUID
import org.qirx.littlespec.Specification
import freemarker.cache.TemplateLoader
import freemarker.template.Configuration
import nl.rhinofly.railosbt.testutils.Cfc
import nl.rhinofly.railosbt.testutils.CfcDefinition
import nl.rhinofly.railosbt.testutils.ShortCastAlias
import nl.rhinofly.railosbt.testutils.arrayToRailoArray
import nl.rhinofly.railosbt.testutils.Struct
import nl.rhinofly.railosbt.testutils.Query
import testUtils.withRailo

object FreemarkerSpec extends Specification {

  class Freemarker(configuration: Option[Configuration] = None)
    extends Cfc("freemarker.Freemarker", configuration.toSeq: _*) {

    def this(configuration: Configuration) = this(Some(configuration))
  }
  object Freemarker extends CfcDefinition("freemarker.Freemarker")

  "Freemarker.cfc should be able to render" - withRailo { implicit context =>

    "with a default configuration" - {
      val freemarker = new Freemarker
      val value = UUID.randomUUID.toString
      val model = Struct("test" -> value)

      val result = freemarker.processTemplate("simple-model.ftl", model)

      result is s"freemarker - $value"
    }

    "with a custom configuration" - {
      val freemarker = {
        val configuration = Freemarker
          .getDefaultConfiguration()
          .as[Configuration]

        val loader = Freemarker
          .getClassTemplateLoader(prefix = "/custom-configuration")
          .as[TemplateLoader]

        configuration.setTemplateLoader(loader)

        new Freemarker(configuration)
      }

      val result = freemarker.processTemplate("simple-model.ftl", Struct())

      result is s"freemarker custom configuration"
    }

    "railo queries" - {
      val freemarker = new Freemarker

      val query =
        Query(
          "column1" -> Seq("1", "2"),
          "column2" -> Seq("3", "4")
        )

      val model = Struct("query" -> query)

      val result = freemarker.processTemplate("query.ftl", model)

      result is """|1-3
                   |2-4
                   |""".stripMargin
    }

    "railo cfc's" - {
      val freemarker = new Freemarker

      val cfc = new Cfc("Test")

      val model = Struct("cfc" -> cfc)

      val result = freemarker.processTemplate("cfc.ftl", model)

      result is """|property1
                   |property2""".stripMargin
    }

    "a model generated with a cfc" - {
      val freemarker = new Freemarker

      val setup = Cfc("Setup")

      val model = setup.getModel()

      val result = freemarker.processTemplate("test.ftl", model)

      result is """|something wacky
                   |inherited function
                   |Red
                   |Exception
                   |Exception
                   |Exception
                   |default
                   |thing
                   |bar
                   |jan
                   |Exception
                   |cfcType ambi height color nested some 
                   |Exception
                   |bar jan property foo Foo 
                   |6
                   |foo ~~~~ bar
                   |property foo
                   |Exception
                   |ambi()
                   |Exception
                   |tall""".stripMargin
    }
  }
}
