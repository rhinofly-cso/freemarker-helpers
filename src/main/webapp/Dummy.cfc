component accessors="true"
{
	property name="color" type="string";
	property name="nested" type="Foo";
	property name="some" type="string";

	public String function getSome()
	{
		return "thing";
	}

	public String function generateSomething()
	{
		return "something wacky";
	}

	public String function prettyJoin(required string x, required string y)
	{
		return arguments.x & " ~~~~ " & arguments.y;
	}

}