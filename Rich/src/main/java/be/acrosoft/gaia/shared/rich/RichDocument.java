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
