package be.acrosoft.gaia.shared.icc;

/**
 * IDCardReader.
 */
public interface CardReader
{
  /**
   * Read the current ID card.
   * @param listener listener to call upon completion.
   * @param idOnly true if only ID should be read instead of the full data.
   */
  public void readIDData(CardReaderListener listener,boolean idOnly);
  
  /**
   * Get the card reader's driver.
   * @return reader driver.
   */
  public CardReaderDriver getDriver();
  
  /**
   * Get the card reader's monitor.
   * @return reader monitor.
   */
  public CardReaderMonitor getMonitor();
}
