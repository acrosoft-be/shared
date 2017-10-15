package be.acrosoft.gaia.shared.dispatch;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import be.acrosoft.gaia.shared.util.Debug;

/**
 * The listener interface is both the super-class for all the listeners as well
 * as a delegate for listener groups.
 * <p/>
 * This class allows to manage group of listeners, adding and removing listeners as well as invoking
 * listener's methods in a completely type-safe approach.
 * <p/>
 * To define a new listener type, a new interface must be created, extending Listener. Any additional method may
 * be defined as long as they do not conflict with the default methods defined in Listener.
 * <p/>
 * Default methods (add/remove...) should never be invoked directly on listener instances, as this will cause
 * an #IllegalAccessError.
 * <p/>
 * To create a group of listeners and invoke their methods in a grouped approach, a listener group must
 * first be created using {@link Listener#groupOf(Class)}. This method will return an instance of listener
 * that will act as a delegate: any invocation of the listener methods will be broadcasted to all listeners
 * in the group.
 * <p/>
 * The group itself is managed by invoking the default methods (add/remove...).
 * <p/>
 * Example:
 * <pre>
 * <code>
 * public static interface MyListener extends Listener {
 *    public void myStringEvent(String s);
 *    public void myIntEvent(int i);
 * }
 * 
 * ...
 * 
 * MyListener listener1 = ...;
 * MyListener listener2 = ...;
 * 
 * ...
 * 
 * MyListener group = Listener.groupOf(MyListener.class);
 * group.add(listener1);
 * group.add(listener2);
 * group.myStringEvent("Hello World!");
 * group.myIntEvent(42);
 * </code>
 * </pre>
 * <p>
 * There are several ways to remove a listener from a group after it has been added. One is to simply call the {@link Listener#remove(Listener)},
 * but this assumes the caller has a reference to the listener instance. This might not be the case if a lambda is used as a listener:
 * <code><pre>
 * group.add((s)->System.out.println(s)); //How to remove this listener?
 * </pre></code>
 * {@link Listener#add(Listener)}
 * The {@link Listener#add(Listener)} method returns a Handle that can be used to remove the listener using the {@link Handle#remove()} method.
 * Additionally, listeners can be given aliases upon addition to the group, and can be later removed via their aliases. This approach, however,
 * is not totally type-safe and is therefore discourage.
 * <p/>
 * How does it work?
 * <p/>
 * The {@link Listener#groupOf(Class)} method creates a #Proxy instance which contains the actual implementation for all
 * the group's methods. If the invoked method is one of the default methods of the Listener interface (add/remove...), then the specific
 * behavior is executed. For any other method, then the call is forwarded to all the listeners that are in the group at the time
 * of invocation. As a consequence, the interface default method's implementation is never really invoked.
 * <p/>
 * @param <T> actual listener type.
 */
public interface Listener<T extends Listener>
{
  /**
   * A handle to a listener registration.
   * @param <T> listener type.
   */
  public static interface Handle<T>
  {
    /**
     * Remove the registration.
     */
    public void remove();
    
    /**
     * Get the listener.
     * @return listener.
     */
    public T getListener();
    
    /**
     * Add an alias to the given handle. Note that since aliases are not type-safe, their usage is discouraged.
     * @param alias alias.
     * @return the same handle for chained execution.
     */
    public Handle<T> alias(Object... alias);
  }
  
  /**
   * Add a listener into the listener group.
   * @param listener listener.
   * @return handle.
   */
  public default Handle<T> add(T listener)
  {
    throw new IllegalAccessError();
  }
  
  /**
   * Remove a listener from the listener group.
   * @param listener listener.
   */
  public default void remove(T listener)
  {
    throw new IllegalAccessError();
  }
  
  /**
   * Remove all listeners using the given alias. Note that since aliases are not type-safe, their usage is discouraged.
   * @param alias alias.
   */
  public default void removeAll(Object... alias)
  {
    throw new IllegalAccessError();
  }
  
  /**
   * Add the given alias to the given listener. Note that since aliases are not type-safe, their usage is discouraged.
   * @param listener listener.
   * @param alias alias to give to listener.
   */
  public default void addAlias(T listener,Object... alias)
  {
    throw new IllegalAccessError();
  }
  
  /**
   * Remove all listeners.
   */
  public default void removeAll()
  {
    throw new IllegalAccessError();
  }
  
  /**
   * Check whether the given listener is part of the listener group.
   * @param listener listener.
   * @return true if listener is part of the listener group, false otherwise.
   */
  public default boolean isListening(T listener)
  {
    throw new IllegalAccessError();
  }
  
  /**
   * Get the number of registered listeners into this group.
   * @return size of the group.
   */
  public default int getListenerCount()
  {
    throw new IllegalAccessError();
  }
  
  /**
   * All the groups for debugging and leak analysis purpose.
   */
  public static HashSet<WeakReference<ProxyHandler>> ALL_LISTENER_GROUPS=new HashSet<>();
  
