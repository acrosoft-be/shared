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
 * ImageSection.
 */
public class ImageSection extends Section
{
  /**
   * The unit used for the image size.
   */
  public static enum Unit
  {
    /**
     * Pixel.
     */
    PIXEL,
    /**
     * Point.
     */
    POINT
  }
  
  private byte[] _data;
  private double _leftOffset;
  private double _topOffset;
  private double _width;
  private double _height;
  private Unit _widthUnit;
  private Unit _heightUnit;
  
  /**
   * Create a new ImageSection.
   */
  public ImageSection()
  {
    this(null);
  }
  
  /**
   * Create a new ImageSection.
   * @param data image data.
   */
  public ImageSection(byte[] data)
  {
    _data=data;
    _leftOffset=0;
    _topOffset=0;
    _width=-1;
    _height=-1;
    _widthUnit=Unit.POINT;
    _heightUnit=Unit.POINT;
  }
  
  
  /**
   * Set the image data.
   * @param data image data.
   */
  public void setData(byte[] data)
  {
    _data=data;
  }
  
  /**
   * Get image data.
   * @return image data.
   */
  public byte[] getData()
  {
    return _data;
  }

  /**
   * Set the width.
   * @param width The width to set.
   * @param unit unit of the width.
   */
  public void setWidth(double width,Unit unit)
  {
    _width=width;
    _widthUnit=unit;
  }

  /**
   * Get the width.
   * @return Returns the width.
   */
  public double getWidth()
  {
    return _width;
  }
  
  /**
   * Get the width unit.
   * @return width unit.
   */
  public Unit getWidthUnit()
  {
    return _widthUnit;
  }

  /**
   * Set the height.
   * @param height The height to set.
   * @param unit unit of the height.
   */
  public void setHeight(double height,Unit unit)
  {
    _height=height;
    _heightUnit=unit;
  }
  
  /**
   * Get the height.
   * @return Returns the height.
   */
  public double getHeight()
  {
    return _height;
  }
  
  /**
   * Get the height unit.
   * @return height unit.
   */
  public Unit getHeightUnit()
  {
    return _heightUnit;
  }
  
  /**
   * Set the left offset for the image, in points.
   * @param leftOffset left offset in points.
   */
  public void setLeftOffset(double leftOffset)
  {
    _leftOffset=leftOffset;
  }
  
  /**
   * Get the left offset, in points.
   * @return left offset.
   */
  public double getLeftOffset()
  {
    return _leftOffset;
  }
  
  /**
   * Set the top offset for the image, in points.
   * @param topOffset top offset.
   */
  public void setTopOffset(double topOffset)
  {
    _topOffset=topOffset;
  }
  
  /**
   * Get the top offset of the image, in points.
   * @return top offset.
   */
  public double getTopOffset()
  {
    return _topOffset;
  }
  
}
