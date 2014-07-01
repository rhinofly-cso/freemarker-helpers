<pre>
component accessors="true"
{
	property name="color" type="string";

	public String function getSome()
	{
		return "thing";
	}

}
</pre>

<table border="1" cellspacing="0" cellpadding="4">
  <thead>
    <tr>
      <th>Freemarker Code</th>
      <th>Expected</th>
      <th>Actual</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>{dummy.color}</td>
      <td>Red</td>
      <td>${dummy.color}</td>
    </tr>
    <tr>
      <td>{nonexistence.color}</td>
      <td>Exception</td>
      <td>
        [#attempt]
          ${nonexistence.color}
        [#recover]
          Exception
        [/#attempt]
       </td>
    </tr>
    <tr>
      <td>{nonexistence.color!"default"}</td>
      <td>Exception</td>
      <td>
        [#attempt]
          ${nonexistence.color!"default"}
        [#recover]
          Exception
        [/#attempt]
      </td>
    </tr>
    <tr>
      <td>{dummy.nonexistence}</td>
      <td>Exception</td>
      <td>
        [#attempt]
          ${dummy.nonexistence}
        [#recover]
          Exception
        [/#attempt]
      </td>
    </tr>
    <tr>
      <td>{dummy.nonexistence!"default"}</td>
      <td>default</td>
      <td>${dummy.nonexistence!"default"}</td>
    </tr>
    <tr>
      <td>{dummy.some}</td>
      <td>thing</td>
      <td>${dummy.some}</td>
    </tr>
    <tr>
      <td>{dummy.nested.bar}</td>
      <td>bar</td>
      <td>${dummy.nested.bar}</td>
    </tr>
    <tr>
      <td>{dummy.nested.jan}</td>
      <td>jan</td>
      <td>${dummy.nested.jan}</td>
    </tr>
    <tr>
      <td>{dummy.nested.henk}</td>
      <td>Exception</td>
      <td>
        [#attempt]
          ${dummy.nested.henk}
        [#recover]
          Exception
        [/#attempt]
      </td>
    </tr>
    <tr>
      <td>{dummy?keys}</td>
      <td>cfcType nested color some</td>
      <td>
        [#list dummy?keys as key]
          ${key}
        [/#list]
      </td>
    </tr>
    <tr>
      <td>{dummy.nested?values}</td>
      <td>Foo jan bar</td>
      <td>
        [#list dummy.nested?values as value]
          ${value}
        [/#list]      
      </td>
    </tr>
  </tbody>
</table>

