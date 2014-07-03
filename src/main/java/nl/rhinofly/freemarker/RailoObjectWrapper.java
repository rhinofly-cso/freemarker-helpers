package nl.rhinofly.freemarker;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import railo.runtime.Component;
import java.sql.ResultSet;

public class RailoObjectWrapper extends DefaultObjectWrapper {
  public TemplateModel wrap(Object obj) throws TemplateModelException {
    if (obj instanceof Component) {
      return new ComponentModel((Component) obj, this);
    } else if (obj instanceof ResultSet) {
      return new ResultSetModel((ResultSet) obj, this);
    } else {
      return super.wrap(obj);
    }
  }
}