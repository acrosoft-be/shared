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
