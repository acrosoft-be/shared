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
package be.acrosoft.gaia.shared.dispatch;

import be.acrosoft.gaia.shared.util.GaiaRuntimeException;

/**
 * Main event dispatching provider. Most of the actual implementation is delegated to an invoker specified through the init method.
 * <br/>
 * This class is mostly a singleton wrapper on top of {@link AsyncInvoker}. It is simple to call with all the
 * static methods, but does not support multiple instances of invokers to be used in parallel.
 */
public class Dispatcher
{
  private static AsyncInvoker _invoker=null;
  private static Object _lock=new Object();
  
  /**
   * Initialize the dispatcher with the given invoker. This method should be called before any call to dispatch or call.
   * Note that if an invoker is already configured, it is disposed and the new invoker replaces it.
   * @param invoker dispatch thread invoker.
   */
  public static void init(AsyncInvoker invoker)
  {
    synchronized(_lock)
    {
      if(_invoker!=null)
        _invoker.dispose();
      _invoker=invoker;
    }
  }
  
  /**
   * Check whether the dispatcher has been initialized.
   * @return true if the dispatched is initialized, false otherwise.
   */
  public static boolean isInitialized()
  {
    synchronized(_lock)
    {
      return _invoker!=null;
    }
  }
  
  /**
   * Get the invoker, or null if the Dispatcher has not been initialized.
   * @return invoker.
   */
  public static AsyncInvoker getInvoker()
  {
    synchronized(_lock)
    {
      return _invoker;
    }
  }
    
  /**
   * Dispatch the given runnable object into the dispatch thread as soon as possible and return immediately.
   * @param run runnable to dispatch.
   */
  public static void dispatch(Runnable run)
  {
    if(!isInitialized()) throw new GaiaRuntimeException(GaiaRuntimeException.RootCause.INTERNAL_ERROR);
    getInvoker().dispatch(run);
  }
  
  /**
   * Execute the given runnable object into the dispatch thread as soon as possible, and return when it is done.
   * @param run runnable to call.
   */
  public static void call(Runnable run)
  {
    if(!isInitialized()) throw new GaiaRuntimeException(GaiaRuntimeException.RootCause.INTERNAL_ERROR);
    getInvoker().call(run);
  }
  
  /**
   * Check whether the current thread is the dispatch thread.
   * @return true if the calling thread is the dispatch thread, false otherwise.
   */
  public static boolean isDispatchThread()
  {
    if(!isInitialized()) return false;
    return getInvoker().isDispatchThread();
  }
  
  /**
   * Report an exception that cannot be handled and that occurred in another thread.
   * @param ex exception.
   */
  public static void reportException(Throwable ex)
  {
    dispatch(()->{throw new GaiaRuntimeException(ex);});
  }
  
  /**
   * Yield the calling thread without preventing the dispatched objects from being executed.
   * @param block if true, the calling thread can be blocked until the next object gets
   * dispatched.
   * @return true if some objects were actually dispatched, false otherwise.
   */
  public static boolean yield(boolean block)
  {
    if(!isDispatchThread())
    {
      if(!block)
        Thread.yield();
      else
      {
        try
        {
          Thread.sleep(10);
        }
        catch(InterruptedException ex)
        {
          throw new GaiaRuntimeException(ex);
        }
      }
      return false;
    }
    
    //This should not really happen in practice as isDispatchThread would have returned false in
    //that case, but you never know, and this is possible in theory because of race conditions...
    if(!isInitialized()) throw new GaiaRuntimeException(GaiaRuntimeException.RootCause.INTERNAL_ERROR);
    return getInvoker().yield(block);
  }
  
  /**
   * Wait until all dispatched objects are executed.
   */
  public static void flush()
  {
    if(!isInitialized()) return;
    getInvoker().flush();
  }
  
  /**
   * Wait for the given thread to terminate without preventing the objects
   * to be dispatched.
   * @param t thread to join.
   * @throws InterruptedException in case waiting has been interrupted.
   */
  public static void join(Thread t) throws InterruptedException
  {
    while(t.isAlive())
    {
      if(!yield(false))
        Thread.sleep(10);
    }
  }
  
}
