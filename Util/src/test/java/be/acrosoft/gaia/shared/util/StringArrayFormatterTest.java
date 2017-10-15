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
