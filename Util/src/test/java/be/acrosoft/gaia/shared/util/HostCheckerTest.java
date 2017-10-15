package be.acrosoft.gaia.shared.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class HostCheckerTest
{
  @Test
  public void testFromLAN() throws Exception
  {
    assertTrue(HostChecker.isFromLAN(InetAddress.getByName("localhost")));
    assertTrue(HostChecker.isFromLAN(InetAddress.getByName("127.0.0.1")));
    assertFalse(HostChecker.isFromLAN(InetAddress.getByName("www.google.com")));
  }
  
  @Test
  public void testList() throws Exception
  {
    assertTrue(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("127.0.0.1")));
    assertTrue(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("127.0.0.1/32")));
    assertTrue(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("127.0.0.0/16")));
    assertFalse(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("zxy.notthere.nottheretld")));
    assertFalse(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("127.0.0.1/a")));
    assertTrue(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("localhost")));
    assertFalse(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("www.google.com")));
    assertTrue(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("0.0.0.0/1")));
    assertFalse(HostChecker.isWithinList(InetAddress.getByName("127.0.0.1"),Arrays.asList("128.0.0.0/1")));
  }
}
