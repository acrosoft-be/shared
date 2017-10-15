package be.acrosoft.gaia.shared.resources;

import be.acrosoft.gaia.shared.util.GaiaRuntimeException;

/**
 * This exception is thrown when a requested resource cannot be found by the ResourcesProvider.
 */
public class NoSuchResourceException extends GaiaRuntimeException
{
  private static final long serialVersionUID=1L;
  private String _resource;
  
  /**
   * Create a new NoSuchResourceException.
   * @param resource missing resource.
   */
  public NoSuchResourceException(String resource)
  {
    super(resource);
    _resource=resource;
  }
  
  /**
   * Get the missing resource.
   * @return missing resource.
   */
  public String getResource()
  {
    return _resource;
  }
}
