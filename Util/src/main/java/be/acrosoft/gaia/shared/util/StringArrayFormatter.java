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

/**
 * The StringArrayFormatter allows to encode and decode an array of string
 * under the form of a single string. If the input string array contains
 * three elements one, two and three, the resulting string will be
 * equal to "one,"two","three". Double quotes are escaped to \". Backslashes
 * are escaped to \\.
 */
public class StringArrayFormatter
{
  private static String escape(String str)
  {
    //I love regexp, backslashes and escaped strings all in one...
    
    //Replace all \ with \\
    str=str.replaceAll("\\\\","\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
    //Replace all " with \"
    str=str.replaceAll("\\\"","\\\\\\\"");  //$NON-NLS-1$//$NON-NLS-2$
    return str;
  }
  
  private static String collapse(String str)
  {
    //Replace all \\ with \
    str=str.replaceAll("\\\\\\\\","\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
    //Replace all \" with "
    str=str.replaceAll("\\\\\\\"","\\\"");  //$NON-NLS-1$//$NON-NLS-2$
    return str;
  }
  
  private static int findSeparator(String str)
  {
    int index=0;
    boolean inStr=false;
    while(index<str.length())
    {
      char c=str.charAt(index);
      if(c=='\\')
      {
        if(index==str.length()-1) throw new IllegalArgumentException(str);
        index++;
      }
      else if(c=='"') inStr=!inStr;
      else if(c==',')
      {
        if(!inStr) return index;
      }
      index++;
    }
    return -1;
  }
  
  /**
   * Encode the given array of strings to a single string.
   * @param array array to encode.
   * @return encoded string.
   */
  public static String encode(String[] array)
  {
    StringBuilder res=new StringBuilder();
    for(int i=0;i<array.length;i++)
    {
      if(i!=0) res.append(',');
      res.append('"');
      res.append(escape(array[i]));
      res.append('"');
    }
    return res.toString();
  }
  
  /**
   * Decode the given string back to an array of strings.
   * @param array encoded string.
   * @return decoded array.
   */
  public static String[] decode(String array)
  {
    ArrayList<String> ans=new ArrayList<String>();
    
    array=array.trim();
    
    while(array.length()>0)
    {
      int pos=findSeparator(array);
      String item;
      if(pos>=0)
      {
        item=array.substring(0,pos).trim();
        array=array.substring(pos+1).trim();
      }
      else
      {
        item=array;
        array=""; //$NON-NLS-1$
      }
      if(item.length()<2) throw new IllegalArgumentException(array);
      if(item.charAt(0)!='"') throw new IllegalArgumentException(array);
      if(item.charAt(item.length()-1)!='"') throw new IllegalArgumentException(array);
      item=item.substring(1,item.length()-1);
      ans.add(collapse(item));
    }
    
    return ans.toArray(new String[ans.size()]);
  }
}
