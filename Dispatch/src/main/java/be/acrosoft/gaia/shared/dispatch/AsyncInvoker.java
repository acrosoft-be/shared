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

import java.util.concurrent.Executor;

/**
 * This interface provides services needed for asynchronous method invocation. There is an assumption
 * that the invocation will take place from within a (single) background thread.<br>
 * Methods are full thread-safe and can be called directly from within the background thread if
 * needed (that is, the methods are reentrant).<br>
 * Invocations will take place in FIFO as much as possible. Some methods will inherently cause this
 * FIFO property to break if called from the background thread.<br>
 * Exceptions thrown during invocations are expected to be handled by the AsyncInvoker in an
 * implementation-dependent manner.
 */
public interface AsyncInvoker extends Executor
{
  @Override
  default public void execute(Runnable command)
  {
    dispatch(command);
  }
  
  /**
   * Dispatch the given runnable object into the dispatch thread as soon as possible and return immediately,
   * typically before the runnable has been executed. Any exception thrown by the runnable's run method
   * will be handled in an implementation-dependent manner.
   * @param run runnable to dispatch.
   */
  public void dispatch(Runnable run);
  
  /**
   * Return whether the calling thread is the dispatch thread.
   * @return true if the calling thread is the dispatch thread, false otherwise.
   */
  public boolean isDispatchThread();
  
  /**
   * Do nothing but does not prevent the runnable objects from being dispatched. It is typically used to
   * wait from within the background thread without preventing other executions.
   * Calling this method outside of the background thread will cause the thread to yield or sleep
   * for a little while if block is set to true.<br/>
   * @param block if true, this method can block until the next object is dispatched.
   * @return true if some runnable objects were actually dispatched, false otherwise.
   */
  public boolean yield(boolean block);
  
  /**
   * Wait until all dispatched objects are executed.
   * Using this method from the background thread is allowed, but not advised as this will cause FIFO to be
   * temporarily violated.<br/>
   * This method is only a "best effort" service as new objects may be dispatched for execution during its
   * execution. Therefore this method will do reasonable effort to dispatch any pending objects, but
   * it is not guaranteed that no objects are pending execution by the time it returns.
   */
  public void flush();
  
  /**
   * Dispose the invoker. No further methods should be called on this object once disposed. This will
   * typically terminate the background thread.
   */
  public void dispose();
  
}
