package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * PairTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class PairTest
{

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.Pair#hashCode()}.
   */
  @Test
  public void testHashCode()
  {
    assertEquals(new Pair<String,String>(null,null).hashCode(),new Pair<String,String>(null,null).hashCode());
    assertEquals(new Pair<String,String>(null,new String("a")).hashCode(),new Pair<String,String>(null,new String("a")).hashCode());
    assertEquals(new Pair<String,String>(new String("a"),null).hashCode(),new Pair<String,String>(new String("a"),null).hashCode());
    assertEquals(new Pair<String,String>(new String("a"),new String("a")).hashCode(),new Pair<String,String>(new String("a"),new String("a")).hashCode());
  }

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.Pair#Pair(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testPair()
  {
    new Pair<String,String>("a","a");
    new Pair<String,String>(null,"a");
    new Pair<String,String>("a",null);
    new Pair<String,String>(null,null);
  }

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.util.Pair#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject()
  {
    assertNotEquals(new Pair<String,String>(null,null),null);
    assertNotEquals(null,new Pair<String,String>(null,null));
    assertEquals(new Pair<String,String>(null,null),new Pair<String,String>(null,null));
    assertEquals(new Pair<String,String>(null,new String("a")),new Pair<String,String>(null,new String("a")));
    assertEquals(new Pair<String,String>(new String("a"),null),new Pair<String,String>(new String("a"),null));
    assertEquals(new Pair<String,String>(new String("a"),new String("b")),new Pair<String,String>(new String("a"),new String("b")));
    assertNotEquals(Pair.pair(1,null),Pair.pair(null,1));
    assertNotEquals(Pair.pair(null,1),Pair.pair(1,null));
    assertNotEquals(Pair.pair(1,2),Pair.pair(2,1));
    assertNotEquals(Pair.pair(2,1),Pair.pair(1,2));
    assertNotEquals(Pair.pair(1,2),Pair.pair(3,4));
    assertNotEquals(Pair.pair(1,2),"Hello");
    assertNotEquals("Null",Pair.pair(1,2));
  }
  
  @Test
  public void testToString()
  {
    assertEquals("Pair<null,null>",new Pair<String,String>(null,null).toString());
    assertEquals("Pair<null,a>",new Pair<String,String>(null,new String("a")).toString());
    assertEquals("Pair<a,null>",new Pair<String,String>(new String("a"),null).toString());
    assertEquals("Pair<a,b>",new Pair<String,String>(new String("a"),new String("b")).toString());
  }

}
