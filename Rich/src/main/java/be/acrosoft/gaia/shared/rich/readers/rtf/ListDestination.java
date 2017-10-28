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

import java.util.ArrayList;
import java.util.List;

import be.acrosoft.gaia.shared.rich.ParagraphStyle;
import be.acrosoft.gaia.shared.rich.ParagraphStyle.BulletStyle;

/**
 * ListDestination.
 */
public class ListDestination extends AbstractDestination
{
  private int _id;
  private List<ParagraphStyle> _levels;
  
  private class ListTemplateDestination extends AbstractDestination
  {
    private class LevelTextDestination extends AbstractDestination
    {
      private String _txt;
      
      @Override
      public void enter(Context context,Global global)
      {
        _txt=""; //$NON-NLS-1$
      }
      
      @Override
      public void write(String text,Context context,Global global)
      {
        for(int i=0;i<text.length();i++)
        {
          char c=text.charAt(i);
          if(c==';')
          {
            _style.setBulletText(_txt);
            break;
          }
          if(c<31)
            _txt=""; //$NON-NLS-1$
          else
            _txt+=c;
        }
      }
    }
    
    private ParagraphStyle _style;
    
    private ListTemplateDestination(ParagraphStyle style)
    {
      _style=style;
    }
    
    @Override
    public void control(String code,String parameter,Context context,Global global)
    {
      if(code.equals("levelnfc")) //$NON-NLS-1$
      {
        int type=Integer.parseInt(parameter);
        switch(type)
        {
          case 0:
            _style.setBulletStyle(BulletStyle.LIST_NUMBER);
            break;
          case 1:
            _style.setBulletStyle(BulletStyle.LIST_UPPER_ROMAN);
            break;
          case 2:
            _style.setBulletStyle(BulletStyle.LIST_LOWER_ROMAN);
            break;
          case 3:
            _style.setBulletStyle(BulletStyle.LIST_UPPER_ALPHABETIC);
            break;
          case 4:
            _style.setBulletStyle(BulletStyle.LIST_LOWER_ALPHABETIC);
            break;
          case 23:
            _style.setBulletStyle(BulletStyle.BULLET);
            break;
          default:
            break;
        }
      }
      else if(code.equals("leveltext")) //$NON-NLS-1$
      {
        context.setDestination(new LevelTextDestination());
      }
      else
        super.control(code,parameter,context,global);
    }
  }
  
  @Override
  public void enter(Context context,Global global)
  {
    _levels=new ArrayList<ParagraphStyle>();
  }
  
  @Override
  public void leave(Context context,Global global)
  {
    global.getLists().put(_id,_levels);
  }
  
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    String param=parameter;
    if(param==null) param="1"; //$NON-NLS-1$

    if(code.equals("listid")) //$NON-NLS-1$
      _id=Integer.parseInt(parameter);
    else if(code.equals("listlevel")) //$NON-NLS-1$
    {
      ParagraphStyle st=new ParagraphStyle();
      _levels.add(st);
      context.setDestination(new ListTemplateDestination(st));
    }
    else    
      super.control(code,parameter,context,global);
  }
}
