package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * TitleDestination.
 */
public class TitleDestination extends AbstractDestination
{
  @Override
  public void write(String s,Context context,Global global)
  {
    global.getDocument().getMeta().setTitle(global.getDocument().getMeta()+s);
  }
  
  @Override
  public void enter(Context context,Global global)
  {
    global.getDocument().getMeta().setTitle(""); //$NON-NLS-1$
  }

}
