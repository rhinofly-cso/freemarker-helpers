component accessors="true"
{
	property name="height" type="string";
	property name="some" type="string";

	public String function getSome()
	{
		return "parent thing";
	}

	public String function inheritedFunction()
	{
	    return "inherited function";
	}

}