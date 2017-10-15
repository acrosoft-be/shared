package be.acrosoft.gaia.shared.dispatch;

import java.lang.ref.WeakReference;

/**
 * This class is a holder to a particular Listener. If the Listener has the WeakListener annotation, then
 * the listener will only be kept via a WeakReference.
 * @param <T> Listener interface.
 */
class ListenerItem<T extends Listener>
{
  /**
   * Listener.
   */
  private T listener;
  /**
   * Weak listener, to be used if listener is null.
   */
  private WeakReference<T> weakListener;
  /**
   * Stack trace.
   */
  //public StackTraceElement[] stack;

  private boolean _removed;
  
  /**
   * Create a new ListenerItem.
   * @param alistener listener.
   */
  public ListenerItem(T alistener)
  {
    if(alistener.getClass().getAnnotation(WeakListener.class)!=null)
    {
      weakListener=new WeakReference<T>(alistener);
    }
    else
    {
      listener=alistener;
    }
    //stack=Thread.currentThread().getStackTrace();
    _removed=false;
  }
  
  /**
   * Get the listener, or null if listener has been collected.
   * @return listener, or null.
   */
  public T getListener()
  {
    if(listener!=null) return listener;
    return weakListener.get();
  }
    
  /**
   * Check whether this item has been removed and does not reflect any actual
   * listener.
   * @return true if item is removed, false otherwise.
   */
  public synchronized boolean isRemoved()
  {
    return _removed;
  }
  
  /**
   * Set this item as removed.
   */
  public synchronized void setRemoved()
  {
    _removed=true;
  }
  
}
