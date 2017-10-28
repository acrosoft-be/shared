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
