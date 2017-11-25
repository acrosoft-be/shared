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
 * BorderStyle, used within table cells.
 */
public class BorderStyle
{
  /**
   * LineStyle.
   */
  public static enum LineStyle
  {
    /**
     * Line is not present.
     */
    NONE,
    /**
     * Solid line.
     */
    SOLID
  }
  
  private LineStyle _lineStyle;
  private double _lineWidth;
  private RGBColor _lineColor;
  private double _margin;
  
  /**
   * Create a new BorderStyle.
   */
  public BorderStyle()
  {
    _lineStyle=LineStyle.SOLID;
    _lineWidth=0.5;
    setLineColor(new RGBColor(0,0,0));
    _margin=5.4;
  }

  /**
   * Get line style.
   * @return line style.
   */
  public LineStyle getLineStyle()
  {
    return _lineStyle;
  }

  /**
   * Set line style.
   * @param lineStyle line style.
   */
  public void setLineStyle(LineStyle lineStyle)
  {
    _lineStyle = lineStyle;
  }

  /**
   * Get line width in points.
   * @return line width in points.
   */
  public double getLineWidth()
  {
    return _lineWidth;
  }

  /**
   * Set line width in points.
   * @param lineWidth line width in points.
   */
  public void setLineWidth(double lineWidth)
  {
    _lineWidth = lineWidth;
  }

  /**
   * Get margin in points.
   * @return margin in points.
   */
  public double getMargin()
  {
    return _margin;
  }

  /**
   * Set margin in points.
   * @param margin margin in points.
   */
  public void setMargin(double margin)
  {
    _margin = margin;
  }

  /**
   * Set line color.
   * @return line color.
   */
  public RGBColor getLineColor()
  {
    return _lineColor;
  }

  /**
   * Set line color.
   * @param lineColor line color.
   */
  public void setLineColor(RGBColor lineColor)
  {
    _lineColor = lineColor;
  }
  
  
}
