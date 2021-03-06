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
package be.acrosoft.gaia.shared.util;

import java.util.Arrays;

/**
 * Tuple.
 */
public class Tuple
{
  /**
   * Tuple values.
   */
  public Object[] values;
  
  /**
   * Create a new Tuple.
   * @param objects values.
   */
  public Tuple(Object...objects)
  {
    values=objects;
  }
  
  @Override
  public int hashCode()
  {
    return Arrays.deepHashCode(values);
  }
  
  @Override
  public boolean equals(Object other)
  {
    if(other==null || !(other instanceof Tuple))
    {
      return false;
    }
    return Arrays.deepEquals(values,((Tuple)other).values);
  }
}
