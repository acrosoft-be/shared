package be.acrosoft.gaia.shared.rich.writers;

import be.acrosoft.gaia.shared.rich.DocumentWriter;
import be.acrosoft.gaia.shared.rich.Paragraph;
import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.Section;
import be.acrosoft.gaia.shared.rich.TextSection;

/**
 * FlatTextWriter
 */
public class FlatTextWriter implements DocumentWriter
{
  private StringBuilder _builder;
  
  /**
   * Create a new FlatTextWriter.
   * @param builder target string builder.
   */
  public FlatTextWriter(StringBuilder builder)
  {
    _builder=builder;
  }

  @Override
  public void write(RichDocument doc)
  {
    for(Paragraph p:doc.getParagraphs())
    {
      if(_builder.length()>0) _builder.append('\n');
      for(Section s:p.getSections())
      {
        if(s instanceof TextSection)
        {
          TextSection text=(TextSection)s;
          _builder.append(text.getText());
        }
      }
    }
  }

  @Override
  public void dispose()
  {
  }

}
