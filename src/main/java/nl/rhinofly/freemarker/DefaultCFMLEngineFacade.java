package nl.rhinofly.freemarker;

import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;

public class DefaultCFMLEngineFacade implements CFMLEngineFacade{
  @Override
  public CFMLEngine getCFMLEngine() {
    return CFMLEngineFactory.getInstance();
  }
}
