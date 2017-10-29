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

import be.acrosoft.gaia.shared.util.GaiaRuntimeException;


/**
 * SimpleAsyncInvoker, that creates one (non-daemon) thread to dispatch the requests.
 */
public class SimpleAsyncInvoker implements AsyncInvoker
{
  private static class InvokeItem
  {
    /**
     * Runnable.
     */
    public Runnable runnable;
    /**
     * Is done.
     */
    public boolean done;
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
      done=false;
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
          _errorConsumer.accept(ex);
        }
      }
      if(item==null) return false;
      try
      {
        item.runnable.run();
        synchronized(item.lock)
        {
          item.done=true;
          item.lock.notifyAll();
        }
      }
      catch(Throwable ex)
      {
        synchronized(item.lock)
        {
          _errorConsumer.accept(ex);
          item.done=true;
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
   * dumped on the console.
   */
  public SimpleAsyncInvoker()
  {
    this(t->t.printStackTrace());
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
  public void dispatch(Runnable arg0)
  {
    _thread.add(new InvokeItem(arg0));
  }

  @Override
  public void call(Runnable arg0)
  {
    InvokeItem item=new InvokeItem(arg0);
    _thread.add(item);
    
    if(isDispatchThread())
    {
      boolean done=false;
      while(!done)
      {
        yield(true);
        synchronized(item.lock)
        {
          done=item.done;
        }
      }
    }
    
    synchronized(item.lock)
    {
      while(!item.done)
      {
        try
        {
          item.lock.wait();
        }
        catch(InterruptedException ex)
        {
          throw new GaiaRuntimeException(ex);
        }
      }
    }
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
  
  /**
   * Dispose the invoker.
   */
  @Override
  public void dispose()
  {
    flush();
    _thread.terminate();
    try
    {
      _thread.join(1000);
    }
    catch(InterruptedException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
  }
  
}
