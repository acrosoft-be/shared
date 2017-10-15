package be.acrosoft.gaia.shared.dispatch;

/**
 * This class is an helper class that can be used to merge asynchronous operations
 * back to synchronous calls. It emulates a synchronous operation with the following
 * characteristics: it has a result type of R, throws a checked exception of type T,
 * and also throws unchecked exception.
 * <br/>
 * Typically, a thread will call the getResult method while another thread performs
 * the actual operation. The getResult method blocks until another thread calls
 * setResult, setVoidResult, setThrowable or setRuntimeException, whatever comes first.
 * <br/>
 * It is similar in principle to {@link java.util.concurrent.Future} except that this
 * class forwards exceptions.
 * 
 * @param <R> result type.
 * @param <T> throwable type.
 */
public class Future<R,T extends Throwable>
{
  private R _result;
  private T _throwable;
  private RuntimeException _runtimeException;
  private boolean _finished;
  private Object _lock;
  
  /**
   * Create a new Future.
   */
  public Future()
  {
    _result=null;
    _throwable=null;
    _runtimeException=null;
    _finished=false;
    _lock=new Object();
  }
  
  private R getResultInternal() throws T
  {
    if(_throwable!=null) throw _throwable;
    if(_runtimeException!=null) throw _runtimeException;
    return _result;
  }
  
  /**
   * Get the result. This method will block until setResult, setVoidResult,
   * setThrowable or setRuntimeException is called by another thread, whatever
   * comes first. If setResult is called, the result will be returned. If
   * setVoidResult is called, null will be returned. If setThrowable or
   * setRuntimeException is called, this exception will be thrown.
   * @return the result.
   * @throws T if the operation thrown an exception.
   * @throws InterruptedException if the wait was interrupted.
   */
  public R getResult() throws T,InterruptedException
  {
    return getResult(0);
  }

  /**
   * Get the result. This method will block until setResult, setVoidResult,
   * setThrowable or setRuntimeException is called by another thread, or
   * after timeOut milliseconds, whichever comes first. If a timeOut of
   * 0 millisecond is specified, the method waits forever.
   * If setResult is called, the result will be returned. If
   * setVoidResult is called, null will be returned. If setThrowable or
   * setRuntimeException is called, this exception will be thrown.
   * If the timeout expires, null will be returned.
   * @param timeOut time out.
   * @return the result.
   * @throws T if the operation thrown an exception.
   * @throws InterruptedException if the wait was interrupted.
   */
  public R getResult(long timeOut) throws T,InterruptedException
  {
    long expiry=System.currentTimeMillis()+timeOut;
    
    if(Dispatcher.isDispatchThread())
    {
      while(!isResultAvailable())
      {
        if(timeOut>0 && System.currentTimeMillis()>=expiry)
        {
          return null;
        }
        Dispatcher.yield(false);
        Thread.sleep(10);
      }
      return getResultInternal();
    }

    synchronized(_lock)
    {
      while(!_finished)
      {
        if(timeOut>0)
        {
          long remaining=expiry-System.currentTimeMillis();
          if(remaining<=0)
          {
            return null;
          }
          _lock.wait(remaining);
        }
        else
        {
          _lock.wait();
        }
      }
      return getResultInternal();
    }
  }
  
  /**
   * Set the result.
   * @param result the result.
   */
  public void setResult(R result)
  {
    synchronized(_lock)
    {
      _finished=true;
      _result=result;
      _lock.notifyAll();
    }
  }
  
  /**
   * Set the void result.
   */
  public void setVoidResult()
  {
    setResult(null);
  }
  
  /**
   * Set the throwable.
   * @param th throwable.
   */
  public void setThrowable(T th)
  {
    synchronized(_lock)
    {
      _finished=true;
      _throwable=th;
      _lock.notifyAll();
    }
  }

  /**
   * Set the runtime exception.
   * @param runtimeException runtime exception.
   */
  public void setRuntimeException(RuntimeException runtimeException)
  {
    synchronized(_lock)
    {
      _finished=true;
      _runtimeException=runtimeException;
      _lock.notifyAll();
    }
  }
  
  /**
   * Check whether a result is available and that getResult will be able
   * to return immediately.
   * @return true if getResult will return immediately, by either returning
   * a value or throwing an exception.
   */
  public boolean isResultAvailable()
  {
    synchronized(_lock)
    {
      return _finished;
    }
  }
}
