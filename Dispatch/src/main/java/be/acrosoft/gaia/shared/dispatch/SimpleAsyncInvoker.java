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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.acrosoft.gaia.shared.util.GaiaRuntimeException;


/**
 * SimpleAsyncInvoker, that creates one (non-daemon) thread to dispatch the requests.
 */
public class SimpleAsyncInvoker implements AsyncInvoker
{
  private static final Logger LOGGER=Logger.getLogger(SimpleAsyncInvoker.class.getName());

  private static class InvokeItem
  {
    /**
     * Runnable.
     */
    public Runnable runnable;
    /**
     * Lock.
     */
    public Object lock;
    
    /**
     * Create a new InvokeItem.
     * @param arunnable runnable.
     */
    public InvokeItem(Runnable arunnable)
    {
      runnable=arunnable;
      lock=new Object();
    }
  }
  
  private static class InvokeThread extends Thread
  {
    private List<InvokeItem> _list=new LinkedList<InvokeItem>();
    private boolean _terminated=false;
    private Consumer<Throwable> _errorConsumer;
    
    /**
     * Create a new InvokeThread.
     * @param errorConsumer the error consumer where all unexpected errors are sent to.
     */
    public InvokeThread(Consumer<Throwable> errorConsumer)
    {
      super("Simple invoker thread"); //$NON-NLS-1$
      _errorConsumer=errorConsumer;
    }

    /**
     * Terminate the InvokeThread.
     */
    public void terminate()
    {
      synchronized(_list)
      {
        _terminated=true;
        _list.notify();
      }
    }
    
    /**
     * Add the given item to dispatch.
     * @param item item to add.
     */
    public void add(InvokeItem item)
    {
      synchronized(_list)
      {
        _list.add(item);
        _list.notify();
      }
    }
    
    /**
     * Get the number of pending items to dispatch.
     * @return pending count.
     */
    public int getSize()
    {
      synchronized(_list)
      {
        return _list.size();
      }
    }
    
    /**
     * Return true if main loop should exit, false otherwise.
     * @return return true if main loop should exit, false otherwise.
     */
    public boolean terminated()
    {
      synchronized(_list)
      {
        return _terminated;
      }
    }
    
    /**
     * Perform one dispatch if available, do nothing otherwise.
     * @param block true if method can block if nothing to do, false otherwise.
     * @return return true if actual job was done, false otherwise.
     */
    public boolean doOne(boolean block)
    {
      InvokeItem item=null;
      synchronized(_list)
      {
        try
        {
          if(!block && _list.size()==0) return false;
          while(_list.size()==0)
          {
            if(_terminated) return false;
            _list.wait();
          }
          item=_list.remove(0);
        }
        catch(InterruptedException ex)
        {
          //Not quite sure it is a good idea to let this thread die here...
          try
          {
            _errorConsumer.accept(ex);
          }
          catch(Throwable th)
          {
            LOGGER.log(Level.SEVERE,"Throwable thrown from within the error consumer",th); //$NON-NLS-1$
          }
        }
      }
      if(item==null) return false;
      try
      {
        item.runnable.run();
        synchronized(item.lock)
        {
          item.lock.notifyAll();
        }
      }
      catch(Throwable ex)
      {
        synchronized(item.lock)
        {
          try
          {
            _errorConsumer.accept(ex);
          }
          catch(Throwable th)
          {
            LOGGER.log(Level.SEVERE,"Throwable thrown from within the error consumer",th); //$NON-NLS-1$
          }
          item.lock.notifyAll();
        }
      }
      return true;
    }
    
    @Override
    public void run()
    {
      while(true)
      {
        doOne(true);
        if(terminated()) return;
      }
    }
  }
  
  /**
   * Create a new SimpleAsyncInvoker. Exceptions thrown from a runnable will be ignored and simply
   * logged as warnings.
   */
  public SimpleAsyncInvoker()
  {
    this(t->LOGGER.log(Level.WARNING,"Uncaught exception in SimpleAsyncInvoker",t)); //$NON-NLS-1$
  }
  
  /**
   * Create a new SimpleAsyncInvoker using the given error consumer. Any exception thrown from a runnable
   * will be reported to this consumer from within the invoker thread.
   * @param errorConsumer error consumer.
   */
  public SimpleAsyncInvoker(Consumer<Throwable> errorConsumer)
  {
    _thread=new InvokeThread(errorConsumer);
    _thread.start();
  }

  private InvokeThread _thread;
  
  @Override
  public void dispatch(Runnable run)
  {
    _thread.add(new InvokeItem(run));
  }

  @Override
  public boolean isDispatchThread()
  {
    return Thread.currentThread().equals(_thread);
  }

  @Override
  public boolean yield(boolean block)
  {
    if(isDispatchThread())
      return _thread.doOne(block);

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

  @Override
  public void flush()
  {
    while(_thread.getSize()>0) yield(true);
  }
  
  @Override
  public void dispose()
  {
    flush();
    _thread.terminate();
    try
    {
      _thread.join(1000);
      if(_thread.isAlive()) LOGGER.warning("SimplerAsyncInvoker thread still alive after timeout");  //$NON-NLS-1$
    }
    catch(InterruptedException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
  }
  
}
