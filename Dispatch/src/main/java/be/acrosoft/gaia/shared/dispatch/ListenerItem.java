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
  
  private static boolean isWeak(Class<?> clazz)
  {
    if(clazz==null || clazz.isPrimitive()) return false;
    if(clazz.isAnnotationPresent(WeakListener.class)) return true;
    if(isWeak(clazz.getSuperclass())) return true;
    for(Class<?> i:clazz.getInterfaces())
    {
      if(isWeak(i)) return true;
    }
    return false;
  }
  
  /**
   * Create a new ListenerItem.
   * @param alistener listener.
   */
  public ListenerItem(T alistener)
  {
    if(isWeak(alistener.getClass()))
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
