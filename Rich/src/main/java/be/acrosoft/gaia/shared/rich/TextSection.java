package be.acrosoft.gaia.shared.rich;

/**
 * TextSection.
 */
public class TextSection extends Section
{
  private TextStyle _style;
  private String _text;
  
  /**
   * Create a new empty TextSection.
   */
  public TextSection()
  {
    this(""); //$NON-NLS-1$
  }
  
  /**
   * Create a new TextSection.
   * @param text text.
   */
  public TextSection(String text)
  {
    this(text,new TextStyle());
  }
  
  /**
   * Create a new TextSection.
   * @param text text.
   * @param style style.
   */
  public TextSection(String text,TextStyle style)
  {
    _text=text;
    _style=style;
  }

  
  /**
   * Set the style.
   * @param style The style to set.
   */
  public void setStyle(TextStyle style)
  {
    _style=style;
  }
  
  /**
   * Get the style.
   * @return Returns the style.
   */
  public TextStyle getStyle()
  {
    return _style;
  }
  
  /**
   * Set the text.
   * @param text The text to set.
   */
  public void setText(String text)
  {
    _text=text;
  }
  
  /**
   * Get the text.
   * @return Returns the text.
   */
  public String getText()
  {
    return _text;
  }
  
}
