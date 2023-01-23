/**
 * Copyright Acropolis Software SPRL (https://www.acrosoft.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.acrosoft.gaia.shared.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * Utilities to check hosts against access list.
 */
public class HostChecker
{
  private static boolean isWithinSubNet(byte[] toTest,byte[] subNetAddress,short subNetLength)
  {
    if(toTest.length!=subNetAddress.length) return false;
    
    if(toTest.length==4 && subNetLength>32)
    {
      //Working around Java bug #6707289
      if(subNetAddress[0]==10)
      {
        subNetLength=8;
      }
      else if(subNetAddress[0]==(byte)172 && subNetAddress[1]>=16 && subNetAddress[1]<32)
      {
        subNetLength=12;
      }
      else if(subNetAddress[0]==(byte)192 && subNetAddress[1]==(byte)168)
      {
        subNetLength=16;
      }
    }
    
    for(int i=0;i<toTest.length;i++)
    {
      int leftBitsInMask=subNetLength-i*8;
      byte left=toTest[i];
      byte right=subNetAddress[i];
      if(leftBitsInMask>=8)
      {
        if(left!=right) return false;
      }
      else if(leftBitsInMask<=0)
      {
        return true;
      }
      else
      {
        int mask=~((1<<(8-leftBitsInMask))-1);
        if((left&mask)!=(right&mask)) return false;
      }
    }
    return true;
  }
  
  /**
   * Check whether the given address is a LAN address.
   * @param address address to check.
   * @return true if address belongs to the LAN, false otherwise.
   * @throws SocketException in case of problem.
   */
  public static boolean isFromLAN(InetAddress address) throws SocketException
  {
    Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
    if(en==null) return false;
    
    while(en.hasMoreElements())
    {
      NetworkInterface inter=en.nextElement();
      for(InterfaceAddress interfaceAddress:inter.getInterfaceAddresses())
      {
        byte[] add=interfaceAddress.getAddress().getAddress();
        short length=interfaceAddress.getNetworkPrefixLength();
        if (length >= 0) {
          if (isWithinSubNet(address.getAddress(), add, length)) return true;
        }
      }
    }
    return false;
  }
  
  /**
   * Check whether the given address matches the list of given hosts.
   * @param address address to test.
   * @param hosts list of hosts. Each host can be an IP address or a host name, and each item can also be postifxed with a mask length separated with the '/' character. For instance: localhost, 192.168.0.0/16 or www.acrosoft.be
   * @return true if the address matches at least one of the possible hosts.
   */
  public static boolean isWithinList(InetAddress address,List<String> hosts)
  {
    for(String host:hosts)
    {
      String hostname=host.trim();
      short length=-1;
      
      int sep=hostname.indexOf('/');
      if(sep>=0)
      {
        try
        {
          length=Short.parseShort(hostname.substring(sep+1).trim());
          hostname=hostname.substring(0,sep).trim();
        }
        catch(NumberFormatException ex)
        {
          //Invalid host, ignore this entry
          continue;
        }
      }
      
      try
      {
        InetAddress[] addresses=InetAddress.getAllByName(hostname);
        for(InetAddress allowedAddress:addresses)
        {
          if(length>=0)
          {
            if(isWithinSubNet(address.getAddress(),allowedAddress.getAddress(),length))
            {
              return true;
            }
          }
          else
          {
            if(address.equals(allowedAddress))
            {
              return true;
            }
          }
        }
      }
      catch(UnknownHostException ex)
      {
        //Invalid host, ignore this entry
        continue;
      }
    }
    return false;
  }
   
}
