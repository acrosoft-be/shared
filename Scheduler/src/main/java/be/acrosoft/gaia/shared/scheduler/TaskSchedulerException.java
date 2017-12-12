package be.acrosoft.gaia.shared.scheduler;

import be.acrosoft.gaia.shared.util.GaiaException;

/**
 * TaskSchedulerException.
 */
public class TaskSchedulerException extends GaiaException
{
  private static final long serialVersionUID=1L;

  /**
   * Create a new TaskSchedulerException.
   * @param message message.
   */
  public TaskSchedulerException(String message)
  {
    super(message);
  }
  
  /**
   * Create a new TaskSchedulerException.
   * @param message message.
   * @param cause cause.
   */
  public TaskSchedulerException(String message,Throwable cause)
  {
    super(message,cause);
  }
}
