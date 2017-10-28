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

/**
 * Debug.
 */
public class Debug
{
  private static Boolean _value;
  
  
  /**
   * Check whether the current running installation is a debug one, or a release one.
   * @return true if debug mode is enabled.
   */
  public static boolean isDebug()
  {
    if(_value==null)
    {
      _value=System.getProperty("gaia.debug")!=null; //$NON-NLS-1$
    }
    return _value;
  }
  
  /**
   * Override the default debug value.
   * @param over new debug value, or null if default should be used.
   */
  public static void override(Boolean over)
  {
    _value=over;
  }
}
