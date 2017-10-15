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
