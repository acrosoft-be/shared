package be.acrosoft.gaia.shared.util;

import java.util.Arrays;

/**
 * Tuple.
 */
public class Tuple
{
  /**
   * Tuple values.
   */
  public Object[] values;
  
  /**
   * Create a new Tuple.
   * @param objects values.
   */
  public Tuple(Object...objects)
  {
    values=objects;
  }
  
  @Override
  public int hashCode()
  {
    return Arrays.deepHashCode(values);
  }
  
  @Override
  public boolean equals(Object other)
  {
    if(other==null || !(other instanceof Tuple))
    {
      return false;
    }
    return Arrays.deepEquals(values,((Tuple)other).values);
  }
}
