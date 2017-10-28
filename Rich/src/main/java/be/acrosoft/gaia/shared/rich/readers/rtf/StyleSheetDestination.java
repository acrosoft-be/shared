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

import be.acrosoft.gaia.shared.rich.TextStyle;


/**
 * StyleSheetDestination.
 */
public class StyleSheetDestination extends AbstractDestination
{
  private int _index;
  
  @Override
  public void enter(Context context,Global global)
  {
    _index=0;
    TextStyle ts=new TextStyle();
    Font fnt=global.getFonts().get(global.getDefaultFontIndex());
    ts.setFontFamily(fnt.family);
    ts.setFontName(fnt.name);
    global.getStyles().put(_index,ts);
  }
      
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    String param=parameter;
    if(param==null) param="1"; //$NON-NLS-1$

    if(code.equals("s")) //$NON-NLS-1$
    {
      _index=Integer.parseInt(parameter);
      TextStyle ts=new TextStyle();
      Font fnt=global.getFonts().get(global.getDefaultFontIndex());
      ts.setFontFamily(fnt.family);
      ts.setFontName(fnt.name);
      global.getStyles().put(_index,ts);
    }
    if(code.equals("b")) //$NON-NLS-1$
      global.getStyles().get(_index).setBold(param.equals("1")); //$NON-NLS-1$
    else if(code.equals("i")) //$NON-NLS-1$
      global.getStyles().get(_index).setItalic(param.equals("1")); //$NON-NLS-1$
    else if(code.equals("ul")) //$NON-NLS-1$
      global.getStyles().get(_index).setUnderline(true);
    else if(code.equals("ulnone")) //$NON-NLS-1$
      global.getStyles().get(_index).setUnderline(false);
    else if(code.equals("cf")) //$NON-NLS-1$
      global.getStyles().get(_index).setForeGround(global.getColors().get(Integer.parseInt(param)));
    else if(code.equals("fs")) //$NON-NLS-1$
      global.getStyles().get(_index).setFontSize(Integer.parseInt(param)/2.0);
    else if(code.equals("f")) //$NON-NLS-1$
    {
      Font fnt=global.getFonts().get(Integer.parseInt(param));
      global.getStyles().get(_index).setFontFamily(fnt.family);
      global.getStyles().get(_index).setFontName(fnt.name);
    }
    else
      super.control(code,parameter,context,global);
  }

}
