<cfscript>
	logger = "";
	defaultObjectWrapper = "";
	dir = "";
	arguments_arr = arrayNew(1);
	resultSetModelFactory = "";
	templateProxyModelFactory = "";

	logger = createObject("java", "freemarker.log.Logger");
	logger.selectLoggerLibrary(logger.LIBRARY_NONE);


	/* get an instance of configuration */
	variables._configuration = createObject("java", "freemarker.template.Configuration").init();

	/* set the output encoding */
	variables._configuration.setOutputEncoding("UTF-8");
	variables._configuration.setNumberFormat("computer");
	variables._configuration.setDateFormat("medium");
	variables._configuration.setTimeFormat("short");
	variables._configuration.setLocale(createObject("java", "java.util.Locale").init("nl", "NL"));


	/* create an object wrapper and assign it to the configuration */
	objectWrapper = createObject("java", "fly.java.freemarker.RailoObjectWrapper").init();
	objectWrapper.setExposeFields(javacast("boolean", true));
	variables._configuration.setObjectWrapper(objectWrapper);

	/* set tag syntax to square brackets */
	variables._configuration.setTagSyntax(2);

		
	fileTemplateLoaderClass = createObject("java", "freemarker.cache.FileTemplateLoader");
	multiTemplateLoaderClass = createObject("java", "freemarker.cache.MultiTemplateLoader");

	dir = createObject("java", "java.io.File").init(expandPath("."));
	templateLoaders = [];
	arrayAppend(templateLoaders, fileTemplateLoaderClass.init(dir));

	multiTemplateLoader = multiTemplateLoaderClass.init(templateLoaders);
	variables._configuration.setTemplateLoader(multiTemplateLoader);

	model = {};
	query = queryNew("een,twee");
	queryAddRow(query, [[1,2]]);

    foo = createObject("Foo");
    foo.setBar("bar");
    foo.setFoo("property foo");

	dummy = createObject("Dummy");
	dummy.setColor("Red");
	dummy.setNested(foo);
	dummy.setHeight("tall");

	structInsert(model, "ping", "pong");
	structInsert(model, "resultset", query);
	structInsert(model, "dummy", dummy);

	//writeDump(model);abort;

	freemarkerTemplate = variables._configuration.getTemplate("test.ftl");
	freemarkerTemplate.process(model, getPageContext().getResponse().getWriter());
</cfscript>