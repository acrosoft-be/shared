package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * Destination.
 */
public interface Destination
{
  /**
   * Write raw string on the Destination.
   * @param s string to write.
   * @param context current context.
   * @param global global information.
   */
  public void write(String s,Context context,Global global);

  /**
   * Write one control code to the destination.
   * @param code control code.
   * @param parameter control parameter, or null if no parameter is supplied.
   * @param context current context.
   * @param global global information.
   */
  public void control(String code,String parameter,Context context,Global global);
  
  /**
   * Enter the destination.
   * @param context current context.
   * @param global global information.
   */
  public void enter(Context context,Global global);

  /**
   * Leave the destination.
   * @param context current context.
   * @param global global information.
   */
  public void leave(Context context,Global global);
  
  /**
   * Check whether the destination supports the given extended control code.
   * @param code control code.
   * @param context current context.
   * @param global global information.
   * @return true if destination supports control code, false otherwise.
   */
  public boolean supportsExtended(String code,Context context,Global global);
}
