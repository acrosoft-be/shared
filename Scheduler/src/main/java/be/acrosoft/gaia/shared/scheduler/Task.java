/**
 * Copyright Acropolis Software SPRL (https://www.acrosoft.be)
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

/**
 * Task.
 */
public class Task
{
  private String _name;
  private Schedule _schedule;
  private String _command;
  private String _parameters;
  
  /**
   * Create a new Task, with given schedule and command.
   * @param name name of the task.
   * @param schedule task schedule.
   * @param command command to execute.
   * @param parameters command parameters.
   */
  public Task(String name,Schedule schedule,String command,String parameters)
  {
    _name=name;
    _schedule=schedule;
    _command=command;
    _parameters=parameters;
  }
  
  /**
   * Get the name of the task.
   * @return task name.
   */
  public String getName()
  {
    return _name;
  }
  
  /**
   * Get the schedule of the task.
   * @return task schedule.
   */
  public Schedule getSchedule()
  {
    return _schedule;
  }
  
  /**
   * Get the command of the task.
   * @return task command.
   */
  public String getCommand()
  {
    return _command;
  }
  
  /**
   * Get the parameters of the task.
   * @return task parameters.
   */
  public String getParameters()
  {
    return _parameters;
  }
  
}
