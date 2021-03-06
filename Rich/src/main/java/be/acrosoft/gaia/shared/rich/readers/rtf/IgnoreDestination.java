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
package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * IgnoreDestination.
 */
public class IgnoreDestination extends AbstractDestination
{
  @Override
  public void write(String s,Context context,Global global)
  {
  }

  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
  }
  
  @Override
  public void enter(Context context,Global global)
  {
  }

  @Override
  public void leave(Context context,Global global)
  {
  }
  
  @Override
  public boolean supportsExtended(String code,Context context,Global global)
  {
    return true;
  }
}
