package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * ListOverrideDestination.
 */
public class ListOverrideDestination extends AbstractDestination
{
  private int _id;
  private int _ls;
  
  @Override
  public void enter(Context context,Global global)
  {
    _id=0;
    _ls=0;
  }
  
  @Override
  public void leave(Context context,Global global)
  {
    global.getListOverrides().put(_ls,_id);
  }
  
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    if(code.equals("listid")) //$NON-NLS-1$
      _id=Integer.parseInt(parameter);
    else if(code.equals("ls")) //$NON-NLS-1$
      _ls=Integer.parseInt(parameter);
    else
      super.control(code,parameter,context,global);
  }
}
