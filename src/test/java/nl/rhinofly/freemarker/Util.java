package nl.rhinofly.freemarker;

import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

class Util {
  public static Collection.Key createKey (String key) {
    return KeyImpl.init(key);
  }
}
