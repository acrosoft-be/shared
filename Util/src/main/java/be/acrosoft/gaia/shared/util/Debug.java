package be.acrosoft.gaia.shared.util;

/**
 * Debug.
 */
public class Debug
{
  private static Boolean _value;
  
  
  /**
   * Check whether the current running installation is a debug one, or a release one.
   * @return true if debug mode is enabled.
   */
  public static boolean isDebug()
  {
    if(_value==null)
    {
      _value=System.getProperty("gaia.debug")!=null; //$NON-NLS-1$
    }
    return _value;
  }
  
  /**
   * Override the default debug value.
   * @param over new debug value, or null if default should be used.
   */
  public static void override(Boolean over)
  {
    _value=over;
  }
}
