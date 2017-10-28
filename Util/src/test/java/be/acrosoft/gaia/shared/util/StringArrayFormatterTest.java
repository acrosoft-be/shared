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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * StringArrayFormatterTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class StringArrayFormatterTest
{

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.StringArrayFormatter#encode(java.lang.String[])}.
   */
  @Test
  public void testEncode()
  {
    assertEquals("",StringArrayFormatter.encode(new String[] {}));
    assertEquals("\"One\",\"Two\"",StringArrayFormatter.encode(new String[] {"One","Two"}));
    assertEquals("\"One\"",StringArrayFormatter.encode(new String[] {"One"}));
    assertEquals("\"\"",StringArrayFormatter.encode(new String[] {""}));
    assertEquals("\"\\\"\"",StringArrayFormatter.encode(new String[] {"\""}));
  }

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.StringArrayFormatter#decode(java.lang.String)}.
   */
  @Test
  public void testDecode()
  {
    assertArrayEquals(new String[] {},StringArrayFormatter.decode(""));
    assertArrayEquals(new String[] {"One","Two"},StringArrayFormatter.decode("\"One\",\"Two\""));
    assertArrayEquals(new String[] {"One"},StringArrayFormatter.decode("\"One\""));
    assertArrayEquals(new String[] {""},StringArrayFormatter.decode("\"\""));
    assertArrayEquals(new String[] {"\""},StringArrayFormatter.decode("\"\\\"\""));
    
  }

}
