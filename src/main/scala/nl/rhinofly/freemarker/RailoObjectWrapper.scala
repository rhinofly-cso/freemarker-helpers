package nl.rhinofly.freemarker

import java.sql.ResultSet
import freemarker.template.DefaultObjectWrapper
import lucee.runtime.`type`.Query
import lucee.runtime.Component

class RailoObjectWrapper extends DefaultObjectWrapper {

  override def wrap(obj: AnyRef) =
    obj match {
      case query: Query         => new QueryModel(query, this)
      case component: Component => new ComponentModel(component, this)
      case x                    => super.wrap(x)
    }
}
