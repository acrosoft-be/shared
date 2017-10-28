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
