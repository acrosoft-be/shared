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
package be.acrosoft.gaia.shared.rich.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import be.acrosoft.gaia.shared.rich.DocumentReader;
import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.readers.rtf.Context;
import be.acrosoft.gaia.shared.rich.readers.rtf.Global;
import be.acrosoft.gaia.shared.rich.readers.rtf.RTFParser;
import be.acrosoft.gaia.shared.rich.readers.rtf.RootDestination;

/**
 * RTFReader.
 */
public class RTFReader implements DocumentReader
{
  private InputStream _is;
    
  /**
   * Create a new RTFReader.
   * @param is RTF input stream.
   */
  public RTFReader(InputStream is)
  {
    _is=is;
  }

  private void print(int depth,String str)
  {
    //for(int i=0;i<depth-2;i++) System.out.print("  ");
    //System.out.println(str);
  }
  
  @Override
  public RichDocument read()
  {
    try
    {
      RichDocument ans=new RichDocument();
      LinkedList<Context> stack=new LinkedList<Context>();
      RTFParser parser=new RTFParser(_is);
      Global global=new Global(parser,ans);
      
      Context top=new Context(global);
      top.setDestination(new RootDestination());
      stack.addLast(top);
      
      RTFParser.Token token=parser.nextToken();
      while(token!=null)
      {
        Context current=stack.getLast();
        if(token.type==RTFParser.Token.Type.CONTROL)
        {
          RTFParser.ControlToken control=(RTFParser.ControlToken)token;
          if(control.parameter==null)
            print(stack.size(),control.code);
          else
            print(stack.size(),control.code+control.parameter);
          current.getDestination().control(control.code,control.parameter,current,global);
        }
        else if(token.type==RTFParser.Token.Type.START_BLOCK)
        {
          print(stack.size(),"{"); //$NON-NLS-1$
          stack.addLast(current.clone());
        }
        else if(token.type==RTFParser.Token.Type.END_BLOCK)
        {
          Context old=stack.removeLast();
          current=stack.getLast();
          if(old.getDestination()!=current.getDestination())
            old.getDestination().leave(old,global);
          print(stack.size(),"}"); //$NON-NLS-1$
        }
        else if(token.type==RTFParser.Token.Type.TEXT)
        {
          RTFParser.TextToken text=(RTFParser.TextToken)token;
          print(stack.size(),text.text);
          current.getDestination().write(text.text,current,global);
        }
        else
        {
          throw new RuntimeException("Unknown token type "+token.type); //$NON-NLS-1$
        }
        token=parser.nextToken();
      }
      return ans;
    }
    catch(IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

}
