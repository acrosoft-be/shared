package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class TupleTest
{

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.Pair#hashCode()}.
   */
  @Test
  public void testHashCode()
  {
    assertEquals(new Tuple(null,null).hashCode(),new Tuple(null,null).hashCode());
    assertEquals(new Tuple(null,new String("a")).hashCode(),new Tuple(null,new String("a")).hashCode());
    assertEquals(new Tuple(new String("a"),null).hashCode(),new Tuple(new String("a"),null).hashCode());
    assertEquals(new Tuple(new String("a"),new String("a")).hashCode(),new Tuple(new String("a"),new String("a")).hashCode());
  }

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.Pair#Pair(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testPair()
  {
    new Tuple("a","a");
    new Tuple(null,"a");
    new Tuple("a",null);
    new Tuple(null,null);
  }

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.Pair#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject()
  {
    assertNotEquals(new Tuple(null,null),null);
    assertNotEquals(null,new Tuple(null,null));
    assertEquals(new Tuple(null,null),new Tuple(null,null));
    assertEquals(new Tuple(null,new String("a")),new Tuple(null,new String("a")));
    assertEquals(new Tuple(new String("a"),null),new Tuple(new String("a"),null));
    assertEquals(new Tuple(new String("a"),new String("b")),new Tuple(new String("a"),new String("b")));
    assertNotEquals(new Tuple(1,null),new Tuple(null,1));
    assertNotEquals(new Tuple(null,1),new Tuple(1,null));
    assertNotEquals(new Tuple(1,2),new Tuple(2,1));
    assertNotEquals(new Tuple(2,1),new Tuple(1,2));
    assertNotEquals(new Tuple(1,2),new Tuple(3,4));
    assertNotEquals(new Tuple(1,2),"Hello");
    assertNotEquals("Null",new Tuple(1,2));
  }

}
