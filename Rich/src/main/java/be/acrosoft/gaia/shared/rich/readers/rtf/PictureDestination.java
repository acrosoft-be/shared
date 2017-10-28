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

import be.acrosoft.gaia.shared.rich.ImageSection;


/**
 * PictureDestination.
 */
public class PictureDestination extends AbstractDestination
{
  private StringBuffer _buffer;
  private ImageSection _section;
  private double _scaleX;
  private double _scaleY;
  private double _width;
  private double _height;
  
  /**
   * Create a new PictureDestination.
   * @param section section to create.
   */
  public PictureDestination(ImageSection section)
  {
    _section=section;
  }
  
  private static class PictureUIDDestination extends AbstractDestination
  {
  }
  
  @Override
  public void write(String s,Context context,Global global)
  {
    _buffer.append(s);
  }

  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    /*if(parameter==null)
      System.out.println(code);
    else
      System.out.println(code+parameter);*/
    
    if(code.equals("blipuid")) //$NON-NLS-1$
      context.setDestination(new PictureUIDDestination());
    else if(code.equals("picwgoal")) //$NON-NLS-1$
      _width=Integer.parseInt(parameter)/20.0;
    else if(code.equals("pichgoal")) //$NON-NLS-1$
      _height=Integer.parseInt(parameter)/20.0;
    else if(code.equals("picscalex")) //$NON-NLS-1$
      _scaleX=Integer.parseInt(parameter)/100.0;
    else if(code.equals("picscaley")) //$NON-NLS-1$
      _scaleY=Integer.parseInt(parameter)/100.0;
    else
      super.control(code,parameter,context,global);
    
  }
  
  @Override
  public void enter(Context context,Global global)
  {
    _buffer=new StringBuffer();
    _scaleX=100;
    _scaleY=100;
  }
  
  private int fromHex(char b)
  {
    if(b>='0' && b<='9') return b-'0';
    if(b>='a' && b<='f') return b-'a'+10;
    if(b>='A' && b<='F') return b-'A'+10;
    throw new RuntimeException("Illegal hex character "+b); //$NON-NLS-1$
  }

  @Override
  public void leave(Context context,Global global)
  {
    if((_buffer.length()&1)!=0)
      throw new RuntimeException("Invalid image data"); //$NON-NLS-1$
    byte[] data=new byte[_buffer.length()>>1];
    for(int i=0;i<data.length;i++)
    {
      char c1=_buffer.charAt(i<<1);
      char c2=_buffer.charAt((i<<1)+1);
      byte b=(byte)((fromHex(c1)<<4)+fromHex(c2));
      data[i]=b;
    }
    
    /*ByteArrayInputStream bis=new ByteArrayInputStream(data);
    ImageLoader loader=new ImageLoader();
    loader.load(bis);*/
    _section.setData(data);
    _section.setWidth(_width*_scaleX);
    _section.setHeight(_height*_scaleY);
  }

}
