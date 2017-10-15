package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * IgnoreDestination.
 */
public class IgnoreDestination extends AbstractDestination
{
  @Override
  public void write(String s,Context context,Global global)
  {
  }

  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
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
    return true;
  }
}
