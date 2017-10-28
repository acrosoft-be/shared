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
 * ListOverrideDestination.
 */
public class ListOverrideDestination extends AbstractDestination
{
  private int _id;
  private int _ls;
  
  @Override
  public void enter(Context context,Global global)
  {
    _id=0;
    _ls=0;
  }
  
  @Override
  public void leave(Context context,Global global)
  {
    global.getListOverrides().put(_ls,_id);
  }
  
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    if(code.equals("listid")) //$NON-NLS-1$
      _id=Integer.parseInt(parameter);
    else if(code.equals("ls")) //$NON-NLS-1$
      _ls=Integer.parseInt(parameter);
    else
      super.control(code,parameter,context,global);
  }
}
