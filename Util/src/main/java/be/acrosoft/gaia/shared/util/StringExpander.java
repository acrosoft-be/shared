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
package be.acrosoft.gaia.shared.util;

import java.util.Map;
import java.util.Properties;

/**
 * The StringExpander allows string expansion from a given set of string->string map.
 * The input string is formatted as "abcd$(var)efgh". The "var" key will be looked-up
 * into the map, and the $(var) string will be replaced with either the value
 * associated with the key, or an empty string if no match is found.
 * Note that the "$$" sequence is replaced with one single "$" character.
 * The expansion does not perform recursively. If an expanded value contains
 * an expansion sequence, it won't be processed.
 */
public class StringExpander
{
  /**
   * Expand the given string, using a map as expansion source.
   * @param string string to expand.
   * @param map mapping.
   * @return expanded string.
   */
  public static String expand(String string,Map<String,String> map)
  {
    Properties prop=new Properties();
    prop.putAll(map);
    return expand(string,prop);
  }
    
  /**
   * Expand the given string, using both the system properties and system environment
   * for expansion.
   * @param string string to expand.
   * @return expanded string.
   */
  public static String expand(String string)
  {
    Properties props=new Properties(System.getProperties());
    props.putAll(System.getenv());
    return expand(string,props);
  }
  
  /**
   * Expand the given string, using a Properties instance as expansion source.
   * @param string string to expand.
   * @param prop mapping.
   * @return expanded string.
   */
  public static String expand(String string,Properties prop)
  {
    StringBuilder ans=new StringBuilder();
    
    int current=0;
    
    while(current<string.length())
    {
      int next=string.indexOf('$',current);
      if(next<0 || next==string.length()-1)
      {
        ans.append(string.substring(current));
        current=string.length();
      }
      else
      {
        ans.append(string.substring(current,next));
        current=next;
        
        char nextChar=string.charAt(next+1);
        if(nextChar=='$')
        {
          ans.append('$');
          current+=2;
        }
        else if(nextChar=='(')
        {
          int endVar=string.indexOf(')',next+2);
          if(endVar<0)
          {
            ans.append(string.substring(current));
            current=string.length();
          }
          else
          {
            String var=string.substring(next+2,endVar);
            var=prop.getProperty(var,""); //$NON-NLS-1$
            ans.append(var);
            current=endVar+1;
          }
        }
        else
        {
          ans.append('$');
          ans.append(nextChar);
          current+=2;
        }
      }
    }
    
    return ans.toString();
  }

}
