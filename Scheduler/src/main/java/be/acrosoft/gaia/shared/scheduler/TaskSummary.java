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

/**
 * TaskSummary.
 */
public class TaskSummary
{
  private String _name;
  private String _command;
  private String _parameters;
  
  /**
   * Create a new TaskSummary.
   * @param name task name.
   * @param command task command.
   * @param parameters task parameters.
   */
  public TaskSummary(String name,String command,String parameters)
  {
    _name=name;
    _command=command;
    _parameters=parameters;
  }
  
  /**
   * Get the task name.
   * @return name.
   */
  public String getName()
  {
    return _name;
  }
  
  /**
   * Get the task command.
   * @return command.
   */
  public String getCommand()
  {
    return _command;
  }
  
  /**
   * Get the task parameters.
   * @return parameters.
   */
  public String getParameters()
  {
    return _parameters;
  }
}
