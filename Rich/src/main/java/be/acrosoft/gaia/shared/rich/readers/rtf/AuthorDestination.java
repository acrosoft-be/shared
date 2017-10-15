package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * AuthorDestination.
 */
public class AuthorDestination extends AbstractDestination
{
  @Override
  public void write(String s,Context context,Global global)
  {
    global.getDocument().getMeta().setAuthor(global.getDocument().getMeta().getAuthor()+s);
  }
  
  @Override
  public void enter(Context context,Global global)
  {
    global.getDocument().getMeta().setAuthor(""); //$NON-NLS-1$
  }

}
