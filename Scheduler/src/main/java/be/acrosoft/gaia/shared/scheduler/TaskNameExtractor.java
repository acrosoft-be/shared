package be.acrosoft.gaia.shared.scheduler;

/**
 * TaskNameExtractor.
 */
public interface TaskNameExtractor
{
  /**
   * Try to find the task name from the given command.
   * @param command command.
   * @return task name, or null if the name could not be extracted.
   */
  public String getTaskName(String command);
}
