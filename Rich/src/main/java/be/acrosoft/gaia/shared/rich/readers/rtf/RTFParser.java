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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * RTFParser.
 */
public class RTFParser
{
  /**
   * An RTF token.
   */
  public static abstract class Token
  {
    /**
     * Token type.
     */
    public static enum Type
    {
      /**
       * Control.
       */
      CONTROL,
      /**
       * Text.
       */
      TEXT,
      /**
       * Start of block.
       */
      START_BLOCK,
      /**
       * End of block.
       */
      END_BLOCK
    }
    
    /**
     * Token type.
     */
    public Type type;
  }
  
  /**
   * ControlToken.
   */
  public static class ControlToken extends Token
  {
    /**
     * Code.
     */
    public String code;
    /**
     * Parameter, null if no parameter.
     */
    public String parameter;
    
    /**
     * Create a new ControlToken.
     */
    public ControlToken()
    {
      type=Type.CONTROL;
    }
  }

  /**
   * TextToken.
   */
  public static class TextToken extends Token
  {
    /**
     * Text.
     */
    public String text;
    
    /**
     * Create a new TextToken.
     */
    public TextToken()
    {
      type=Type.TEXT;
    }
  }

  /**
   * StartBlockToken.
   */
  public static class StartBlockToken extends Token
  {
    /**
     * Create a new StartBlockToken.
     */
    public StartBlockToken()
    {
      type=Type.START_BLOCK;
    }
  }
  
  /**
   * EndBlockToken.
   */
  public static class EndBlockToken extends Token
  {
    /**
     * Create a new EndBlockToken.
     */
    public EndBlockToken()
    {
      type=Type.END_BLOCK;
    }
  }
  
  private InputStream _is;
  private int _next;
  private boolean _eof;
  private Charset _charset;

  /**
   * Create a new RTFParser.
   * @param is rtf stream.
   */
  public RTFParser(InputStream is)
  {
    _is=is;
    _next=-1;
    _eof=false;
    setCharset(Charset.defaultCharset());
  }
  
  private int readNextByte() throws IOException
  {
    if(_eof) return -1;
    if(_next!=-1)
    {
      byte ans=(byte)_next;
      _next=-1;
      return ans;
    }
    int ans=_is.read();
    if(ans==-1)
    {
      _is.close();
      _eof=true;
    }
    return ans;
  }
  
  private void pushBack(int b)
  {
    _next=b;
  }
  
  private ControlToken readControl(int first) throws IOException
  {
    StringBuffer control=new StringBuffer();
    StringBuffer param=null;
    control.append((char)first);
    int current=readNextByte();
    while(current!=-1)
    {
      if((current>='a' && current<='z') || (current>='A' && current<='Z'))
      {
        control.append((char)current);
      }
      else if(current==' ')
      {
        break;
      }
      else
      {
        pushBack(current);
        break;
      }
      current=readNextByte();
    }
    
    if((current>='0' && current<='9') || (current=='-'))
    {
      current=readNextByte();
      param=new StringBuffer();
      while(current!=-1)
      {
        if((current>='a' && current<='z') || (current>='A' && current<='Z') || (current>='0' && current<='9') || (current=='-'))
        {
          param.append((char)current);
        }
        else if(current==' ')
        {
          break;
        }
        else
        {
          pushBack(current);
          break;
        }
        current=readNextByte();
      }
    }
        
    ControlToken token=new ControlToken();
    token.code=control.toString();
    if(param!=null)
      token.parameter=param.toString();
    return token;
  }
  
  private TextToken readText(int first) throws IOException
  {
    StringBuffer buffer=new StringBuffer();
    if(first!=10 && first!=13)
      buffer.append((char)first);
    int current=readNextByte();
    while(current!=-1 && current!='\\' && current!='{' && current!='}')
    {
      if(current!=10 && current!=13)
        buffer.append((char)current);
      current=readNextByte();
    }
    if(current!=-1)
      pushBack(current);
    TextToken token=new TextToken();
    token.text=buffer.toString();
    return token;
  }
  
  private int fromHex(int b) throws IOException
  {
    if(b>='0' && b<='9') return b-'0';
    if(b>='a' && b<='f') return b-'a'+10;
    if(b>='A' && b<='F') return b-'A'+10;
    throw new IOException("Illegal hex character "+(char)b); //$NON-NLS-1$
  }
  
  private String charFromByte(int i)
  {
    byte b=(byte)i;
    ByteBuffer buffer=ByteBuffer.allocate(1);
    buffer.put(b);
    CharBuffer cbuf=_charset.decode(buffer.rewind());
    String ans=cbuf.toString();
    return ans;
  }
  
  private Token nextTokenInternal() throws IOException
  {
    int first=readNextByte();
    if(first==-1) return null;
    if(first=='\\')
    {
      first=readNextByte();
      if(first=='\'')
      {
        int b1=fromHex(readNextByte());
        int b2=fromHex(readNextByte());
        int b=(b1<<4)+b2;
        TextToken text=new TextToken();
        text.text=charFromByte(b);
        return text;
      }
      else if(first=='~')
      {
        TextToken text=new TextToken();
        text.text="\u00a0"; //$NON-NLS-1$
        return text;
      }
      else if((first>='a' && first<='z') || (first>='A' && first<='Z') || first=='*')
      {
        return readControl(first);
      }
      TextToken text=new TextToken();
      text.text=""+(char)first; //$NON-NLS-1$
      return text;
    }
    if(first=='{')
      return new StartBlockToken();
    if(first=='}')
      return new EndBlockToken();
    return readText(first);
  }
  
  private boolean isEmptyText(Token token)
  {
    if(token==null) return false;
    if(token.type!=Token.Type.TEXT) return false;
    TextToken text=(TextToken)token;
    if(text.text.length()==0) return true;
    return false;
  }
  
  /**
   * Read the next RTF token.
   * @return next RTF token, or null if end of file is reached.
   * @throws IOException in case of problem.
   */
  public Token nextToken() throws IOException
  {
    Token token=nextTokenInternal();
    while(isEmptyText(token))
      token=nextTokenInternal();
    return token;
  }
  
  /**
   * Set the charset.
   * @param charset The charset to set.
   */
  public void setCharset(Charset charset)
  {
    _charset=charset;
  }

  /**
   * Get the charset.
   * @return Returns the charset.
   */
  public Charset getCharset()
  {
    return _charset;
  }
}
