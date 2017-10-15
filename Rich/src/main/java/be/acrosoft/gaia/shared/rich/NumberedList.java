package be.acrosoft.gaia.shared.rich;

/**
 * List.
 */
public class NumberedList
{
  private int _init;
  
  /**
   * Create a new NumberedList.
   */
  public NumberedList()
  {
  }
  
  /**
   * Set initial value.
   * @param init initial value.
   */
  public void setInitialValue(int init)
  {
    _init=init;
  }
  
  /**
   * Get the initial value.
   * @return initial value.
   */
  public int getInitialValue()
  {
    return _init;
  }

}
