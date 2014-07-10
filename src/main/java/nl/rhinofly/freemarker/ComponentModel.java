package nl.rhinofly.freemarker;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import freemarker.template.*;
import railo.loader.engine.CFMLEngine;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.exp.PageException;
import railo.runtime.type.KeyImpl;

/**
 * A wrapper for Railo Components, also known as CFC's
 */
public class ComponentModel extends WrappingTemplateModel implements TemplateHashModelEx
{
  static private final String TYPE_PROPERTY_NAME = "cfcType";

  final private Component component;

  final private PageContext pc;

  final private Map<String, Lazy<TemplateModel>> properties = new HashMap<> ();

  public ComponentModel(final Component component, final CFMLEngineFacade facade, final ObjectWrapper objectWrapper) throws TemplateModelException
  {
    super(objectWrapper);

    this.component = component;

    final CFMLEngine engine = facade.getCFMLEngine();
    pc = engine.getThreadPageContext();
    
    try {
      registerCFCType();
      registerProperties ();
    } catch (PageException e) {
      throw new TemplateModelException(e);
    }
  }

  private void registerProperties() throws PageException {
	  Component current = component;
    do {
      Property[] properties = current.getProperties(false);
      for (int i = 0; i < properties.length; i++)
        this.properties.put(properties[i].getName(), getProperty(properties[i].getName()));

      current = StringUtils.isEmpty(current.getExtends()) ? null : pc.loadComponent(current.getExtends());
    } while (current != null);
  }

  private void registerCFCType() {
    properties.put(TYPE_PROPERTY_NAME, new Lazy<TemplateModel>() {
      public TemplateModel execute() throws TemplateModelException {
        return wrap(component.getName());
      }
    });
  }

  private Lazy<TemplateModel> getProperty (final String key) {
    return new Lazy<TemplateModel> () {
      public TemplateModel execute () throws TemplateModelException {
        try {
          return wrap(component.call(pc, "get" + key, new Object[0]));
        } catch (PageException e1) {
          ComponentScope scope = component.getComponentScope();
          try {
            return wrap(scope.get(KeyImpl.init(key)));
          } catch (PageException e2) {
            throw new TemplateModelException(e2);
          }
        }
      }
    };
  }


  /**
   * @inheritDoc
   */
  public TemplateModel get(final String key) throws TemplateModelException {
    if (properties.containsKey(key)) {
      return properties.get(key).get();
    } else {
      if (component.contains(pc, KeyImpl.init(key))) {
        return new TemplateMethodModelEx() {
          public Object exec(List arguments) throws TemplateModelException {
            try {
              return wrap(component.call(pc, key, arguments.toArray()));
            } catch (PageException e) {
              throw new TemplateModelException(e);
            }
          }
        };
      } else {
        return null;
      }
    }
  }

  /**
   * @inheritDoc
   */
  public boolean isEmpty() throws TemplateModelException {
    return properties.isEmpty();
  }

  /**
   * @inheritDoc
   */
  public TemplateCollectionModel keys() throws TemplateModelException {
    return new SimpleCollection(properties.keySet(), getObjectWrapper());
  }

  /**
   * @inheritDoc
   */
  public int size() throws TemplateModelException {
    return properties.size();
  }

  /**
   * @inheritDoc
   */
  public TemplateCollectionModel values() throws TemplateModelException {
    final List<TemplateModel> result = new ArrayList<> ();

    for (Lazy<TemplateModel> value : properties.values())
      result.add(value.get());

    return new SimpleCollection(result, getObjectWrapper());
  }

  private abstract static class Lazy<T> {
    private boolean executed = false;

    private T value;

    public T get () throws TemplateModelException {
      if (!executed) {
        value = execute();
        executed = true;
      }
      return value;
    }

    public abstract T execute() throws TemplateModelException;
  }
}