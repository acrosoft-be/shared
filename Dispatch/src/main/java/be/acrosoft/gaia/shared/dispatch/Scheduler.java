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

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scheduling management class. This class manages a set of scheduled items. A scheduled item is a Runnable instance
 * that must be run at a given time.
 * <br/>
 * A singleton approach is implemented. The Scheduler is backed by a single daemon thread.
 */
public class Scheduler
{
  private static final Logger LOGGER=Logger.getLogger(Scheduler.class.getName());

  private static class ScheduledItemInternal implements Runnable
  {
    /**
     * Runnable to run.
     */
    public final Runnable runnable;
    /**
     * Scheduled run time.
     */
    public final long time;
    /**
     * Whether this item has been cancelled or not.
     */
    public boolean cancelled;
    
    /**
     * Create a new ScheduledItemInternal.
     * @param arunnable runnable to run.
     * @param atime scheduled run time.
     */
    public ScheduledItemInternal(Runnable arunnable,long atime)
    {
      runnable=arunnable;
      time=atime;
      cancelled=false;
    }
  
    @Override
    public void run()
    {
      if(cancelled) return;
      runnable.run();
    }
  }
  
  private static class ScheduledItem
  {
    /**
     * Internal.
     */
    public ScheduledItemInternal internal;
    
    /**
     * Create a new ScheduledItem.
     * @param ainternal internal.
     */
    public ScheduledItem(ScheduledItemInternal ainternal)
    {
      internal=ainternal;
    }
  }
  
  private static class SchedulerThread extends Thread
  {
    private TreeMap<Long,List<ScheduledItemInternal>> _items;
    private boolean _waiting;
    private Clock _clock;
    
    /**
     * Create a new SchedulerThread.
     */
    public SchedulerThread()
    {
      super("SchedulerThread"); //$NON-NLS-1$
      setDaemon(true);
      _items=new TreeMap<>();
      _waiting=false;
      _clock=Clock.systemUTC();
    }
    
    private void cancelInternal(ScheduledItem item)
    {
      item.internal.cancelled=true;
      List<ScheduledItemInternal> atTime=_items.get(item.internal.time);
      if(atTime==null) return;
      atTime.remove(item.internal);
      if(atTime.size()==0)
      {
        _items.remove(item.internal.time);
      }
    }
    
    private void scheduleInternal(ScheduledItem item)
    {
      List<ScheduledItemInternal> atTime=_items.get(item.internal.time);
      if(atTime==null)
      {
        atTime=new ArrayList<ScheduledItemInternal>(1);
        _items.put(item.internal.time,atTime);
      }
      atTime.add(item.internal);
    }
    
    /**
     * Set the internal clock.
     * @param clock clock.
     */
    public void setClock(Clock clock)
    {
      _clock=clock;
    }
    
    /**
     * Get the internal clock.
     * @return clock.
     */
    public Clock getClock()
    {
      return _clock;
    }
    
    /**
     * Add a scheduled operation.
     * @param runnable runnable.
     * @param time scheduled time.
     * @return scheduled item.
     */
    public ScheduledItem schedule(Runnable runnable,long time)
    {
      synchronized(_items)
      {
        ScheduledItem item=new ScheduledItem(new ScheduledItemInternal(runnable,time));
        scheduleInternal(item);
        if(_waiting)
        {
          _items.notify();
        }
        return item;
      }
    }
    
    /**
     * Remove a scheduled operation.
     * @param item scheduled operation to remove.
     */
    public void cancel(ScheduledItem item)
    {
      if(item.internal.cancelled) return;
      synchronized(_items)
      {
        cancelInternal(item);
        if(_waiting)
        {
          _items.notify();
        }
      }
    }
    
    /**
     * Reschedule an operation.
     * @param item scheduled operation.
     * @param time new run time.
     */
    public void reschedule(ScheduledItem item,long time)
    {
      if(item.internal.time==time) return;
      
      synchronized(_items)
      {
        cancelInternal(item);
        item.internal=new ScheduledItemInternal(item.internal.runnable,time);
        scheduleInternal(item);
        if(_waiting)
        {
          _items.notify();
        }
      }
    }
    
    private ScheduledItemInternal getFirst()
    {
      if(_items.size()==0) return null;
      List<ScheduledItemInternal> firstList=_items.firstEntry().getValue();
      if(firstList.size()==0) return null;
      return firstList.get(0);
    }
    
