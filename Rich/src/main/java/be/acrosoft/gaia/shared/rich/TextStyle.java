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
 * SectionStyle.
 */
public class TextStyle extends Style implements Cloneable
{
  private String _fontName;
  private String _fontFamily;
  private Double _fontSize;
  private Boolean _bold;
  private Boolean _italic;

  private Boolean _underline;
  private RGBColor _foreGround;
  private RGBColor _backGround;
  
  /**
   * Create a new TextStyle.
   */
  public TextStyle()
  {
    _fontName=null;
    _fontFamily=null;
    _fontSize=null;
    _bold=null;
    _underline=null;
    _italic=null;
    _foreGround=null;
    _backGround=null;
  }
  
  @Override
  public TextStyle clone()
  {
    try
    {
      return (TextStyle)super.clone();
    }
    catch(CloneNotSupportedException ex)
    {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * Apply the given text style to this one by copying all defined properties.
   * @param other other text style.
   */
  public void apply(TextStyle other)
  {
    applyFontFamily(other);
    applyFontName(other);
    applyFontSize(other);
    applyBold(other);
    applyUnderline(other);
    applyItalic(other);
    applyForeGround(other);
    applyBackGround(other);
  }
  
  @Override
  public boolean equals(Object o)
  {
    if(o==null) return false;
    if(!(o instanceof TextStyle)) return false;
    TextStyle t=(TextStyle)o;
    return getFontName().equals(t.getFontName())&&
      getFontFamily().equals(t.getFontFamily())&&
      getFontSize()==t.getFontSize()&&
      isBold()==t.isBold()&&
      isItalic()==t.isItalic()&&
      isUnderline()==t.isUnderline()&&
      getForeGround().equals(t.getForeGround())&&
      getBackGround().equals(t.getBackGround());
  }
  
  @Override
  public int hashCode()
  {
    return getFontName().hashCode()^
    getFontFamily().hashCode()^
    getForeGround().hashCode()^
    getBackGround().hashCode()^
      (int)Double.doubleToRawLongBits(getFontSize());
  }

  /**
   * Set the fontName.
   * @param fontName The fontName to set.
   */
  public void setFontName(String fontName)
  {
    _fontName=fontName;
  }

  /**
   * Apply the font name.
   * @param other style to apply font name from.
   */
  public void applyFontName(TextStyle other)
  {
    if(other._fontName!=null)
      _fontName=other.getFontName();
  }
  
  /**
   * Get the fontName.
   * @return Returns the fontName.
   */
  public String getFontName()
  {
    if(_fontName==null) return ""; //$NON-NLS-1$
    return _fontName;
  }

  /**
   * Set the fontFamily.
   * @param fontFamily The fontFamily to set.
   */
  public void setFontFamily(String fontFamily)
  {
    _fontFamily=fontFamily;
  }

  /**
   * Apply the font family.
   * @param other style to apply font family from.
   */
  public void applyFontFamily(TextStyle other)
  {
    if(other._fontFamily!=null)
      _fontFamily=other.getFontFamily();
  }
  
  /**
   * Get the fontFamily.
   * @return Returns the fontFamily.
   */
  public String getFontFamily()
  {
    if(_fontFamily==null) return ""; //$NON-NLS-1$
    return _fontFamily;
  }

  /**
   * Set the fontSize.
   * @param fontSize The fontSize to set.
   */
  public void setFontSize(double fontSize)
  {
    _fontSize=fontSize;
  }

  /**
   * Apply the font size.
   * @param other style to apply font size from.
   */
  public void applyFontSize(TextStyle other)
  {
    if(other._fontSize!=null)
      _fontSize=other.getFontSize();
  }
  
  /**
   * Get the fontSize.
   * @return Returns the fontSize.
   */
  public double getFontSize()
  {
    if(_fontSize==null) return 10;
    return _fontSize;
  }

  /**
   * Set the bold.
   * @param bold The bold to set.
   */
  public void setBold(boolean bold)
  {
    _bold=bold;
  }

  /**
   * Set bold if other style defines it.
   * @param other other style.
   */
  public void applyBold(TextStyle other)
  {
    if(other._bold!=null)
      _bold=other.isBold();
  }
  
  /**
   * Get the bold.
   * @return Returns the bold.
   */
  public boolean isBold()
  {
    if(_bold==null) return false;
    return _bold;
  }

  /**
   * Set the underline.
   * @param underline The underline to set.
   */
  public void setUnderline(boolean underline)
  {
    _underline=underline;
  }

  /**
   * Set underline if other style defines it.
   * @param other other style.
   */
  public void applyUnderline(TextStyle other)
  {
    if(other._underline!=null)
      _underline=other.isUnderline();
  }
  
  /**
   * Get the underline.
   * @return Returns the underline.
   */
  public boolean isUnderline()
  {
    if(_underline==null) return false;
    return _underline;
  }

  /**
   * Set the italic.
   * @param italic The italic to set.
   */
  public void setItalic(boolean italic)
  {
    _italic=italic;
  }
  
  /**
   * Set italic if other style defines.
   * @param other other style.
   */
  public void applyItalic(TextStyle other)
  {
    if(other._italic!=null)
      _italic=other.isItalic();
  }

  /**
   * Get the italic.
   * @return Returns the italic.
   */
  public boolean isItalic()
  {
    if(_italic==null) return false;
    return _italic;
  }

  /**
   * Set the foreGround.
   * @param foreGround The foreGround to set.
   */
  public void setForeGround(RGBColor foreGround)
  {
    _foreGround=foreGround;
  }

  /**
   * Set foreground of other if defined.
   * @param other other style.
   */
  public void applyForeGround(TextStyle other)
  {
    if(other._foreGround!=null)
      _foreGround=other.getForeGround();
  }
  
  /**
   * Get the foreGround.
   * @return Returns the foreGround.
   */
  public RGBColor getForeGround()
  {
    if(_foreGround==null) return RGBColor.BLACK;
    return _foreGround;
  }

  /**
   * Set the backGround.
   * @param backGround The backGround to set.
   */
  public void setBackGround(RGBColor backGround)
  {
    _backGround=backGround;
  }
  
  /**
   * Set background of other if defined.
   * @param other other style.
   */
  public void applyBackGround(TextStyle other)
  {
    if(other._backGround!=null)
      _backGround=other.getBackGround();
  }
  

  /**
   * Get the backGround.
   * @return Returns the backGround.
   */
  public RGBColor getBackGround()
  {
    if(_backGround==null) return RGBColor.WHITE;
    return _backGround;
  }
}
