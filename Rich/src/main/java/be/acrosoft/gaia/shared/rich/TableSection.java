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

import java.util.ArrayList;
import java.util.List;

/**
 * TableSection.
 */
public class TableSection extends Section
{
  /**
   * Single table cell.
   */
  public static class Cell
  {
    private int _spanY;
    private double _width;
    private VerticalAlignment _align;
    private BorderStyle _leftBorder;
    private BorderStyle _rightBorder;
    private BorderStyle _topBorder;
    private BorderStyle _bottomBorder;
    
    private List<Paragraph> _content;
    
    /**
     * Create a new Cell.
     */
    public Cell()
    {
      _content=new ArrayList<>();
      _align=VerticalAlignment.TOP;
      _spanY=1;
      _width=0;
      _leftBorder=new BorderStyle();
      _rightBorder=new BorderStyle();
      _topBorder=new BorderStyle();
      _bottomBorder=new BorderStyle();
    }
    
    /**
     * Get list of paragraphs in that cell.
     * @return all paragraphs in the cell.
     */
    public List<Paragraph> getContent()
    {
      return _content;
    }
    
    /**
     * Get the number of vertical span of this cell.
     * @return cell vertical span.
     */
    public int getSpanY()
    {
      return _spanY;
    }
    
    /**
     * Set the vertical span.
     * @param spanY vertical span.
     */
    public void setSpanY(int spanY)
    {
      _spanY=spanY;
    }
    
    /**
     * Get the cell width, in points, or 0 if not available.
     * @return cell width in points.
     */
    public double getWidth()
    {
      return _width;
    }
    
    /**
     * Set the cell width, in points.
     * @param width new cell with, or 0 if not available.
     */
    public void setWidth(double width)
    {
      _width=width;
    }
    
    /**
     * Get cell vertical alignment.
     * @return vertical alignment.
     */
    public VerticalAlignment getVerticalAlignment()
    {
      return _align;
    }
    
    /**
     * Set cell vertical alignment.
     * @param align vertical alignment.
     */
    public void setVerticalAlignment(VerticalAlignment align)
    {
      _align=align;
    }

    /**
     * Get left border.
     * @return left border.
     */
    public BorderStyle getLeftBorder()
    {
      return _leftBorder;
    }

    /**
     * Set left border.
     * @param leftBorder left border.
     */
    public void setLeftBorder(BorderStyle leftBorder)
    {
      _leftBorder = leftBorder;
    }

    /**
     * Get right border.
     * @return right border.
     */
    public BorderStyle getRightBorder()
    {
      return _rightBorder;
    }

    /**
     * Set right border.
     * @param rightBorder right border.
     */
    public void setRightBorder(BorderStyle rightBorder)
    {
      _rightBorder = rightBorder;
    }

    /**
     * Get top border.
     * @return top border.
     */
    public BorderStyle getTopBorder()
    {
      return _topBorder;
    }

    /**
     * Set top border.
     * @param topBorder top border.
     */
    public void setTopBorder(BorderStyle topBorder)
    {
      _topBorder = topBorder;
    }

    /**
     * Get bottom border.
     * @return bottom border.
     */
    public BorderStyle getBottomBorder()
    {
      return _bottomBorder;
    }

    /**
     * Set bottom border.
     * @param bottomBorder bottom border.
     */
    public void setBottomBorder(BorderStyle bottomBorder)
    {
      _bottomBorder = bottomBorder;
    }
  }
  
  /**
   * A table row.
   */
  public static class Row
  {
    private double _height;
    private List<Cell> _cells;
    
    /**
     * Create a new Row.
     */
    public Row()
    {
      _height=0;
      _cells=new ArrayList<>();
    }
    
    /**
     * Get all cells in the row.
     * @return row cells.
     */
    public List<Cell> getCells()
    {
      return _cells;
    }
    
    /**
     * Get the row height, or 0 if not available.
     * @return row height.
     */
    public double getHeight()
    {
      return _height;
    }
    
    /**
     * Set the row height.
     * @param height row height, or 0 if not available.
     */
    public void setHeight(double height)
    {
      _height = height;
    }
  }
  
  private List<Row> _rows;
  
  /**
   * Create a new TableSection.
   */
  public TableSection()
  {
    _rows=new ArrayList<>();
  }
  
  /**
   * Get all the rows in the table.
   * @return all rows.
   */
  public List<Row> getRows()
  {
    return _rows;
  }
  
}