  /**
   * Proxy invocation handler used by the group. Made visible to allow declaring ALL_LISTENER_GROUPS.
   * @param <T> Listener type.
   */
  class ProxyHandler<T extends Listener<T>> implements InvocationHandler,Listener<T>
  {
    private static class AliasKey
    {
      private Object[] _objects;
      private int _hash;
      
      public AliasKey(Object[] objects)
      {
        _objects=new Object[objects.length];
        _hash=0;
        for(int i=0;i<objects.length;i++)
        {
          Object object=objects[i];
          if(object!=null)
          {
            _hash^=object.hashCode();
            object=new WeakReference(object);
          }
          _objects[i]=object;
        }
      }
      
      @Override
      public boolean equals(Object obj)
      {
        if(obj==null) return false;
        if(!(obj instanceof AliasKey)) return false;
        AliasKey other=(AliasKey)obj;
        if(_objects.length!=other._objects.length) return false;
        for(int i=0;i<_objects.length;i++)
        {
          Object a=_objects[i];
          Object b=other._objects[i];
          if(a!=b)
          {
            if(a==null || b==null) return false;
            if(a instanceof WeakReference && b instanceof WeakReference)
            {
              a=((WeakReference)a).get();
              b=((WeakReference)b).get();
              if(a!=b)
              {
                if(a==null || b==null) return false;
                if(!a.equals(b)) return false;
              }
            }
            else
            {
              if(!a.equals(b)) return false;
            }
          }
        }
        return true;
      }
      
      @Override
      public int hashCode()
      {
        return _hash;
      }
      
      @Override
      public String toString()
      {
        return Arrays.toString(_objects);
      }
    }
    
    private WeakHashMap<T,ArrayList<ListenerItem<T>>> _listeners;
    private Map<AliasKey,List<WeakReference<T>>> _aliases;
    private Object _lock=new Object();
    private ListenerMergeStrategy<T> _mergeStrategy;
    private long _delay;
    //private StackTraceElement[] _trace;
    
    
    public ProxyHandler(ListenerMergeStrategy strategy,long delay)
    {
      _mergeStrategy=strategy;
      _delay=delay;
      _listeners=new WeakHashMap<T,ArrayList<ListenerItem<T>>>(1);
      //_trace=Thread.currentThread().getStackTrace();
      synchronized(ALL_LISTENER_GROUPS)
      {
        ALL_LISTENER_GROUPS.add(new WeakReference<ProxyHandler>(this));
      }
    }
    
    @Override
    public int getListenerCount()
    {
      int ans=0;
      for(ArrayList<ListenerItem<T>> items:_listeners.values())
      {
        ans+=items.size();
      }
      return ans;
    }
    
    /*private static void stack(StackTraceElement[] stack)
    {
      for(int i=0;i<Math.min(stack.length,7);i++)
      {
        System.err.println("    "+stack[i]); //$NON-NLS-1$
      }
    }*/
    
    @Override
    public Handle<T> add(T listener)
    {
      synchronized(_lock)
      {
        ArrayList<ListenerItem<T>> list=_listeners.get(listener);
        ListenerItem<T> item=new ListenerItem<T>(listener);
        if(list==null)
        {
          list=new ArrayList<ListenerItem<T>>(1);
          _listeners.put(listener,list);
        }
        list.add(item);
      }
      return new Handle<T>()
      {
        @Override
        public Handle<T> alias(Object... aliases)
        {
          ProxyHandler.this.addAlias(getListener(),aliases);
          return this;
        }
        
        @Override
        public T getListener()
        {
          return listener;
        }
        
        @Override
        public void remove()
        {
          ProxyHandler.this.remove(getListener());
        }
      };
    }
    
    @Override
    public void removeAll()
    {
      synchronized(_lock)
      {
        WeakHashMap<T,ArrayList<ListenerItem<T>>> copy=new WeakHashMap<>(_listeners);
        for(T listener:copy.keySet())
        {
          remove(listener);
        }
      }
    }
    
    @Override
    public void remove(T listener)
    {
      if(listener==null) return;
      synchronized(_lock)
      {
        ArrayList<ListenerItem<T>> list=_listeners.get(listener);
        if(list==null) return;
        Iterator<ListenerItem<T>> it=list.iterator();
        while(it.hasNext())
        {
          ListenerItem<T> item=it.next();
          it.remove();
          item.setRemoved();
        }
        if(list.size()==0)
          _listeners.remove(listener);
        
        if(_aliases!=null)
        {
          _aliases.entrySet().forEach(entry->entry.getValue().removeIf(r->r.get()==null||r.get()==listener));
          _aliases.entrySet().removeIf(e->e.getValue().isEmpty());
          if(_aliases.size()==0)
          {
            _aliases=null;
          }
        }
      }
    }
    
    @Override
    public void addAlias(T listener,Object... alias)
    {
      AliasKey key=new AliasKey(alias);
      synchronized(_lock)
      {
        if(_aliases==null)
        {
          _aliases=new HashMap<>(1);
        }
        List<WeakReference<T>> list=_aliases.get(key);
        if(list==null)
        {
          list=new ArrayList<>(1);
          _aliases.put(key,list);
        }
        list.add(new WeakReference<T>(listener));
      }
    }
    
