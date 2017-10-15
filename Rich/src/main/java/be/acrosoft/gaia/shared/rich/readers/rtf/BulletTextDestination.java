package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * BulletTextDestination.
 */
public class BulletTextDestination extends AbstractDestination
{
  @Override
  public void write(String s,Context context,Global global)
  {
    context.getParagraphStyle().setBulletText(context.getParagraphStyle().getBulletText()+s);
  }
  
  @Override
  public void enter(Context context,Global global)
  {
    context.getParagraphStyle().setBulletText(""); //$NON-NLS-1$
  }
}
