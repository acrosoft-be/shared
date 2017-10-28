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
 * RGBColor.
 */
public class RGBColor
{
  private double _r;
  private double _g;
  private double _b;
  
  /**
   * White.
   */
  public static final RGBColor WHITE=new RGBColor(1,1,1);
  /**
   * Black.
   */
  public static final RGBColor BLACK=new RGBColor(0,0,0);
  
  /**
   * Create a new RGBColor.
   * @param r red, between 0 and 1 inclusive.
   * @param g green, between 0 and 1 inclusive.
   * @param b blue, between 0 and 1 inclusive.
   */
  public RGBColor(double r,double g,double b)
  {
    _r=r;
    _g=g;
    _b=b;
  }
  
  /**
   * Get red.
   * @return red.
   */
  public double red()
  {
    return _r;
  }
  
  /**
   * Get green.
   * @return green.
   */
  public double green()
  {
    return _g;
  }
  
  /**
   * Get blue.
   * @return blue.
   */
  public double blue()
  {
    return _b;
  }
  
  /**
   * Get the color coded as a single packed 24-bits RGB integer.
   * @return 0RGB 24-bits integer.
   */
  public int getRGB()
  {
    int ir=(int)(red()*255);
    int ig=(int)(green()*255);
    int ib=(int)(blue()*255);
    return (ir<<16)+(ig<<8)+ib;
  }
  
  @Override
  public int hashCode()
  {
    return getRGB();
  }
  
  @Override
  public boolean equals(Object other)
  {
    if(other==null) return false;
    if(!(other instanceof RGBColor)) return false;
    RGBColor c=(RGBColor)other;
    return (red()==c.red())&&(green()==c.green())&&(blue()==c.blue());
  }
  
  @Override
  public String toString()
  {
    return "["+red()+","+green()+","+blue()+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }
}
