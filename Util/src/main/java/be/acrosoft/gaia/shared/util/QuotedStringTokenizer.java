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

import java.util.ArrayList;
import java.util.List;

/**
 * The QuotedStringTokenizer serves the same purpose as the standard
 * StringTokenizer, except that it allows defining a set of quote
 * characters. Characters between quotes don't qualify as separators.
 * Quotes are not returned in the resulting tokens.
 * If several quote characters are defined, quote characters within
 * quotes are considered as regular characters. For example, if ' and
 * " are quotes, "'" returns ' and '"' return ".
 */
public class QuotedStringTokenizer
{
  /**
   * Parse the given string using " and ' as quotes, and space as separator.
   * @param string string to parse.
   * @return tokens.
   */
  public static String[] parse(String string)
  {
    return parse(string," ","\"'");  //$NON-NLS-1$//$NON-NLS-2$
  }
  
  /**
   * Parse the given string.
   * @param string string to parse.
   * @param separators separators. Each character of the string is considered as separator.
   * @param quotes quotes. Each character of the string is considered as quote.
   * @return tokens.
   */
  public static String[] parse(String string,String separators,String quotes)
  {
    List<String> ans=new ArrayList<String>();
    
    StringBuilder current=new StringBuilder();
    
    char currentQuote=0;
    boolean inQuote=false;
    
    for(int i=0;i<string.length();i++)
    {
      char c=string.charAt(i);
      
      if(inQuote)
      {
        if(c==currentQuote)
        {
          inQuote=false;
        }
        else
        {
          current.append(c);
        }
      }
      else
      {
        if(quotes.indexOf(c)>=0)
        {
          currentQuote=c;
          inQuote=true;
        }
        else if(separators.indexOf(c)>=0)
        {
          if(current.length()>0)
          {
            ans.add(current.toString());
            current=new StringBuilder();
          }
        }
        else
        {
          current.append(c);
        }
      }
    }
    
    if(current.length()>0)
      ans.add(current.toString());
    
    return ans.toArray(new String[ans.size()]);
  }

  /**
   * Parse the given string using " " as separator.
   * @param string string to parse.
   * @return list of ranges, each item being the token start index and the length.
   */
  public static List<Pair<Integer,Integer>> parseRanges(String string)
  {
    return parseRanges(string," "); //$NON-NLS-1$
  }

  /**
   * Parse the given string.
   * @param string string to parse.
   * @param separators separators. Each character of the string is considered as separator.
   * @return list of ranges, each item being the token start index and the length.
   */
  public static List<Pair<Integer,Integer>> parseRanges(String string,String separators)
  {
    List<Pair<Integer,Integer>> ans=new ArrayList<Pair<Integer,Integer>>();
    
    int currentStart=0;
    int currentLength=0;
    
    for(int i=0;i<string.length();i++)
    {
      char c=string.charAt(i);
      
      if(separators.indexOf(c)>=0)
      {
        if(currentLength>0)
          ans.add(new Pair<Integer,Integer>(currentStart,currentLength));
        currentLength=0;
      }
      else
      {
        if(currentLength==0)
          currentStart=i;
        currentLength++;
      }
    }
    
    if(currentLength>0)
      ans.add(new Pair<Integer,Integer>(currentStart,currentLength));
    
    return ans;
  }
}
