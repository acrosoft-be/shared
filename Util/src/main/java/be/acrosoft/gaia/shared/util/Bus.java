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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   * @return the response object if the response has been received within the specified
   * timeout, null if the response is not available yet and the timeout
   * expired. Note that is a response is actually returned by this method, the
   * token is invalidated and cannot be used anymore.
   * @throws InterruptedException if the wait gets interrupted.
   */
  public Object getResponse(Token token,long timeout) throws InterruptedException
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
      return item.response;
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
}
