/**
 * Copyright Acropolis Software SPRL (http://www.acrosoft.be)
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

import java.util.GregorianCalendar;

import org.junit.Test;

/**
 * DateToolsTest.
 */
@SuppressWarnings({"javadoc"})
public class DateToolsTest
{
  @Test
  public void testSimple()
  {
    GregorianCalendar birth=new GregorianCalendar(1980,2,6);
    GregorianCalendar against=new GregorianCalendar(1990,5,7);
    assertEquals(10,DateTools.getAge(birth.getTime(),against.getTime()));
  }
  
  @Test
  public void testBeforeInYear()
  {
    GregorianCalendar birth=new GregorianCalendar(1980,2,6);
    GregorianCalendar against=new GregorianCalendar(1990,1,3);
    assertEquals(9,DateTools.getAge(birth.getTime(),against.getTime()));
  }

  @Test
  public void testAtBirthDay()
  {
    GregorianCalendar birth=new GregorianCalendar(1980,2,6);
    GregorianCalendar against=new GregorianCalendar(1990,2,6);
    assertEquals(9,DateTools.getAge(birth.getTime(),against.getTime()));
  }
}
