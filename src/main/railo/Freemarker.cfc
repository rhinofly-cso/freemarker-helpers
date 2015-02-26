component {
  
  public function init(configuration) {
    if (structKeyExists(arguments, "configuration"))
      variables._configuration = arguments.configuration;
    else
      variables._configuration = getDefaultConfiguration();
  
    var logger = createObject("java", "freemarker.log.Logger");
    logger.selectLoggerLibrary(logger.LIBRARY_NONE);
  
    return this;
  }
  
  /**
   * Processes the template and writes the result directly into the output stream.
   *
   * @param template  The template that needs to be processed
   * @param model     The model that will be given to freemarker
   *
   * @return String   The rendered template
   */
  public String function processTemplate(
    required String template,
    required Struct model) {
    
    var freemarkerTemplate = variables._configuration.getTemplate(arguments.template);
    var writer_obj = createObject("java", "java.io.StringWriter").init();
    
    freemarkerTemplate.process(arguments.model, writer_obj);
    
    return writer_obj.toString();
  }
  
  public function getDefaultConfiguration() {
    var c = createObject("java", "freemarker.template.Configuration").init();
    
    c.setOutputEncoding("UTF-8");
    c.setNumberFormat("computer");
    c.setDateFormat("medium");
    c.setTimeFormat("short");
    c.setLocale(createObject("java", "java.util.Locale").init("nl", "NL"));
    
    // set tag syntax to square brackets 
    c.setTagSyntax(2);
    
    c.setObjectWrapper(createObject("java", "nl.rhinofly.freemarker.RailoObjectWrapper"));
    
    c.setTemplateLoader(getClassTemplateLoader("/"));
      
    return c;
  }
  
  public function getClassTemplateLoader(required String prefix) {
   return createObject("java", "freemarker.cache.ClassTemplateLoader")
     .init(structNew().getClass(), prefix);
  }
}
