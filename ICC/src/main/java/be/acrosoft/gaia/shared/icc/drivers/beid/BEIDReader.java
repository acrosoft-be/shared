/**
 * Copyright Acropolis Software SPRL (http://www.acrosoft.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
