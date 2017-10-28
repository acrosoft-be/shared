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
