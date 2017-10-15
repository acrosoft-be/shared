package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * DebugTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class DebugTest
{
  @Test
  public void testEnv()
  {
    Debug.override(null);
    System.setProperty("gaia.debug","true");
    assertTrue(Debug.isDebug());
    Debug.override(null);
    System.getProperties().remove("gaia.debug");
    assertFalse(Debug.isDebug());
  }
  
  @Test
  public void testOverride()
  {
    System.setProperty("gaia.debug","true");
    Debug.override(true);
    assertTrue(Debug.isDebug());
    Debug.override(false);
    assertFalse(Debug.isDebug());
    Debug.override(null);
    assertTrue(Debug.isDebug());
    
    System.getProperties().remove("gaia.debug");
    Debug.override(true);
    assertTrue(Debug.isDebug());
    Debug.override(false);
    assertFalse(Debug.isDebug());
    Debug.override(null);
    assertFalse(Debug.isDebug());
  }
}
