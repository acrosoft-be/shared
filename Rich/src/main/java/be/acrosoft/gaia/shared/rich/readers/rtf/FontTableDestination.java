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
 * FontTableDestination.
 */
public class FontTableDestination extends AbstractDestination
{
  private static class FontDestination extends AbstractDestination
  {
    private Font _font=new Font();

    /**
     * Create a new FontDestination.
     * @param font target font.
     */
    public FontDestination(Font font)
    {
      _font=font;
      _font.name=""; //$NON-NLS-1$
    }

    @Override
    public void write(String s,Context context,Global global)
    {
      if(s.endsWith(";")) s=s.substring(0,s.length()-1); //$NON-NLS-1$
      _font.name+=s;
    }

    @Override
    public void control(String code,String parameter,Context context,Global global)
    {
      if(code.equals("fnil")) //$NON-NLS-1$
        _font.family="nil"; //$NON-NLS-1$
      else if(code.equals("froman")) //$NON-NLS-1$
        _font.family="roman"; //$NON-NLS-1$
      else if(code.equals("fswiss")) //$NON-NLS-1$
        _font.family="swiss"; //$NON-NLS-1$
      else if(code.equals("fmodern")) //$NON-NLS-1$
        _font.family="modern"; //$NON-NLS-1$
      else if(code.equals("fscript")) //$NON-NLS-1$
        _font.family="script"; //$NON-NLS-1$
      else if(code.equals("fdecor")) //$NON-NLS-1$
        _font.family="decor"; //$NON-NLS-1$
      else if(code.equals("ftech")) //$NON-NLS-1$
        _font.family="tech"; //$NON-NLS-1$
      else if(code.equals("fbidi")) //$NON-NLS-1$
        _font.family="bidi"; //$NON-NLS-1$
      else
        super.control(code,parameter,context,global);
    }
  }
  
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    if(code.equals("f")) //$NON-NLS-1$
    {
      int index=Integer.parseInt(parameter);
      Font f=new Font();
      global.getFonts().put(index,f);
      context.setDestination(new FontDestination(f));
    }
    else
      super.control(code,parameter,context,global);
  }

}
