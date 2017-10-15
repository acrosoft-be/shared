package be.acrosoft.gaia.shared.rich;

/**
 * ParagraphStyle.
 */
public class ParagraphStyle extends Style implements Cloneable
{
  /**
   * BulletStyle.
   */
  public static enum BulletStyle
  {
    /**
     * None.
     */
    NONE,
    /**
     * Simple bullet.
     */
    BULLET,
    /**
     * Simple enumerated list.
     */
    LIST_NUMBER,
    /**
     * A,B,C
     */
    LIST_UPPER_ALPHABETIC,
    /**
     * a,b,c
     */
    LIST_LOWER_ALPHABETIC,
    /**
     * I,II,III
     */
    LIST_UPPER_ROMAN,
    /**
     * i,ii,iii
     */
    LIST_LOWER_ROMAN,
  }
  
  private Alignment _alignment;
  private double _indent;
  private double _firstLineIndent;
  private BulletStyle _bulletStyle;
  private String _bulletText;
  private TextStyle _bulletTextStyle;
  private NumberedList _list;
  
  /**
   * Create a new ParagraphStyle.
   */
  public ParagraphStyle()
  {
    _alignment=Alignment.LEFT;
    setBulletStyle(BulletStyle.NONE);
    _bulletText=null;
    //setBulletText("\u00b7"); //$NON-NLS-1$
    setBulletTextStyle(new TextStyle());
    getBulletTextStyle().setFontName("Symbol"); //$NON-NLS-1$
    setIndent(0);
    setFirstLineIndent(0);
  }
  
  @Override
  public boolean equals(Object o)
  {
    if(o==null) return false;
    if(!(o instanceof ParagraphStyle)) return false;
    ParagraphStyle p=(ParagraphStyle)o;
    return _alignment.equals(p._alignment)&&_indent==p._indent;
  }
  
  @Override
  public int hashCode()
  {
    return (int)(_alignment.hashCode()^Double.doubleToRawLongBits(_indent));
  }
  
  @Override
  public ParagraphStyle clone()
  {
    try
    {
      return (ParagraphStyle)super.clone();
    }
    catch(CloneNotSupportedException ex)
    {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * Set the alignment.
   * @param alignment The alignment to set.
   */
  public void setAlignment(Alignment alignment)
  {
    _alignment=alignment;
  }

  /**
   * Get the alignment.
   * @return Returns the alignment.
   */
  public Alignment getAlignment()
  {
    return _alignment;
  }

  /**
   * Set the indent.
   * @param indent The indent to set.
   */
  public void setIndent(double indent)
  {
    _indent=indent;
  }

  /**
   * Get the indent.
   * @return Returns the indent.
   */
  public double getIndent()
  {
    return _indent;
  }

  /**
   * Set the bulletStyle.
   * @param bulletStyle The bulletStyle to set.
   */
  public void setBulletStyle(BulletStyle bulletStyle)
  {
    _bulletStyle=bulletStyle;
  }

  /**
   * Get the bulletStyle.
   * @return Returns the bulletStyle.
   */
  public BulletStyle getBulletStyle()
  {
    return _bulletStyle;
  }

  /**
   * Set the bulletText.
   * @param bulletText The bulletText to set.
   */
  public void setBulletText(String bulletText)
  {
    _bulletText=bulletText;
  }

  /**
   * Get the bulletText.
   * @return Returns the bulletText.
   */
  public String getBulletText()
  {
    if(_bulletText==null) return "\u00b7"; //$NON-NLS-1$
    return _bulletText;
  }
  
  /**
   * Return whether the bullet text is default text.
   * @return true if bullet text is default text.
   */
  public boolean isBulletTextDefault()
  {
    return _bulletText==null;
  }

  /**
   * Set the bulletTextStyle.
   * @param bulletTextStyle The bulletTextStyle to set.
   */
  public void setBulletTextStyle(TextStyle bulletTextStyle)
  {
    _bulletTextStyle=bulletTextStyle;
  }

  /**
   * Get the bulletTextStyle.
   * @return Returns the bulletTextStyle.
   */
  public TextStyle getBulletTextStyle()
  {
    return _bulletTextStyle;
  }

  /**
   * Set the firstLineIndent.
   * @param firstLineIndent The firstLineIndent to set.
   */
  public void setFirstLineIndent(double firstLineIndent)
  {
    _firstLineIndent=firstLineIndent;
  }

  /**
   * Get the firstLineIndent.
   * @return Returns the firstLineIndent.
   */
  public double getFirstLineIndent()
  {
    return _firstLineIndent;
  }

  /**
   * Set the list.
   * @param list The list to set.
   */
  public void setList(NumberedList list)
  {
    _list=list;
  }

  /**
   * Get the list.
   * @return Returns the list.
   */
  public NumberedList getList()
  {
    return _list;
  }


}
