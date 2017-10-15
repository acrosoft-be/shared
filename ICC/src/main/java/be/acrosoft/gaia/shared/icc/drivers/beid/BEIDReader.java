package be.acrosoft.gaia.shared.icc.drivers.beid;

import be.acrosoft.gaia.shared.icc.CardReader;
import be.acrosoft.gaia.shared.icc.CardReaderDriver;
import be.acrosoft.gaia.shared.icc.CardReaderListener;
import be.acrosoft.gaia.shared.icc.CardReaderMonitor;

/**
 * BEIDReader.
 */
public class BEIDReader implements CardReader
{
  private CardReaderDriver _driver;
  private ReaderThread _th;
  
  /**
   * Create a new BEIDReader.
   * @param driver driver.
   */
  public BEIDReader(CardReaderDriver driver)
  {
    _driver=driver;
    _th=new ReaderThread(this);
  }

  @Override
  public CardReaderDriver getDriver()
  {
    return _driver;
  }

  @Override
  public void readIDData(CardReaderListener listener,boolean idOnly)
  {
    _th.fetchData(listener,idOnly);
  }

  @Override
  public CardReaderMonitor getMonitor()
  {
    return _th;
  }

  /**
   * Dispose the reader.
   */
  public void dispose()
  {
    _th.dispose();
  }

}
