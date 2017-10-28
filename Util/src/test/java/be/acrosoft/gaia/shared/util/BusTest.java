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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import be.acrosoft.gaia.shared.util.Bus.Token;
import be.acrosoft.gaia.shared.util.GaiaRuntimeException.RootCause;

@SuppressWarnings({"javadoc","nls"})
public class BusTest
{
  private static class ServerRunnable implements Runnable
  {
    private Bus _bus;
    private int _count;
    
    /**
     * Create a new ServerRunnable.
     * @param bus
     */
    public ServerRunnable(Bus bus)
    {
      _bus=bus;
      _count=0;
    }
    
    @Override
    public void run()
    {
      while(true)
      {
        try
        {
          Pair<Token,Object> request=_bus.getRequest(0);
          int v=(Integer)request.b;
          Token token=request.a;
          _bus.sendResponse(token,2*v);
          _count++;
        }
        catch(InterruptedException ex)
        {
          throw new GaiaRuntimeException(ex);
        }
      }
    }
    
    /**
     * .
     * @return count.
     */
    public int report()
    {
      return _count;
    }
  }
  
  private static class ClientRunnable implements Runnable
  {
    private Bus _bus;
    
    /**
     * Create a new ServerRunnable.
     * @param bus
     */
    public ClientRunnable(Bus bus)
    {
      _bus=bus;
    }

    @Override
    public void run()
    {
      try
      {
        for(int i=0;i<1000;i++)
        {
          Token token=_bus.sendRequest(i);
          Object response=_bus.getResponse(token,0);
          int v=(Integer)response;
          if(v!=i*2) throw new GaiaRuntimeException(RootCause.INTERNAL_ERROR);
        }
      }
      catch(InterruptedException ex)
      {
        throw new GaiaRuntimeException(ex);
      }
    }
  }
  
  @Test
  public void testBus()
  {
    final Bus bus=new Bus();

    ServerRunnable[] serversRunnable=new ServerRunnable[20];
    Thread[] servers=new Thread[serversRunnable.length];
    for(int i=0;i<servers.length;i++)
    {
      serversRunnable[i]=new ServerRunnable(bus);
      servers[i]=new Thread(serversRunnable[i]);
      servers[i].setDaemon(true);
      servers[i].start();
    }
    
    Thread[] clients=new Thread[20];
    for(int i=0;i<clients.length;i++)
    {
      clients[i]=new Thread(new ClientRunnable(bus));
      clients[i].start();
    }
    
    for(int i=0;i<clients.length;i++)
    {
      try
      {
        clients[i].join();
      }
      catch(InterruptedException ex)
      {
        throw new GaiaRuntimeException(ex);
      }
    }
    
    int total=0;
    for(int i=0;i<serversRunnable.length;i++)
    {
      total+=serversRunnable[i].report();
    }
    assertEquals(1000*20,total);
  }
  
  @Test
  public void testCallBack()
  {
    final Bus b=new Bus();

    class CallBack implements Runnable
    {
      public volatile Token token;
      @Override
      public void run()
      {
        try
        {
          b.getResponse(token,-1).equals("Response");
          token=null;
        }
        catch(InterruptedException ex)
        {
          fail(ex.toString());
        }
      }
    }
    
    
    Thread worker=new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          Token token=b.getRequest(0).a;
          b.sendResponse(token,"Response");
        }
        catch(InterruptedException ex)
        {
          org.junit.Assert.fail(ex.toString());
        }
      }
    });
    
    CallBack cb=new CallBack();
    cb.token=b.sendRequest("Request",cb);
    worker.start();
    try
    {
      worker.join();
    }
    catch(InterruptedException ex)
    {
      fail(ex.toString());
    }
    assertEquals(null,cb.token);
  }

  @Test(expected=GaiaRuntimeException.class)
  public void testInvalidTokenGetResponse() throws Exception
  {
    new Bus().getResponse(new Token(),0);
  }
  
  @Test(expected=GaiaRuntimeException.class)
  public void testInvalidTokenSendResponse() throws Exception
  {
    new Bus().sendResponse(new Token(),"hello");
  }
  
  @Test
  public void testNonBlockingNoResponse() throws Exception
  {
    Bus bus=new Bus();
    Token token=bus.sendRequest("hello");
    assertNull(bus.getResponse(token,-1));
  }

  @Test
  public void testBlockingNoResponse() throws Exception
  {
    Bus bus=new Bus();
    Token token=bus.sendRequest("hello");
    assertNull(bus.getResponse(token,10));
  }

  @Test
  public void testBlockingHasResponse() throws Exception
  {
    Bus bus=new Bus();
    Token token=bus.sendRequest("hello");
    bus.sendResponse(token,"world");
    assertEquals("world",bus.getResponse(token,10));
  }
  
  @Test
  public void testGetRequest() throws Exception
  {
    Bus bus=new Bus();
    assertNull(bus.getRequest(-1));
    assertNull(bus.getRequest(10));
    bus.sendRequest("hello");
    assertEquals("hello",bus.getRequest(-1).b);
    bus.sendRequest("hello");
    assertEquals("hello",bus.getRequest(10).b);
  }
}
