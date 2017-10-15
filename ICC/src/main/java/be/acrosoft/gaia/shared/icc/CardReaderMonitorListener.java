package be.acrosoft.gaia.shared.icc;

import be.acrosoft.gaia.shared.dispatch.Listener;

/**
 * IDCardReaderMonitorListener.
 */
public interface CardReaderMonitorListener extends Listener<CardReaderMonitorListener>
{
  /**
   * An ID card has been inserted.
   * @param reader card reader.
   * @param data ID data from card.
   */
  public void cardInserted(CardReader reader,IDData data);
  
  /**
   * An ID card has been removed.
   * @param reader card reader.
   */
  public void cardRemoved(CardReader reader);
}
