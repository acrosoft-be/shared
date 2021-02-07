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
package be.acrosoft.gaia.shared.rich.ext.readers;

import java.util.ArrayList;

/**
 * Nesting.
 */
public class Nesting
{
  private ArrayList<Integer> _current;
  
  /**
   * Create a new Nesting.
   */
  public Nesting()
  {
    _current=new ArrayList<>();
  }
  
  /**
   * Return the next value for the given level.
   * @param level level.
   * @return value.
   */
  public ArrayList<Integer> next(int level)
  {
    int c=0;
    while(_current.size()>level+1) _current.remove(_current.size()-1);
    while(_current.size()<level) _current.add(1);
    if(_current.size()==level+1) c=_current.remove(level);
    c++;
    _current.add(c);
    return new ArrayList<>(_current);
  }
  
  /**
   * Reset the value at the given level.
   * @param level level.
   * @return value.
   */
  public ArrayList<Integer> reset(int level)
  {
    while(_current.size()>level+1) _current.remove(_current.size()-1);
    while(_current.size()<level) _current.add(1);
    if(_current.size()==level+1) _current.remove(level);
    _current.set(level,1);
    return new ArrayList<>(_current);
  }
}
