package be.acrosoft.gaia.shared.resources;

import be.acrosoft.gaia.shared.resources.AbstractResourcesProvider;
import be.acrosoft.gaia.shared.util.GaiaRuntimeException;

/**
 * Messages.
 */
public class Messages
{
  private static final AbstractResourcesProvider locals=new AbstractResourcesProvider() {};
  
  private Messages()
  {
  }

  /**
   * Get the localized string based on the given key.
   * @param key key.
   * @return localized string.
   * @throws GaiaRuntimeException if the key is not found.
   */
  public static String getString(String key)
  {
    return locals.getString(key);
  }

  /**
   * Get the localized formatted string based on the given key.
   * @param key key.
   * @param objects strings parameters.
   * @return localized string.
   * @throws GaiaRuntimeException if the key is not found.
   */
  public static String getString(String key,Object... objects)
  {
    return locals.getString(key,objects);
  }
}
