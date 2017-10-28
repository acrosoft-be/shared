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
package be.acrosoft.gaia.shared.rich;

/**
 * ImageSection.
 */
public class ImageSection extends Section
{
  private byte[] _data;
  private double _width;
  private double _height;
  
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
    _width=-1;
    _height=-1;
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
   */
  public void setWidth(double width)
  {
    _width=width;
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
   * Set the height.
   * @param height The height to set.
   */
  public void setHeight(double height)
  {
    _height=height;
  }

  /**
   * Get the height.
   * @return Returns the height.
   */
  public double getHeight()
  {
    return _height;
  }
  
}
