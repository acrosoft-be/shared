package be.acrosoft.gaia.shared.icc;


/**
 * CardReaderMonitor.
 */
public interface CardReaderMonitor
{
  /**
   * Get the monitored reader.
   * @return monitored reader.
   */
  public CardReader getMonitoredReader();
  
  /**
   * Add a monitoring listener.
   * @param listener listener to add.
   */
  public void addIDCardReaderMonitorListener(CardReaderMonitorListener listener);
  
  /**
   * Remove a monitoring listener.
   * @param listener listener to remove.
   */
  public void removeIDCardReaderMonitorListener(CardReaderMonitorListener listener);
}
