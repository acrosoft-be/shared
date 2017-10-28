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
