package be.acrosoft.gaia.shared.icc;

import be.acrosoft.gaia.shared.dispatch.Listener;

/**
 * IDCardReaderListener.
 */
public interface CardReaderListener extends Listener<CardReaderListener>
{
  /**
   * The ID Data has been retrieved.
   * @param data ID data.
   */
  public void dataRetreived(IDData data);
  
  /**
   * Error retrieving data.
   * @param th throwable.
   */
  public void errorOccured(Throwable th);
  
}
