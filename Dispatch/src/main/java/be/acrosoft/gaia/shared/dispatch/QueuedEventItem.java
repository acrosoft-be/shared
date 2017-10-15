package be.acrosoft.gaia.shared.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import be.acrosoft.gaia.shared.dispatch.ListenerMergeStrategy.MergeAction.Operator;
import be.acrosoft.gaia.shared.util.GaiaRuntimeException;

/**
 * QueuedEventItem. This contains shared code between Listener, ListenerItem and ListenerMergeStrategy.
 * @param <K> listener type.
 */
class QueuedEventItem<K extends Listener> implements Runnable
{
  private ListenerItem<K> _item;
  private K _listener;
  private Method _m;
  private Object[] _params;
  private boolean _invoked;
  private ListenerMergeStrategy<K> _mergeStrategy;
  private Object _queuingReference;

  /**
   * Create a new QueuedEventItem.
   * @param item item.
   * @param listener strong reference to the listener as the item could contain
   * nothing but a weak reference to it (we don't want to loose the reference once
   * the item is queued...).
   * @param mergeStrategy merge strategy.
   * @param m method to call.
   * @param params method parameters.
   */
  public QueuedEventItem(ListenerItem<K> item,K listener,ListenerMergeStrategy<K> mergeStrategy,Method m,Object... params)
  {
    _listener=listener;
    _mergeStrategy=mergeStrategy;
    _invoked=false;
    _item=item;
    _m=m;
    _params=params;
    if(_mergeStrategy!=null)
      _queuingReference=_mergeStrategy.enqueue(this);
  }
  
  /**
   * Get the target listener.
   * @return listener.
   */
  public K getListener()
  {
    return _listener;
  }
  
  /**
   * Get the parameters.
   * @return event parameters.
   */
  public Object[] getParameters()
  {
    return _params;
  }
  
  /**
   * Get the method.
   * @return method.
   */
  public Method getMethod()
  {
    return _m;
  }
  
  /**
   * Try to merge the given event with this one.
   * @param operator merge operator.
   * @param listener listener.
   * @param m method to call.
   * @param params event parameter.
   * @return true if event was merged with this one, false otherwise.
   */
  public synchronized boolean tryMerge(Operator operator,K listener,Method m,Object[] params)
  {
    if(_invoked) return false;
    
    if(!m.equals(_m)) return false;
    if(_item.isRemoved()) return false;
    if(listener==_listener)
    {
      Object[] newParams = operator.apply(_params,params);
      if(newParams==null) return false;
      _params=newParams;
      return true;
    }
    return false;
  }
  
  @Override
  public synchronized void run()
  {
    _invoked=true;
    try
    {
      if(_queuingReference!=null)
        _mergeStrategy.dequeue(this,_queuingReference);
      
      if(!_item.isRemoved())
      {
        _m.invoke(_listener,_params);
      }
    }
    catch(InvocationTargetException ex)
    {
      if(ex.getTargetException() instanceof RuntimeException) throw (RuntimeException)ex.getTargetException();
      if(ex.getTargetException() instanceof Error) throw (Error)ex.getTargetException();
      throw new GaiaRuntimeException(ex);
    }
    catch(Exception ex)
    {
      throw new GaiaRuntimeException(ex);
    }
  }

}
