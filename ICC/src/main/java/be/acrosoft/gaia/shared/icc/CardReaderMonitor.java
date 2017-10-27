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
   * Get the event delegate for event registration.
   * @return event delegate.
   */
  public CardReaderMonitorListener getEventDelegate(); 
}
