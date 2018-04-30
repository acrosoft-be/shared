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
import static org.junit.Assert.assertNull;

import org.junit.Test;


/**
 * CollatorUtilTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class CollatorUtilTest
{
  @Test
  public void testBase()
  {
    assertEquals("",CollatorUtil.cleanup(""));
    assertEquals("abc",CollatorUtil.cleanup("abc"));
    assertEquals("'abc'",CollatorUtil.cleanup("'abc'"));
    assertEquals("%abc%",CollatorUtil.cleanup("%abc%"));
    assertEquals("abc",CollatorUtil.cleanup("àBç"));
    assertEquals("ssu",CollatorUtil.cleanup("\u00DFù"));
    assertEquals("2",CollatorUtil.cleanup("²"));
    assertEquals("tm",CollatorUtil.cleanup("\u2122"));
  }
  
  @Test
  public void testNull()
  {
    assertNull(CollatorUtil.cleanup(null));
  }
  
  @Test
  public void testWhiteSpaces()
  {
    assertEquals(" ",CollatorUtil.cleanup(" "));
    assertEquals("  ",CollatorUtil.cleanup("  "));
    assertEquals(" a b c u ",CollatorUtil.cleanup(" A B ç ù "));
    assertEquals("  ",CollatorUtil.cleanup("\t "));
    assertEquals("                ",CollatorUtil.cleanup("\u0020\u00A0\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000"));
  }
  
  @Test
  public void testNonDisplayable()
  {
    assertEquals("",CollatorUtil.cleanup("\00\07\n\010"));
  }
  
  @Test
  public void testUnmapped()
  {
    String smp=new String(Character.toChars(0x2070E)); //SMP character
    assertEquals("a"+smp+"c",CollatorUtil.cleanup("A"+smp+"ç"));
    
    String ss="\u1E9E"; //Unofficial character
    assertEquals("a"+ss+"c",CollatorUtil.cleanup("A"+ss+"ç"));

    String user="\uE00A"; //User character
    assertEquals("a"+user+"c",CollatorUtil.cleanup("A"+user+"ç"));
  }
  
}
