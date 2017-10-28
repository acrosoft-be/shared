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
package be.acrosoft.gaia.shared.scheduler;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages.
 */
class Messages
{
  private static final String BUNDLE_NAME="be.acrosoft.gaia.shared.scheduler.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE=ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages()
  {
  }

  /**
   * Get localized string.
   * @param key string key.
   * @param params parameters.
   * @return localized string.
   */
  public static String getString(String key,Object...params)
  {
    try
    {
      return String.format(RESOURCE_BUNDLE.getString(key),params);
    }
    catch(MissingResourceException e)
    {
      return '!'+key+'!';
    }
  }
}
