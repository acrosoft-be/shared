package be.acrosoft.gaia.shared.rich;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Paragraph.
 */
public class Paragraph implements Iterable<Section>
{
  private ParagraphStyle _style;
  private List<Section> _sections;
  
  /**
   * Create a new Paragraph.
   */
  public Paragraph()
  {
    setStyle(new ParagraphStyle());
    _sections=new ArrayList<Section>();
  }

  /**
   * Set the style.
   * @param style The style to set.
   */
  public void setStyle(ParagraphStyle style)
  {
    _style=style;
  }

  /**
   * Get the style.
   * @return Returns the style.
   */
  public ParagraphStyle getStyle()
  {
    return _style;
  }
  
  /**
   * Get the sections.
   * @return the sections.
   */
  public List<Section> getSections()
  {
    return _sections;
  }

  @Override
  public Iterator<Section> iterator()
  {
    return getSections().iterator();
  }
  
  
}
