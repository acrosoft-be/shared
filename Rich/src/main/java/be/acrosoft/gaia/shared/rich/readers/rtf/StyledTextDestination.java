package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * StyledTextDestination.
 */
public abstract class StyledTextDestination extends AbstractDestination
{
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    String param=parameter;
    if(param==null) param="1"; //$NON-NLS-1$
    
    if(code.equals("b")) //$NON-NLS-1$
      context.getTextStyle().setBold(param.equals("1")); //$NON-NLS-1$
    else if(code.equals("i")) //$NON-NLS-1$
      context.getTextStyle().setItalic(param.equals("1")); //$NON-NLS-1$
    else if(code.equals("ul")) //$NON-NLS-1$
      context.getTextStyle().setUnderline(true);
    else if(code.equals("ulnone")) //$NON-NLS-1$
      context.getTextStyle().setUnderline(false);
    else if(code.equals("highlight")) //$NON-NLS-1$
      context.getTextStyle().setBackGround(global.getColors().get(Integer.parseInt(param)));
    else if(code.equals("cf")) //$NON-NLS-1$
      context.getTextStyle().setForeGround(global.getColors().get(Integer.parseInt(param)));
    else if(code.equals("fs")) //$NON-NLS-1$
      context.getTextStyle().setFontSize(Integer.parseInt(param)/2.0);
    else if(code.equals("f")) //$NON-NLS-1$
    {
      Font fnt=global.getFonts().get(Integer.parseInt(param));
      context.getTextStyle().setFontFamily(fnt.family);
      context.getTextStyle().setFontName(fnt.name);
    }
    else
      super.control(code,parameter,context,global);
  }

}
