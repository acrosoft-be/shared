/**
 * Copyright Acropolis Software SPRL (https://www.acrosoft.be)
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
