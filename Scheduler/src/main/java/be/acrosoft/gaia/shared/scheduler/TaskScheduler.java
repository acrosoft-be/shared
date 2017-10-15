package be.acrosoft.gaia.shared.scheduler;

import java.util.List;

/**
 * TaskScheduler.
 */
public interface TaskScheduler
{
  /**
   * Create a new task into the scheduler.
   * @param task task to create.
   * @return error message if creation is not successful, null if everything is ok.
   */
  public String createTask(Task task);
  
  /**
   * Delete an existing task from the scheduler.
   * @param name name of the task to delete.
   * @return error message if deletion is not successful, null if everything is ok.
   */
  public String deleteTask(String name);
  
  /**
   * List all existing tasks from the scheduler.
   * @param extractor task name extractor.
   * @return all available tasks. 
   */
  public List<TaskSummary> listTasks(TaskNameExtractor extractor);

  /**
   * List all existing tasks from the scheduler using the default extractor.
   * This is equivalent to listTasks(getDefaultTaskNameExtractor()).
   * @return all available tasks. 
   */
  public List<TaskSummary> listTasks();
  
  /**
   * Set the default taskname extractor.
   * @param extractor default extractor to use.
   */
  public void setDefaultTaskNameExtractor(TaskNameExtractor extractor);
  
  /**
   * Get the default taskname extractor currently in use.
   * @return default taskname extractor.
   */
  public TaskNameExtractor getDefaultTaskNameExtractor();
  
  /**
   * Check that the system scheduler is ready for normal operation.
   * @return null if scheduler is ready for operation, localized error message otherwise.
   */
  public String checkAvailability();
}
