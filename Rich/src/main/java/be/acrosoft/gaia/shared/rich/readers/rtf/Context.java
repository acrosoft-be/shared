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
package be.acrosoft.gaia.shared.rich.readers.rtf;

import be.acrosoft.gaia.shared.rich.ParagraphStyle;
import be.acrosoft.gaia.shared.rich.TextStyle;

/**
 * Context.
 */
public class Context implements Cloneable
{
  private Destination _destination;
  private TextStyle _textStyle;
  private ParagraphStyle _paragraphStyle;
  private Global _global;
  private int _level;
  private int _listId;
  
  private Context()
  {
    _textStyle=new TextStyle();
  }
  
  /**
   * Create a new Context.
   * @param global global information.
   */
  public Context(Global global)
  {
    _level=0;
    _listId=0;
    _global=global;
    _textStyle=new TextStyle();
    setParagraphStyle(new ParagraphStyle());
  }
  
  @Override
  public Context clone()
  {
    Context ans=new Context();
    ans._level=_level;
    ans._listId=_listId;
    ans._global=_global;
    ans._destination=getDestination();
    ans._textStyle=getTextStyle().clone();
    ans.setParagraphStyle(getParagraphStyle());
    return ans;
  }
  
  /**
   * Get the destination.
   * @return destination.
   */
  public Destination getDestination()
  {
    return _destination;
  }
  
  /**
   * Set the destination.
   * @param destination destination.
   */
  public void setDestination(Destination destination)
  {
    _destination=destination;
    _destination.enter(this,getGlobal());
  }

  /**
   * Set the textStyle.
   * @param textStyle The textStyle to set.
   */
  public void setTextStyle(TextStyle textStyle)
  {
    _textStyle.apply(textStyle);
  }

  /**
   * Get the textStyle.
   * @return Returns the textStyle.
   */
  public TextStyle getTextStyle()
  {
    return _textStyle;
  }

  /**
   * Set the paragraphStyle.
   * @param paragraphStyle The paragraphStyle to set.
   */
  public void setParagraphStyle(ParagraphStyle paragraphStyle)
  {
    _paragraphStyle=paragraphStyle;
  }

  /**
   * Get the paragraphStyle.
   * @return Returns the paragraphStyle.
   */
  public ParagraphStyle getParagraphStyle()
  {
    return _paragraphStyle;
  }

  /**
   * Get global information.
   * @return global information.
   */
  public Global getGlobal()
  {
    return _global;
  }

  /**
   * Set the level.
   * @param level The level to set.
   */
  public void setLevel(int level)
  {
    _level=level;
  }

  /**
   * Get the level.
   * @return Returns the level.
   */
  public int getLevel()
  {
    return _level;
  }

  /**
   * Set the listId.
   * @param listId The listId to set.
   */
  public void setListId(int listId)
  {
    _listId=listId;
  }

  /**
   * Get the listId.
   * @return Returns the listId.
   */
  public int getListId()
  {
    return _listId;
  }
}
