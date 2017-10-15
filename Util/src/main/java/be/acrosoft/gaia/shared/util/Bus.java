package be.acrosoft.gaia.shared.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.acrosoft.gaia.shared.util.GaiaRuntimeException.RootCause;

/**
 * The Bus implements the asynchronous Query/Answer paradigm. FIFO is not guaranteed.
 * All methods of the Bus class are multi-thread safe.
 */
public class Bus
{
  /**
   * Opaque reconciliation token.
   */
  public static class Token
  {
  }
  
  private static class BusItem
  {
    /**
     * Token.
     */
    public Token token;
    /**
     * Request.
     */
    public Object request;
    /**
     * Whether response is available.
     */
    public boolean hasResponse;
    /**
     * Response.
     */
    public Object response;
    /**
     * Sync response callback
     */
    public Runnable callBack;
    
    /**
     * Create a new BusItem.
     * @param atoken token.
     * @param arequest request.
     * @param acallBack callback.
     */
    public BusItem(Token atoken,Object arequest,Runnable acallBack)
    {
      token=atoken;
      request=arequest;
      callBack=acallBack;
      hasResponse=false;
      response=null;
      
    }
  }
  
  private List<BusItem> _pendingItems;
  private Map<Token,BusItem> _items;
  private Object _lock;
  
  /**
   * Create a new Bus.
   */
  public Bus()
  {
    _pendingItems=new ArrayList<BusItem>();
    _items=new HashMap<Token,BusItem>();
    _lock=new Object();
  }
  
  /**
   * Send a request.
   * @param request request to send.
   * @return reconciliation token. This token must be used when calling
   * the getResponse method.
   */
  public Token sendRequest(Object request)
  {
    return sendRequest(request,null);
  }
  
  /**
   * Send a request.
   * @param request request to send.
   * @param callBack sync callback, which runnable method will be called synchronously
   * from the thread calling sendResponse when the response is available.
   * @return reconciliation token. This token must be used when calling
   * the getResponse method.
   */
  public Token sendRequest(Object request,Runnable callBack)
  {
    Token token=new Token();
    BusItem item=new BusItem(token,request,callBack);
    synchronized(_lock)
    {
      _pendingItems.add(item);
      _items.put(token,item);
      _lock.notify();
    }
    return token;
  }
  
  /**
   * Get the response of a previously sent request.
   * @param token token.
   * @param timeout maximum amount to wait for the response if it is not available
   * yet. If timeout is equal to 0, infinite wait is done. If timeout is negative,
   * no wait is done (ie, non-blocking operation).
   * @return <true,response> if the response has been received within the specified
   * timeout, <false,null> if the response is not available yet and the timeout
   * expired. Note that is a response is actually returned by this method, the
   * token is invalidated and cannot be used anymore.
   * @throws InterruptedException if the wait gets interrupted.
   */
  public Pair<Boolean,Object> getResponse(Token token,long timeout) throws InterruptedException
  {
    synchronized(_lock)
    {
      BusItem item=_items.get(token);
      if(item==null) throw new GaiaRuntimeException(new IllegalArgumentException());
      
      if(timeout<0)
      {
        if(!item.hasResponse) return null;
      }
      else if(timeout>0)
      {
        if(!item.hasResponse)
          _lock.wait(timeout);
        if(!item.hasResponse) return null;
      }
      else
      {
        while(!item.hasResponse)
          _lock.wait(timeout);
      }
      
      _items.remove(token);
      return new Pair<Boolean,Object>(true,item.response);
    }
  }
  
  /**
   * Get the next request.
   * @param timeout maximum amount to wait for a request if none is available.
   * If timeout is equal to 0, infinite wait is done. If timeout is negative,
   * no wait is done (ie, non-blocking operation).
   * @return <Token,Request> if a request was received within the timeout, or
   * null if not request was available and the timeout expired.
   * @throws InterruptedException if the wait gets interrupted.
   */
  public Pair<Token,Object> getRequest(long timeout) throws InterruptedException
  {
    synchronized(_lock)
    {
      if(timeout<0)
      {
        if(_pendingItems.size()==0) return null;
      }
      else if(timeout>0)
      {
        if(_pendingItems.size()==0)
          _lock.wait(timeout);
        if(_pendingItems.size()==0) return null;
      }
      else
      {
        while(_pendingItems.size()==0)
          _lock.wait(timeout);
      }
      
      BusItem first=_pendingItems.remove(0);
      
      return new Pair<Token,Object>(first.token,first.request);
    }
  }
  
  /**
   * Send a response to a previously received request.
   * @param token request token.
   * @param response response.
   */
  public void sendResponse(Token token,Object response)
  {
    BusItem item;
    synchronized(_lock)
    {
      item=_items.get(token);
      if(item==null) throw new GaiaRuntimeException(new IllegalArgumentException());
      item.response=response;
      item.hasResponse=true;
      _lock.notifyAll();
    }
    if(item.callBack!=null)
      item.callBack.run();
  }
    
  
    /////////////////////////
   // TEST TEST TEST TEST //
  /////////////////////////
  
  
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
      System.out.println(this+" handled "+_count+" requests"); //$NON-NLS-1$ //$NON-NLS-2$
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
          Pair<Boolean,Object> response=_bus.getResponse(token,0);
          if(!response.a) throw new GaiaRuntimeException(RootCause.INTERNAL_ERROR);
          int v=(Integer)response.b;
          if(v!=i*2) throw new GaiaRuntimeException(RootCause.INTERNAL_ERROR);
        }
      }
      catch(InterruptedException ex)
      {
        throw new GaiaRuntimeException(ex);
      }
    }
  }

  /**
   * Test.
   * @param args
   */
  public static void main(String[] args)
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
    System.out.println("total = "+total); //$NON-NLS-1$
  }
}
