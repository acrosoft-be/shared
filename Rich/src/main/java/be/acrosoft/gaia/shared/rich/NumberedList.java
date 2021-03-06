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
package be.acrosoft.gaia.shared.rich;

/**
 * List.
 */
public class NumberedList
{
  private int _init;
  
  /**
   * Create a new NumberedList.
   */
  public NumberedList()
  {
    _init=1;
  }
  
  /**
   * Set initial value.
   * @param init initial value.
   */
  public void setInitialValue(int init)
  {
    _init=init;
  }
  
  /**
   * Get the initial value.
   * @return initial value.
   */
  public int getInitialValue()
  {
    return _init;
  }

}
