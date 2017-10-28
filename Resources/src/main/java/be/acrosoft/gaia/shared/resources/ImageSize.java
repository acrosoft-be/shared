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
package be.acrosoft.gaia.shared.resources;


/**
 * ImageSize.
 */
public enum ImageSize
{
  /**
   * A small image, typically 16x16 pixels in 96DPI or 12 points.
   * The idea is that the SMALL size should roughly match the default text's
   * height for common dialogs and widgets.
   */
  SMALL(16),
  
  /**
   * A medium image, typically 24x24 pixels in 96DPI or 18 points.
   */
  MEDIUM(24),

  /**
   * A large image, typically 32x32 pixels in 96DPI or 24 points.
   * A LARGE image will typically have twice the height of the text for common
   * dialogs and widgets.
   */
  LARGE(32),

  /**
   * A very large image, typically 64x64 pixels in 96DPI or 48 points.
   */
  VERY_LARGE(64);

  private int _size;
  
  private ImageSize(int size)
  {
    _size=size;
  }
  
  /**
   * Get pixel size.
   * @return pixel size.
   */
  public int getSize()
  {
    return _size;
  }
  
}
