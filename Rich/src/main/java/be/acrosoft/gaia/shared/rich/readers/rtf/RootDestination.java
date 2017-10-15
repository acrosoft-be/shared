package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * RootDestination.
 */
public class RootDestination extends AbstractDestination
{
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    if(code.equals("rtf")) //$NON-NLS-1$
      context.setDestination(new DocumentDestination());
    else
      super.control(code,parameter,context,global);
  }

}
