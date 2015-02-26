package nl.rhinofly.freemarker

import java.sql.ResultSet
import freemarker.template.TemplateCollectionModel
import freemarker.template.TemplateModelIterator
import scala.util.Try
import java.sql.SQLException
import freemarker.template.TemplateException
import freemarker.template.TemplateModelException
import freemarker.template.TemplateHashModelEx
import freemarker.template.ObjectWrapper
import freemarker.template.SimpleCollection
import scala.collection.JavaConverters._
import java.sql.Clob
import lucee.runtime.exp.PageException
import lucee.runtime.`type`.Query
import java.util.Arrays

class QueryModel(query: Query, wrapper: ObjectWrapper) extends TemplateCollectionModel with TemplateModelIterator {

  import SqlUtils.catchingRailoException

  private lazy val queryIterator = query.getIterator

  def iterator = catchingRailoException {
    this
  }

  def hasNext = catchingRailoException {
    queryIterator.hasNext
  }

  def next = catchingRailoException {
    wrapper.wrap(queryIterator.next)
  }

}

object SqlUtils {
  def catchingRailoException[T](code: => T): T =
    try code
    catch {
      case e @ (_: SQLException | _: PageException) => throw new TemplateModelException(e)
    }
}