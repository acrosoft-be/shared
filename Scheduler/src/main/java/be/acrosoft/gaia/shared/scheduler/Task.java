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
