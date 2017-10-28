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
 * DocumentStyle.
 */
public class DocumentStyle extends Style
{
  private TextStyle _defaultTextStyle;
  private ParagraphStyle _defaultParagraphStyle;
  
  /**
   * Create a new DocumentStyle.
   */
  public DocumentStyle()
  {
    setDefaultTextStyle(new TextStyle());
    setDefaultParagraphStyle(new ParagraphStyle());
  }

  @Override
  public DocumentStyle clone()
  {
    DocumentStyle ans=new DocumentStyle();
    ans._defaultParagraphStyle=_defaultParagraphStyle.clone();
    ans._defaultTextStyle=_defaultTextStyle.clone();
    return ans;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if(o==null) return false;
    if(!(o instanceof DocumentStyle)) return false;
    DocumentStyle d=(DocumentStyle)o;
    return _defaultTextStyle.equals(d._defaultTextStyle) && _defaultParagraphStyle.equals(d._defaultParagraphStyle);
  }
  
  @Override
  public int hashCode()
  {
    return _defaultTextStyle.hashCode()^_defaultParagraphStyle.hashCode();
  }
  
  /**
   * Set the defaultTextStyle.
   * @param defaultTextStyle The defaultTextStyle to set.
   */
  public void setDefaultTextStyle(TextStyle defaultTextStyle)
  {
    _defaultTextStyle=defaultTextStyle;
  }

  /**
   * Get the defaultTextStyle.
   * @return Returns the defaultTextStyle.
   */
  public TextStyle getDefaultTextStyle()
  {
    return _defaultTextStyle;
  }

  /**
   * Set the defaultParagraphStyle.
   * @param defaultParagraphStyle The defaultParagraphStyle to set.
   */
  public void setDefaultParagraphStyle(ParagraphStyle defaultParagraphStyle)
  {
    _defaultParagraphStyle=defaultParagraphStyle;
  }

  /**
   * Get the defaultParagraphStyle.
   * @return Returns the defaultParagraphStyle.
   */
  public ParagraphStyle getDefaultParagraphStyle()
  {
    return _defaultParagraphStyle;
  }
}
