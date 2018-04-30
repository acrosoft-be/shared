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
package be.acrosoft.gaia.shared.util;

import java.text.CollationElementIterator;
import java.text.Collator;
import java.text.RuleBasedCollator;

/**
 * Provides utilities on top of java.text.Collator.
 */
public class CollatorUtil
{
  private static char[] _primaryToChar=null;
  private static int[] _whiteSpace=new int[2];
  private static int[] _tab=new int[2];
  private static Object _lock=new Object();
  
  /**
   * So how does all this actually work? The RuleBasedCollator will return an iteration of components for each character
   * in a string, but the value of these components is arbitrary and does not immediately map to a particular code point.
   * So for instance, the character 'A' could be mapped to 12345, but this 12345 value is meaningless per se. However
   * both 'a' and 'A' would share the same "primary order" value, whereas 'A' would also have secondary or tertiary
   * order values (basically, the uppercase characteristic). So what we do here is that we will scan all the code points
   * and will identify the ones that consist only of a primary order characteristic (i.e. "primitive" characters),
   * building a mapping table of "primary order characteristic value to code point".
   * <p>
   * Strictly speaking, the table we're building gives BMP characters only, whereas the proper way should be to target
   * an int[] instead of a char[], but in practice such "primitive" characters only exist within the BMP range and
   * this gives much better performance when building the resulting string.
   */
  private static void computeTable()
  {
    synchronized(_lock)
    {
      if(_primaryToChar!=null)
        return;
      
      RuleBasedCollator col=(RuleBasedCollator)Collator.getInstance();
      col.setDecomposition(Collator.FULL_DECOMPOSITION);
      col.setStrength(Collator.TERTIARY);
      
      //From experience and testing, no code point above a certain range will be considered "primitive" using our
      //approach, and we really care about start-up speed.
      int maxPoint = 0x02FFF; //Instead of Character.MAX_CODE_POINT
      
      //First we need to find out which is the highest possible primitive value so that we can size our array
      //accordingly. Of course we could be using a Map instead, but an array is much faster.
      
      int maxPrim=0;
      for(int i=0;i<=maxPoint;i++)
      {
        CollationElementIterator it=col.getCollationElementIterator(new String(Character.toChars(i)));
        int v=it.next();
        
        //Will this code point have only one component, and this unique component has only a primary order
        //characteristic with no secondary nor tertiary orders? That's what we call a "primitive" character.
        if(v!=CollationElementIterator.NULLORDER && it.next()==CollationElementIterator.NULLORDER)
        {
          int prim=CollationElementIterator.primaryOrder(v);
          int sec=CollationElementIterator.secondaryOrder(v);
          int tier=CollationElementIterator.tertiaryOrder(v);
          if(prim>maxPrim && sec==0 && tier==0)
          {
            maxPrim=prim;
          }
        }
      }
      
      //Now we can create our table.
      _primaryToChar=new char[maxPrim+1];
      for(int i=0;i<=maxPoint;i++)
      {
        CollationElementIterator it=col.getCollationElementIterator(new String(Character.toChars(i)));
        int v=it.next();

        //Will this code point have only one component, and this unique component has only a primary order
        //characteristic with no secondary nor tertiary orders? That's what we call a "primitive" character.
        if(v!=CollationElementIterator.NULLORDER && it.next()==CollationElementIterator.NULLORDER)
        {
          int prim=CollationElementIterator.primaryOrder(v);
          int sec=CollationElementIterator.secondaryOrder(v);
          int tier=CollationElementIterator.tertiaryOrder(v);
          if(prim>0 && prim<_primaryToChar.length && sec==0 && tier==0)
          {
            //In the unlikely situation where two code points would be considered primitive for the same primary
            //order characteristic, we basically only keep the lowest point.
            if(_primaryToChar[prim]==0)
            {
              _primaryToChar[prim]=(char)i;
            }
          }
        }
      }
      
      //Special cases for spaces and tabs.
      CollationElementIterator it=col.getCollationElementIterator(" \t"); //$NON-NLS-1$
      int v=it.next();
      _whiteSpace[0]=CollationElementIterator.secondaryOrder(v);
      _whiteSpace[1]=CollationElementIterator.tertiaryOrder(v);
      v=it.next();
      _tab[0]=CollationElementIterator.secondaryOrder(v);
      _tab[1]=CollationElementIterator.tertiaryOrder(v);
    }
  }
  
  /**
   * Cleanup the given string by removing non-primary characteristics where possible, in order to get an output
   * that is as normalized as possible with regards to natural text entry.
   * <ul>
   * <li>Non-displayable characters are removed.</li>
   * <li>Spaces are normalized to \u0020.</li>
   * <li>Latin or extended Latin characters are set to lowercase.</li>
   * <li>Accentuated characters are replaced with their non-accent equivalent.</li>
   * <li>Alternative characters are replaced with their primitive version (i.e. '²' is replaced with '2').</li>
   * <li>Combined characters are replaced with their plain-text equivalent (i.e. the (tm) character \u2122 is replaced with "tm").</li>
   * </ul>
   * The resulting string can be shorter, of the same length or longer than the input string.
   * Codepoints above BMP are supported, but nothing much will happen outside of BMP and SMP. Characters from other planes will remain mostly unaffected.
   * @param s input string.
   * @return cleaned-up string.
   */
  public static String cleanup(String s)
  {
    computeTable();
    if(s==null)
      return null;
    
    StringBuilder ans=new StringBuilder();
    RuleBasedCollator col=(RuleBasedCollator)Collator.getInstance();
    col.setDecomposition(Collator.FULL_DECOMPOSITION);
    col.setStrength(Collator.TERTIARY);
    
    CollationElementIterator it=col.getCollationElementIterator(s);
    int offset=it.getOffset();
    int v=it.next();
    
    while(v!=CollationElementIterator.NULLORDER)
    {
      //Get the primary order of this element.
      int prim=CollationElementIterator.primaryOrder(v);
      if(prim>0)
      {
        char c=0;
        if(prim<_primaryToChar.length)
        {
          //The primary characteristic of this element is within the mapping table.
          c=_primaryToChar[prim];
        }
        
        if(c>0)
        {
          //We have a mapping to a primitive BMP character, let's use it.
          ans.append(c);
        }
        else
        {
          //There is a primary order, but we have no known mapping to a primitive BMP character. We'll
          //just copy from the input string.
          int len=it.getOffset()-offset;
          if(offset+len<=s.length())
          {
            ans.append(s.substring(offset,offset+len));
          }
        }
      }
      else
      {
        //This element does not have any primary order characteristic, probably a whitespace or something.
        
        if(_whiteSpace[0]==CollationElementIterator.secondaryOrder(v) && _whiteSpace[1]==CollationElementIterator.tertiaryOrder(v))
        {
          ans.append(" "); //$NON-NLS-1$
        }
        else if(_tab[0]==CollationElementIterator.secondaryOrder(v) && _tab[1]==CollationElementIterator.tertiaryOrder(v))
        {
          ans.append(" "); //$NON-NLS-1$
        }
        else
        {
          //Skip this character.
        }
      }
      offset=it.getOffset();
      v=it.next();
    }
    
    return ans.toString();
  }
}