    @Override
    public void removeAll(Object... alias)
    {
      AliasKey key=new AliasKey(alias);
      synchronized(_lock)
      {
        if(_aliases==null) return;
        List<WeakReference<T>> list=_aliases.get(key);
        if(list==null) return;
        //No matter what findbugs believe, we really need to create a temporary ArrayList to avoid re-entry issues
        //during the iteration...
        new ArrayList<WeakReference<T>>(list).forEach(wl->remove(wl.get()));
      }
    }
    
    @Override
    public boolean isListening(T listener)
    {
      synchronized(_lock)
      {
        return _listeners.containsKey(listener);
      }
    }

    private void fireEvent(ListenerItem<T> item,T listener,Method m,Object[] params) throws Exception
    {
      QueuedEventItem itm=new QueuedEventItem<T>(item,listener,_mergeStrategy,m,params);
      if(_delay==0)
      {
        Dispatcher.dispatch(itm);
      }
      else
      {
        Scheduler.getInstance().schedule(itm,System.currentTimeMillis()+_delay);
      }
    }
    
    private void fireEvent(Method m,Object... params) throws Exception
    {
      Iterator<ArrayList<ListenerItem<T>>> lists=_listeners.values().iterator();
      while(lists.hasNext())
      {
        ArrayList<ListenerItem<T>> list=lists.next();
        Iterator<ListenerItem<T>> items=list.iterator();
        while(items.hasNext())
        {
          ListenerItem<T> item=items.next();
          T listener=item.getListener();
          if(listener==null)
          {
            items.remove();
          }
          else
          {
            if(_mergeStrategy==null)
              fireEvent(item,listener,m,params);
            else
            {
              if(!_mergeStrategy.tryMerge(listener,m,params))
                fireEvent(item,listener,m,params);
            }
          }
        }
        if(list.isEmpty())
          lists.remove();
      }
      return;
    }
    
    @Override
    public Object invoke(Object p,Method method,Object[] args) throws Throwable
    {
      String name=method.getName();
      switch(name)
      {
        case "add":return add((T)args[0]); //$NON-NLS-1$
        case "remove":remove((T)args[0]);return null; //$NON-NLS-1$
        case "removeAll": //$NON-NLS-1$
        {
          if(args==null || args.length==0)
            removeAll();
          else
            removeAll((Object[])args[0]);
          return null;
        }
        case "getListenerCount":return getListenerCount(); //$NON-NLS-1$
        case "isListening":return isListening((T)args[0]); //$NON-NLS-1$
        case "addAlias":addAlias((T)args[0],(Object[])args[1]);return null; //$NON-NLS-1$
      }
      synchronized(_lock)
      {
        fireEvent(method,args);
      }
      return null;
    }
  }  
  
  /**
   * Dump all instances of listener still registered into a listener group.
   * @param listener listener.
   */
  public static void dumpDisposedListening(Object listener)
  {
    if(!Debug.isDebug())
      return;
    synchronized(ALL_LISTENER_GROUPS)
    {
      for(WeakReference<ProxyHandler> w:ALL_LISTENER_GROUPS)
      {
        ProxyHandler group=w.get();
        if(group==null) continue;
        synchronized(group._lock)
        {
          ArrayList<ListenerItem> items=(ArrayList<ListenerItem>)group._listeners.get(listener);
          if(items==null) continue;
          //if(items.size()>0)
          //{
            //System.err.println(listener+" is still listening in group "+group); //$NON-NLS-1$
            //Thread.dumpStack();
            group._listeners.remove(listener);
            //System.err.println("Listener has been removed"); //$NON-NLS-1$
            //System.err.println(""); //$NON-NLS-1$
          //}
        }
        
      }
    }
  }
  
  /**
   * Create a delegate listener group for the given listener class.
   * @param clazz listener class.
   * @return listener group.
   */
  public static <T extends Listener> T groupOf(Class<T> clazz)
  {
    return groupOf(clazz,null,0);
  }

  /**
   * Create a delegate listener group for the given listener class.
   * @param clazz listener class.
   * @param mergeStrategy merge strategy.
   * @return listener group.
   */
  public static <T extends Listener> T groupOf(Class<T> clazz,ListenerMergeStrategy<T> mergeStrategy)
  {
    return groupOf(clazz,mergeStrategy,0);
  }

  /**
   * Create a delegate listener group for the given listener class.
   * @param clazz listener class.
   * @param mergeStrategy merge strategy.
   * @param delay grace period during which the events are queue but not fired, to optimize merging.
   * @return listener group.
   */
  public static <T extends Listener> T groupOf(Class<T> clazz,ListenerMergeStrategy<T> mergeStrategy,long delay)
  {
    return (T)Proxy.newProxyInstance(clazz.getClassLoader(),new Class[] {clazz},new ProxyHandler(mergeStrategy,delay));
  }
}
