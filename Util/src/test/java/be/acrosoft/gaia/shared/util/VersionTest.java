package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

/**
 * VersionTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class VersionTest
{

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.Version#Version(java.lang.String)}.
   */
  @Test
  public void testVersionString()
  {
    Version version;
    
    version=new Version((String)null);
    assertEquals(0,version.getHigh());
    assertEquals(0,version.getLow());
    assertEquals(0,version.getRevision());
    assertEquals(0,version.getBuild());
    assertEquals("",version.getMod());
    
    version=new Version("1.14.0.5");
    assertEquals(1,version.getHigh());
    assertEquals(14,version.getLow());
    assertEquals(0,version.getRevision());
    assertEquals(5,version.getBuild());
    assertEquals("",version.getMod());

    version=new Version("1.14.0.5w");
    assertEquals(1,version.getHigh());
    assertEquals(14,version.getLow());
    assertEquals(0,version.getRevision());
    assertEquals(5,version.getBuild());
    assertEquals("w",version.getMod());

    version=new Version("1.14.0.5mod");
    assertEquals(1,version.getHigh());
    assertEquals(14,version.getLow());
    assertEquals(0,version.getRevision());
    assertEquals(5,version.getBuild());
    assertEquals("mod",version.getMod());
  }

  /**
   * Test method for {@link com.acrosoft.gaia.common.core.Version#compareTo(com.acrosoft.gaia.common.core.Version)}.
   */
  @Test
  public void testCompareTo()
  {
    Version a,b;
    
    a=new Version(1,14,0,1);
    b=new Version(1,14,0,2);
    assertTrue(a.compareTo(b)<0);
    assertTrue(b.compareTo(a)>0);

    a=new Version(1,14,1,1);
    b=new Version(1,14,2,1);
    assertTrue(a.compareTo(b)<0);
    assertTrue(b.compareTo(a)>0);

    a=new Version(1,14,1,15);
    b=new Version(1,14,2,1);
    assertTrue(a.compareTo(b)<0);
    assertTrue(b.compareTo(a)>0);

    a=new Version(1,13,10,15);
    b=new Version(1,14,2,1);
    assertTrue(a.compareTo(b)<0);
    assertTrue(b.compareTo(a)>0);

    a=new Version(1,13,10,15);
    b=new Version(2,0,0,0);
    assertTrue(a.compareTo(b)<0);
    assertTrue(b.compareTo(a)>0);
    
    a=new Version(1,14,0,1,"w");
    a=new Version(1,14,0,1);
    assertTrue(a.compareTo(b)<0);
    assertTrue(b.compareTo(a)>0);

    a=new Version(1,14,0,1,"w");
    a=new Version(1,14,0,1,"z");
    assertTrue(a.compareTo(b)<0);
    assertTrue(b.compareTo(a)>0);
  }

  @Test
  public void testToString()
  {
    Version v=new Version(1,14,0,1,"w");
    assertEquals("1.14.0.1w",v.toString());
    assertEquals("1.14.0",v.toUserString());
  }
  
  @Test
  public void testClone()
  {
    Version v=new Version(1,14,0,1,"w");
    Version vc=v.clone();
    assertEquals(v,vc);
    assertTrue(v!=vc);
  }
  
  @Test
  public void testReadFromFile() throws IOException
  {
    File file=new File("version");
    try
    {
      BufferedWriter bw;
      
      bw=new BufferedWriter(new FileWriter(file));
      bw.write("1.15.0.1\n");
      bw.close();
      Version version=new Version(file);
      assertEquals(new Version(1,15,0,1),version);
      
      bw=new BufferedWriter(new FileWriter(file));
      bw.write("1.15.0.1");
      bw.close();
      assertEquals(new Version(1,15,0,1),version);
    }
    finally
    {
      if(!file.delete()) file.deleteOnExit();
    }
  }
}
