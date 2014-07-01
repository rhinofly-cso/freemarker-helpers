package fly.java.freemarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import freemarker.template.*;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
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

  final private Map<String, Lazy<Object>> members = new HashMap<String, Lazy<Object>> ();

  public ComponentModel(final Component component, final ObjectWrapper objectWrapper)
  {
    super(objectWrapper);

    this.component = component;

    registerCFCType();
    registerProperties ();
  }

  private void registerProperties() {
    Property[] properties = component.getProperties(false);
    for (int i = 0; i < properties.length; i++)
      members.put(properties[i].getName(), getProperty(properties[i].getName()));
  }

  private void registerCFCType() {
    members.put (TYPE_PROPERTY_NAME, new Lazy<Object> () {
      public Object execute() {
        return component.getName();
      }
    });
  }

  private Lazy<Object> getProperty (final String key) {
    final CFMLEngine engine	= CFMLEngineFactory.getInstance();
    final PageContext pc = engine.getThreadPageContext();

    return new Lazy<Object> () {
      public Object execute () {
        try {
          return component.call(pc,"get" + key, new Object[0]);
        } catch (PageException e1) {
          ComponentScope scope = component.getComponentScope();
          return scope.get(KeyImpl.init(key),null);
        }
      }
    };
  }

  /**
   * @inheritDoc
   */
  public TemplateModel get(final String key) throws TemplateModelException {
    if (members.containsKey(key)) {
      return wrap(members.get(key).get());
    } else {
      return null;
    }
  }

  /**
   * @inheritDoc
   */
  public boolean isEmpty() throws TemplateModelException {
    return members.isEmpty();
  }

  /**
   * @inheritDoc
   */
  public TemplateCollectionModel keys() throws TemplateModelException {
    return new SimpleCollection(members.keySet(), getObjectWrapper());
  }

  /**
   * @inheritDoc
   */
  public int size() throws TemplateModelException {
    return members.size();
  }

  /**
   * @inheritDoc
   */
  public TemplateCollectionModel values() throws TemplateModelException {
    final List<TemplateModel> result = new ArrayList<TemplateModel> ();

    for (Lazy value : members.values())
      result.add(wrap(value.get()));

    return new SimpleCollection(result, getObjectWrapper());
  }

  private abstract static class Lazy<T> {
    private boolean executed = false;

    private T value;

    public T get () {
      if (!executed) {
        value = execute();
        executed = true;
      }
      return value;
    }

    public abstract T execute ();
  }
}
