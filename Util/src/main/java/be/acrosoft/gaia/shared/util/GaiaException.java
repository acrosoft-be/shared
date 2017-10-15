package be.acrosoft.gaia.shared.util;

/**
 * GaiaException
 */
public class GaiaException extends Exception
{
  private static final long serialVersionUID=1L;

  /**
   * Create a new GaiaException
   */
  protected GaiaException()
  {
    super();
  }
  
  /**
   * Create a new GaiaException
   * @param cause exception cause.
   */
  public GaiaException(Throwable cause)
  {
    super(cause);
  }

  /**
   * Create a new GaiaException
   * @param message error message.
   */
  protected GaiaException(String message)
  {
    super(message);
  }
  
  /**
   * Create a new GaiaException
   * @param message error message.
   * @param cause exception cause.
   */
  public GaiaException(String message,Throwable cause)
  {
    super(message,cause);
  }
  
  /**
   * Get the root cause of the given exception.
   * @param th throwable.
   * @return root cause.
   */
  public static Throwable getRootCause(Throwable th)
  {
    Throwable root=th.getCause();
    if(root==null) return th;
    return getRootCause(root);
  }
}
