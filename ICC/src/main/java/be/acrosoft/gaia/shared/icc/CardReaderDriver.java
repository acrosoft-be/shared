package be.acrosoft.gaia.shared.icc;

/**
 * IDCardReaderDriver.
 */
public interface CardReaderDriver
{
  /**
   * Register the driver.
   */
  public void register();
  
  /**
   * Unregister the driver.
   */
  public void unregister();
  
  /**
   * Get the driver's name.
   * @return driver name.
   */
  public String getName();
}
