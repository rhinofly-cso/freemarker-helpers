component {

  function getModel() {

    var model = {};

    var query = queryNew("een,twee");
    queryAddRow(query, [[1,2]]);

    var foo = createObject("Foo");
    foo.setBar("bar");
    foo.setFoo("property foo");

    var dummy = createObject("Dummy");
    dummy.setColor("Red");
    dummy.setNested(foo);
    dummy.setHeight("tall");

    structInsert(model, "ping", "pong");
    structInsert(model, "resultset", query);
    structInsert(model, "dummy", dummy);

    return model;
  }
}