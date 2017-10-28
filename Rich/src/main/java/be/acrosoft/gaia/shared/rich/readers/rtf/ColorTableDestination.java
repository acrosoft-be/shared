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
package be.acrosoft.gaia.shared.rich.readers.rtf;

import be.acrosoft.gaia.shared.rich.RGBColor;


/**
 * ColorTableDestination.
 */
public class ColorTableDestination extends AbstractDestination
{
  private int _index;
  private int _red;
  private int _green;
  private int _blue;
  
  @Override
  public void enter(Context context,Global global)
  {
    _index=0;
    _red=0;
    _green=0;
    _blue=0;
  }
    
  @Override
  public void write(String s,Context context,Global global)
  {
    for(int i=0;i<s.length();i++)
    {
      if(s.charAt(i)==';')
      {
        _index++;
        _red=0;
        _green=0;
        _blue=0;
      }
    }
  }

  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    if(code.equals("red")) //$NON-NLS-1$
    {
      _red=Integer.parseInt(parameter);
      global.getColors().put(_index,new RGBColor(_red/255.0,_green/255.0,_blue/255.0));
    }
    else if(code.equals("green")) //$NON-NLS-1$
    {
      _green=Integer.parseInt(parameter);
      global.getColors().put(_index,new RGBColor(_red/255.0,_green/255.0,_blue/255.0));
    }
    else if(code.equals("blue")) //$NON-NLS-1$
    {
      _blue=Integer.parseInt(parameter);
      global.getColors().put(_index,new RGBColor(_red/255.0,_green/255.0,_blue/255.0));
    }
    else
      super.control(code,parameter,context,global);
  }

}
