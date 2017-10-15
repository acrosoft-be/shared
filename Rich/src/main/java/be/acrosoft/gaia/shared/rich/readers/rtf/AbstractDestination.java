package be.acrosoft.gaia.shared.rich.readers.rtf;

import java.io.IOException;

import be.acrosoft.gaia.shared.rich.readers.rtf.RTFParser.ControlToken;
import be.acrosoft.gaia.shared.rich.readers.rtf.RTFParser.Token;

/**
 * AbstractDestination.
 */
public abstract class AbstractDestination implements Destination
{
  @Override
  public void write(String s,Context context,Global global)
  {
  }
  
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    if(code.equals("*")) //$NON-NLS-1$
    {
      try
      {
        Token token=global.getParser().nextToken();
        if(token.type!=Token.Type.CONTROL)
          throw new RuntimeException("Unexpected token type "+token.type+" after \\* control token"); //$NON-NLS-1$ //$NON-NLS-2$
        ControlToken control=(ControlToken)token;
        if(supportsExtended(control.code,context,global))
        {
          control(control.code,control.parameter,context,global);
        }
        else
        {
          context.setDestination(new IgnoreDestination());
        }
      }
      catch(IOException ex)
      {
        throw new RuntimeException(ex);
      }
    }
    else if(code.equals("tab")) //$NON-NLS-1$
      write("\t",context,global); //$NON-NLS-1$
    else if(code.equals("rquote")) //$NON-NLS-1$
      write("\u2019",context,global); //$NON-NLS-1$
    else if(code.equals("lquote")) //$NON-NLS-1$
      write("\u201b",context,global); //$NON-NLS-1$
    else if(code.equals("rdblquote")) //$NON-NLS-1$
      write("\u201d",context,global); //$NON-NLS-1$
    else if(code.equals("ldblquote")) //$NON-NLS-1$
      write("\u201c",context,global); //$NON-NLS-1$
    else if(code.equals("endash")) //$NON-NLS-1$
      write("-",context,global); //$NON-NLS-1$
    else if(code.equals("emdash")) //$NON-NLS-1$
      write("\u2013",context,global); //$NON-NLS-1$
    else if(code.equals("enspace")) //$NON-NLS-1$
      write(" ",context,global); //$NON-NLS-1$
    else if(code.equals("emspace")) //$NON-NLS-1$
      write(" ",context,global); //$NON-NLS-1$
  }

  @Override
  public void enter(Context context,Global global)
  {
  }
  
  @Override
  public void leave(Context context,Global global)
  {
  }

  @Override
  public boolean supportsExtended(String code,Context context,Global global)
  {
    return false;
  }

}
