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

import java.util.function.Supplier;

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
   * Dispatch the given supplier object into the dispatch thread as soon as possible and return immediately. The
   * supplier's result is returned as a future. Exceptions thrown by the supplier will not be captured by the
   * future, but will instead be propagated to the invoker.
   * @param supplier supplier to dispatch.
   * @return future value for the supplier's result.
   */
  public static <R> Future<R,RuntimeException> dispatch(Supplier<R> supplier)
  {
    Future<R,RuntimeException> future=new Future<>();
    dispatch((()->
    {
      future.setResult(supplier.get());
    }));
    return future;
  }
    
  /**
   * Dispatch the given runnable object into the dispatch thread as soon as possible and return immediately.
   * Exceptions thrown by the runnable's run method will be propagated to the invoker.
   * @param run runnable to dispatch.
   */
  public static void dispatch(Runnable run)
  {
    if(!isInitialized()) throw new GaiaRuntimeException(GaiaRuntimeException.RootCause.INTERNAL_ERROR);
    getInvoker().dispatch(run);
  }
  
  /**
   * A throwing supplier.
   * @param <R> supplier's return type.
   * @param <T> thrown exception.
   */
  public static interface ThrowingSupplier<R,T extends Throwable>
  {
    /**
     * Get the value.
     * @return value.
     * @throws T exception.
     */
    public R get() throws T;
  }
  
  /**
   * A throwing runnable.
   * @param <T> thrown exception.
   */
  public static interface ThrowingRunnable<T extends Throwable>
  {
    /**
     * Run.
     * @throws T exception.
     */
    public void run() throws T;
  }
  
  /**
   * Execute the given runnable object into the dispatch thread as soon as possible, and return when it is done. If
   * an exception T is thrown by the runnable, it will be thrown back by this method.
   * @param run runnable to call.
   * @throws T exception type.
   */
  public static <T extends Throwable> void call(ThrowingRunnable<T> run) throws T
  {
    call((ThrowingSupplier<Void,T>)()->{run.run();return null;});
  }
  
  /**
   * Execute the given supplier into the dispatch thread as soon as possible, and return the supplier's value when
   * the execution is completed. Any exception T thrown by the supplier will be thrown back by this method.<br>
   * Practically, this method allows to call another method or lambda within the dispatch thread as if it was
   * call synchronously from the calling's thread.
   * @param supplier supplier.
   * @return supplier's result.
   * @throws T supplier's exception if thrown.
   */
  public static <R,T extends Throwable> R call(ThrowingSupplier<R,T> supplier) throws T
  {
    if(isDispatchThread()) return supplier.get();
    Future<R,T> future=new Future<>();
    dispatch(()->
    {
        try
        {
          future.setResult(supplier.get());
        }
        catch(Throwable ex)
        {
          future.setThrowable((T)ex);
        }
    });
    
    try
    {
      return future.getResult();
    }
    catch(InterruptedException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
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
   * Report an exception that cannot be handled and that occurred in another thread. It will be propagated to
   * the invoker.
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
   * @return true if some objects were actually dispatched, false otherwise (or if unknown).
   * @see AsyncInvoker#yield(boolean)
   */
  public static boolean yield(boolean block)
  {
    if(!isInitialized()) return false;
    return getInvoker().yield(block);
  }
  
  /**
   * Wait until all dispatched objects are executed.
   * @see AsyncInvoker#flush()
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
