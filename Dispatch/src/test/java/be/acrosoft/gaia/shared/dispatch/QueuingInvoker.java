package be.acrosoft.gaia.shared.dispatch;

import java.util.LinkedList;
import java.util.List;

import be.acrosoft.gaia.shared.util.GaiaRuntimeException;

/**
 * QueuingInvoker.
 */
@SuppressWarnings({"javadoc"})
public class QueuingInvoker implements AsyncInvoker
{
  private List<Runnable> _list=new LinkedList<Runnable>();
  private boolean _sink=false;
  private Thread _thread;
  
  public QueuingInvoker()
  {
    _thread=Thread.currentThread();
  }
  
  @Override
  public void call(Runnable run)
  {
    dispatch(run);
    flush();
  }

  @Override
  public synchronized void dispatch(Runnable run)
  {
    if(_sink) return;
    _list.add(run);
  }

  public void flushOne()
  {
    if(Dispatcher.isDispatchThread())
    {
      List<Runnable> copy;
      synchronized(this)
      {
        copy=new LinkedList<Runnable>(_list);
        _list.clear();
      }
      for(Runnable run:copy)
        run.run();
    }
  }
  
  @Override
  public void flush()
  {
    if(Dispatcher.isDispatchThread())
    {
      while(_list.size()>0)
      {
        flushOne();
      }
    }
    else
    {
      while(getListSize()>0)
      {
        try
        {
          Thread.sleep(100);
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
    synchronized(this)
    {
      if(_list.size()==0) return false;
    }
    flush();
    return true;
  }
  
  public synchronized int getListSize()
  {
    return _list.size();
  }

  public synchronized void setSink(boolean sink)
  {
    _sink=sink;
  }

  @Override
  public void dispose()
  {
    flush();
    _list.clear();
    _sink=true;
  }
  
}
