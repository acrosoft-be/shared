/**
 * Copyright Acropolis Software SPRL (http://www.acrosoft.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
