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
