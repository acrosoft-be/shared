/**
 * Copyright Acropolis Software SPRL (https://www.acrosoft.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

/**
 * StringExpanderTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class StringExpanderTest
{
  @Test
  public void noExpansionTest()
  {
    String str="Content";
    str=StringExpander.expand(str);
    assertEquals("Content",str);

    str="";
    str=StringExpander.expand(str);
    assertEquals("",str);
  }

  @Test
  public void escapingTest()
  {
    String str="Left$$Right";
    str=StringExpander.expand(str);
    assertEquals("Left$Right",str);

    str="$$Right";
    str=StringExpander.expand(str);
    assertEquals("$Right",str);

    str="Left$$";
    str=StringExpander.expand(str);
    assertEquals("Left$",str);

    str="$$";
    str=StringExpander.expand(str);
    assertEquals("$",str);

    str="$$$$";
    str=StringExpander.expand(str);
    assertEquals("$$",str);
  }
  
  @Test
  public void syntaxTest()
  {
    String str="Left$(Right";
    str=StringExpander.expand(str);
    assertEquals("Left$(Right",str);

    str="Left$Right";
    str=StringExpander.expand(str);
    assertEquals("Left$Right",str);

    str="Left$";
    str=StringExpander.expand(str);
    assertEquals("Left$",str);

    str="Left$(";
    str=StringExpander.expand(str);
    assertEquals("Left$(",str);

    str="$";
    str=StringExpander.expand(str);
    assertEquals("$",str);

    str="$(";
    str=StringExpander.expand(str);
    assertEquals("$(",str);

    str="$Right";
    str=StringExpander.expand(str);
    assertEquals("$Right",str);

    str="$(Right";
    str=StringExpander.expand(str);
    assertEquals("$(Right",str);

    str="$$(Right";
    str=StringExpander.expand(str);
    assertEquals("$(Right",str);

    str="$$(";
    str=StringExpander.expand(str);
    assertEquals("$(",str);

    str="$$($";
    str=StringExpander.expand(str);
    assertEquals("$($",str);

    str="$$($(";
    str=StringExpander.expand(str);
    assertEquals("$($(",str);

    str="$$($$";
    str=StringExpander.expand(str);
    assertEquals("$($",str);

    str="$$(($$";
    str=StringExpander.expand(str);
    assertEquals("$(($",str);

    str="$$($$(";
    str=StringExpander.expand(str);
    assertEquals("$($(",str);

    str="$$$";
    str=StringExpander.expand(str);
    assertEquals("$$",str);

    str="$$$(";
    str=StringExpander.expand(str);
    assertEquals("$$(",str);

    str="$$$$(";
    str=StringExpander.expand(str);
    assertEquals("$$(",str);
  }
  
  @Test
  public void expansionTestNoMatch()
  {
    assertEquals("LeftRight",StringExpander.expand("Left$(var)Right",new Properties()));
    assertEquals("LeftRight",StringExpander.expand("Left$()Right",new Properties()));

    assertEquals("Right",StringExpander.expand("$(var)Right",new Properties()));
    assertEquals("Right",StringExpander.expand("$()Right",new Properties()));

    assertEquals("Left",StringExpander.expand("Left$(var)",new Properties()));
    assertEquals("Left",StringExpander.expand("Left$()",new Properties()));

    assertEquals("",StringExpander.expand("$(var)",new Properties()));
    assertEquals("",StringExpander.expand("$()",new Properties()));
  }

  @Test
  public void expansionTestOneMatch()
  {
    Properties prop=new Properties();
    prop.setProperty("var","val");
    assertEquals("LeftvalRight",StringExpander.expand("Left$(var)Right",prop));

    assertEquals("valRight",StringExpander.expand("$(var)Right",prop));

    assertEquals("Leftval",StringExpander.expand("Left$(var)",prop));

    assertEquals("val",StringExpander.expand("$(var)",prop));
  }

  @Test
  public void expansionTestTwoMatches()
  {
    Properties prop=new Properties();
    prop.setProperty("var1","val1");
    prop.setProperty("var2","val2");
    assertEquals("Leftval1val2Right",StringExpander.expand("Left$(var1)$(var2)Right",prop));
    assertEquals("val1val2Right",StringExpander.expand("$(var1)$(var2)Right",prop));
    assertEquals("Leftval1val2",StringExpander.expand("Left$(var1)$(var2)",prop));
    assertEquals("val1val2",StringExpander.expand("$(var1)$(var2)",prop));
    
    assertEquals("Leftval1Centerval2Right",StringExpander.expand("Left$(var1)Center$(var2)Right",prop));
    assertEquals("val1Centerval2Right",StringExpander.expand("$(var1)Center$(var2)Right",prop));
    assertEquals("Leftval1Centerval2",StringExpander.expand("Left$(var1)Center$(var2)",prop));
    assertEquals("val1Centerval2",StringExpander.expand("$(var1)Center$(var2)",prop));
  }
  
  @Test
  public void nonRecursiveTest()
  {
    Properties prop=new Properties();
    prop.setProperty("var1","$(var2)");
    prop.setProperty("var2","$(val)");
    assertEquals("$(var2)",StringExpander.expand("$(var1)",prop));
  }
}
