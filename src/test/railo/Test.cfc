component accessors="true" {

  property name="property1" type="string";
  property name="property2" type="string";

  function init() {
    setProperty1("property1")
    return this;
  }

  variables.property2 = "property2";
}