    @Override
    public void run()
    {
      while(true)
      {
        ScheduledItemInternal next=null;
        synchronized(_items)
        {
          _waiting=true;
          next=getFirst();
          try
          {
            if(next==null)
            {
              _items.wait();
            }
            else
            {
              long time=_clock.millis();
              long toWait=next.time-time;
              if(toWait>1000) toWait=1000;
              if(toWait>0) _items.wait(toWait);
              long newTime=_clock.millis();
              if(newTime<time || newTime>time+10000)
              {
                long offset=newTime-time-1000;
                if(offset<0)
                {
                  LOGGER.warning("Clock jumped backwards by "+(-offset)+"ms."); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                  LOGGER.info("Clock jumped forward by "+offset+"ms."); //$NON-NLS-1$ //$NON-NLS-2$
                }
                TreeMap<Long,List<ScheduledItemInternal>> shifted=new TreeMap<>();
                for(Map.Entry<Long,List<ScheduledItemInternal>> entry:_items.entrySet())
                {
                  List<ScheduledItemInternal> list=new ArrayList<>();
                  for(ScheduledItemInternal i:entry.getValue())
                  {
                    list.add(new ScheduledItemInternal(i.runnable,i.time+offset));
                  }
                  shifted.put(entry.getKey()+offset,list);
                }
                _items=shifted;
                continue;
              }
              if(next.time>newTime)
              {
                next=null;
              }
            }
          }
          catch(InterruptedException ex)
          {
            next=null;
          }
          _waiting=false;
        }
        
        if(next!=null)
        {
          try
          {
            //It is possible that next is not within _items anymore.
            synchronized(_items)
            {
              List<ScheduledItemInternal> atTime=_items.get(next.time);
              if(atTime!=null)
              {
                atTime.remove(next);
                if(atTime.size()==0)
                {
                  _items.remove(next.time);
                }
              }
            }
            Dispatcher.dispatch(next);
          }
          catch(Throwable ex)
          {
            LOGGER.log(Level.SEVERE,"Unexpected exception in Scheduler",ex); //$NON-NLS-1$
            Dispatcher.reportException(ex);
          }
        }
      }
    }
  }
  
  private SchedulerThread _thread;
  private static Scheduler _instance=new Scheduler();
  
  private Scheduler()
  {
    _thread=new SchedulerThread();
    _thread.start();
  }
  
  /**
   * Get the unique scheduler instance.
   * @return the unique scheduler instance.
   */
  public static Scheduler getInstance()
  {
    return _instance;
  }
  
  /**
   * Set the scheduler's clock.
   * @param clock clock.
   */
  public void setClock(Clock clock)
  {
    _thread.setClock(clock);
  }
  
  /**
   * Get the scheduler's clock.
   * @return clock.
   */
  public Clock getClock()
  {
    return _thread.getClock();
  }
  
  /**
   * Schedule an operation to be run at the given time. Return a reference to the scheduled operation. This reference
   * can be used to get scheduling information or to cancel the scheduling.
   * @param runnable runnable to run.
   * @param time run time.
   * @return scheduled reference.
   */
  public Object schedule(Runnable runnable,long time)
  {
    return _thread.schedule(runnable,time);
  }
  
  private ScheduledItem getItem(Object reference)
  {
    return (ScheduledItem)reference;
  }

  /**
   * Get the run time of the given reference.
   * @param reference reference.
   * @return the run time, or -1 if the reference is not found.
   */
  public long getScheduledTime(Object reference)
  {
    ScheduledItem item=getItem(reference);
    if(item==null) return -1;
    return item.internal.time;
  }
  
  /**
   * Get the runnable of the given reference.
   * @param reference reference.
   * @return the runnable, or null if the reference is not found.
   */
  public Runnable getRunnable(Object reference)
  {
    ScheduledItem item=getItem(reference);
    if(item==null) return null;
    return item.internal.runnable;
  }
  
  /**
   * Cancel the given scheduled reference. The reference cannot be rescheduled anymore.
   * @param reference reference to cancel.
   */
  public void cancel(Object reference)
  {
    _thread.cancel(getItem(reference));
  }
  
  /**
   * Reschedule the given reference.
   * @param reference reference.
   * @param time new run time.
   */
  public void reschedule(Object reference,long time)
  {
    ScheduledItem item=getItem(reference);
    _thread.reschedule(item,time);
  }
}
