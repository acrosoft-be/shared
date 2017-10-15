package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;

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
  }
}
