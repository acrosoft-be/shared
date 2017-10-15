package be.acrosoft.gaia.shared.rich;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RichDocument.
 */
public class RichDocument implements Iterable<Paragraph>
{
  private DocumentMeta _meta;
  private DocumentStyle _style;
  private List<Paragraph> _paragraphs;
  
  /**
   * Create a new RichDocument.
   */
  public RichDocument()
  {
    setMeta(new DocumentMeta());
    setStyle(new DocumentStyle());
    _paragraphs=new ArrayList<Paragraph>();
  }

  /**
   * Set the meta.
   * @param meta The meta to set.
   */
  public void setMeta(DocumentMeta meta)
  {
    _meta=meta;
  }

  /**
   * Get the meta.
   * @return Returns the meta.
   */
  public DocumentMeta getMeta()
  {
    return _meta;
  }

  /**
   * Set the style.
   * @param style The style to set.
   */
  public void setStyle(DocumentStyle style)
  {
    _style=style;
  }

  /**
   * Get the style.
   * @return Returns the style.
   */
  public DocumentStyle getStyle()
  {
    return _style;
  }
  
  /**
   * Get the paragraphs.
   * @return the paragraphs.
   */
  public List<Paragraph> getParagraphs()
  {
    return _paragraphs;
  }

  @Override
  public Iterator<Paragraph> iterator()
  {
    return getParagraphs().iterator();
  }
  

}
