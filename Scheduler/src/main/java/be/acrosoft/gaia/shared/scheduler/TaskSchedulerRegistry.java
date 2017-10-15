package be.acrosoft.gaia.shared.scheduler;

import be.acrosoft.gaia.shared.util.GaiaConstants;
import be.acrosoft.gaia.shared.util.OSSpecific;
import be.acrosoft.gaia.shared.util.Platform;

/**
 * TaskSchedulerRegistry.
 */
@OSSpecific({GaiaConstants.OS_NAME_WINDOWS,GaiaConstants.OS_NAME_MACOSX,GaiaConstants.OS_NAME_LINUX})
public class TaskSchedulerRegistry
{
  private static TaskScheduler _instance=createInstance();
  
  private static TaskScheduler createInstance()
  {
    if(Platform.isWindows())
    {
      return new WindowsTaskScheduler();
    }
    return new CronTaskScheduler();
  }
  
  /**
   * Get the instance of the task scheduler for this system.
   * @return the task scheduler.
   */
  public static TaskScheduler getTaskScheduler()
  {
    return _instance;
  }
}
