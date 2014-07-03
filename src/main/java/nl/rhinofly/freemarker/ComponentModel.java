package nl.rhinofly.freemarker;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import freemarker.template.*;
import freemarker.template.TemplateMethodModelEx;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.component.ComponentLoader;
import railo.runtime.ComponentImpl;
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

  final private Map<String, Lazy<TemplateModel>> attributes = new HashMap<String, Lazy<TemplateModel>> ();

  final private Map<String, Lazy<TemplateModel>> methods = new HashMap<String, Lazy<TemplateModel>> ();

  public ComponentModel(final Component component, final ObjectWrapper objectWrapper) throws TemplateModelException
  {
    super(objectWrapper);

    this.component = component;

    final CFMLEngine engine	= CFMLEngineFactory.getInstance();
    pc = engine.getThreadPageContext();
    
    try {
      registerCFCType();
      registerProperties ();
      registerUDFs();
    } catch (PageException e) {
      System.out.println(e);
      throw new TemplateModelException(e);
    }
  }

  private void registerUDFs() {

  }

  private void registerProperties() throws PageException {
	  System.out.println("register prop");

    Component current = component;
    do {
      System.out.println("current: " + current.getName());
      
      Property[] properties = current.getProperties(false);
      for (int i = 0; i < properties.length; i++) {
        System.out.println("property: "  + properties[i].getName());
        attributes.put(properties[i].getName(), getProperty(properties[i].getName()));
      }

      System.out.println("loading base: " + current.getExtends());
      
      if (!StringUtils.isEmpty(current.getExtends()))
        current = pc.loadComponent(current.getExtends());
      else 
    	  current = null;
      
      System.out.println("new current : " + current);
    } while (current != null);
  }

  private void registerCFCType() {
    attributes.put(TYPE_PROPERTY_NAME, new Lazy<TemplateModel>() {
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
          return wrap(scope.get(KeyImpl.init(key), null));
        }
      }
    };
  }


  /**
   * @inheritDoc
   */
  public TemplateModel get(final String key) throws TemplateModelException {
    System.out.println(key);

    if (attributes.containsKey(key)) {
      return attributes.get(key).get();
    } else {
      boolean cont = component.contains(pc, KeyImpl.init(key));
      System.out.println(cont);

      if (cont) {
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
    return attributes.isEmpty();
  }

  /**
   * @inheritDoc
   */
  public TemplateCollectionModel keys() throws TemplateModelException {
    return new SimpleCollection(attributes.keySet(), getObjectWrapper());
  }

  /**
   * @inheritDoc
   */
  public int size() throws TemplateModelException {
    return attributes.size();
  }

  /**
   * @inheritDoc
   */
  public TemplateCollectionModel values() throws TemplateModelException {
    final List<TemplateModel> result = new ArrayList<TemplateModel> ();

    for (Lazy<TemplateModel> value : attributes.values())
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