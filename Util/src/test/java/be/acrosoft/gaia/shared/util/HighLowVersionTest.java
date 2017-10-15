package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * HighLowVersionTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class HighLowVersionTest
{

  @Test
  public void testHighLowVersion()
  {
    HighLowVersion version=new HighLowVersion(1,2);
    
    assertTrue(HighLowVersion.NONE.compareTo(version)<0);
    assertTrue(version.compareTo(HighLowVersion.NONE)>0);
    assertTrue(version.compareTo(version)==0);
    assertTrue(version.equals(version));

    assertTrue(new HighLowVersion(1,1).compareTo(version)<0);
    assertTrue(version.compareTo(new HighLowVersion(1,1))>0);
    assertTrue(version.compareTo(new HighLowVersion(1,2))==0);
    assertTrue(version.equals(new HighLowVersion(1,2)));
    
    assertEquals(1,version.getHigh());
    assertEquals(2,version.getLow());

    assertTrue(version.compareTo(version.clone())==0);
    assertTrue(version.equals(version.clone()));
    
    assertTrue(new HighLowVersion(1,0).compareTo(new HighLowVersion(1,1))<0);
    assertTrue(new HighLowVersion(1,0).compareTo(new HighLowVersion(4,0))<0);
    assertTrue(new HighLowVersion(1,0).compareTo(new HighLowVersion(4,1))<0);
    
    assertTrue(new HighLowVersion(1,1).compareTo(new HighLowVersion(1,0))>0);
    assertTrue(new HighLowVersion(4,0).compareTo(new HighLowVersion(1,0))>0);
    assertTrue(new HighLowVersion(4,1).compareTo(new HighLowVersion(1,0))>0);
    
    assertEquals("1.2",new HighLowVersion(1,2).toString());
}

}
