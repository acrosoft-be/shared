package be.acrosoft.gaia.shared.icc;

import be.acrosoft.gaia.shared.dispatch.Listener;

/**
 * AbstractCardReaderMonitor.
 */
public abstract class AbstractCardReaderMonitor implements CardReaderMonitor
{
  private CardReaderMonitorListener _listeners;
  private CardReader _reader;
  
  /**
   * Create a new AbstractCardReaderMonitor.
   * @param reader reader.
   */
  public AbstractCardReaderMonitor(CardReader reader)
  {
    _listeners=Listener.groupOf(CardReaderMonitorListener.class);
    _reader=reader;
  }
  
  @Override
  public CardReader getMonitoredReader()
  {
    return _reader;
  }

  /**
   * Fire inserted listeners.
   * @param data id data.
   */
  protected void fireInsertedListeners(IDData data)
  {
    _listeners.cardInserted(_reader,data);
  }
  
  /**
   * Fire removed listeners.
   */
  protected void fireRemovedListeners()
  {
    _listeners.cardRemoved(_reader);
  }
  
  @Override
  public CardReaderMonitorListener getEventDelegate()
  {
    return _listeners;
  }
}
