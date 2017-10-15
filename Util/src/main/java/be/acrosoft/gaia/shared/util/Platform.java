package be.acrosoft.gaia.shared.util;

/**
 * Platform.
 */
@OSSpecific({GaiaConstants.OS_NAME_WINDOWS,GaiaConstants.OS_NAME_MACOSX,GaiaConstants.OS_NAME_LINUX})
public class Platform
{
  /**
   * Platform is Windows.
   * @return true if platform is Windows.
   */
  public static boolean isWindows()
  {
    return (System.getProperty("os.name").indexOf(GaiaConstants.OS_NAME_WINDOWS)>=0); //$NON-NLS-1$
  }
  
  /**
   * Platform is Linux.
   * @return true if platform is Linux.
   */
  public static boolean isLinux()
  {
    return (System.getProperty("os.name").indexOf(GaiaConstants.OS_NAME_LINUX)>=0); //$NON-NLS-1$
  }

  /**
   * Platform is Mac OS X.
   * @return true if platform is Mac OS X.
   */
  public static boolean isMacOSX()
  {
    return (System.getProperty("os.name").indexOf(GaiaConstants.OS_NAME_MACOSX)>=0); //$NON-NLS-1$
  }
  
  /**
   * Get the platform name.
   * @return platform name.
   */
  public static String getPlatformName()
  {
    if(isWindows())
      return GaiaConstants.OS_NAME_WINDOWS;
    if(isLinux())
      return GaiaConstants.OS_NAME_LINUX;
    if(isMacOSX())
        return GaiaConstants.OS_NAME_MACOSX;
    throw new GaiaRuntimeException(new Error("Unsupported platform "+System.getProperty("os.name")));  //$NON-NLS-1$//$NON-NLS-2$
  }
  
}
