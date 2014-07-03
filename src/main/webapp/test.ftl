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
      <td>{dummy.generateSomething()}</td>
      <td>something wacky</td>
      <td>
        [#attempt]
          ${dummy.generateSomething()}
        [#recover]
          Exception
        [/#attempt]
      </td>
    </tr>
    <tr>
      <td>{dummy.inheritedFunction()}</td>
      <td>inherited function</td>
      <td>${dummy.inheritedFunction()}</td>
    </tr>
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
      <td>generateSomething cfcType height nested color prettyJoin some ambi </td>
      <td>
        [#list dummy?keys as key]
          ${key}
        [/#list]
      </td>
    </tr>
    <tr>
      <td>{dummy?values}</td>
      <td>Exception</td>
      <td>
        [#attempt]
          [#list dummy?values as value]
            ${value}
          [/#list]
        [#recover]
          Exception
        [/#attempt]
      </td>
    </tr>
    <tr>
      <td>{dummy.nested?values}</td>
      <td>Exception</td>
      <td>
        [#attempt]
          [#list dummy.nested?values as value]
            ${value}
          [/#list]
        [#recover]
          Exception
        [/#attempt]
      </td>
    </tr>
    <tr>
      <td>{dummy?size}</td>
      <td>8</td>
      <td>${dummy?size}</td>
    </tr>
    <tr>
      <td>{dummy.prettyJoin("foo", "bar")}</td>
      <td>foo ~~~~ bar</td>
      <td>
      [#attempt]
        ${dummy.prettyJoin("foo", "bar")}
      [#recover]
        Exception
      [/#attempt]      
      </td>
    </tr>
    <tr>
      <td>{dummy.nested.foo}</td>
      <td>cfFoo2ecfc1432908334$funcFOO@7c7778cc</td>
      <td>${dummy.nested.foo}</td>
    </tr>
    <tr>
      <td>{dummy.nested.foo()}</td>
      <td>Exception</td>
      <td>
      [#attempt]
        ${dummy.nested.foo()}
      [#recover]
        Exception
      [/#attempt]      
      </td>
    </tr>
    <tr>
      <td>{dummy.ambi}</td>
      <td>cfDummy2ecfc720211312$funcAMBI@298e08d2 </td>
      <td>${dummy.ambi}     
      </td>
    </tr>
    <tr>
      <td>{dummy.ambi()}</td>
      <td>Exception</td>
      <td>
      [#attempt]
        ${dummy.ambi()}     
      [#recover]
        Exception
      [/#attempt]          
      </td>
    </tr>      
    <tr>
      <td>{dummy.height}</td>
      <td>tall</td>
      <td>
      [#attempt]
        ${dummy.height}     
      [#recover]
        Exception
      [/#attempt]          
      </td>
    </tr>      
  </tbody>
</table>

