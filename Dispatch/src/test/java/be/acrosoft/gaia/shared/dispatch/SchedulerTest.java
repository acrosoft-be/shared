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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.Clock;
import java.time.Duration;

import org.junit.Test;

import be.acrosoft.gaia.shared.util.OffsetClock;

/**
 * SchedulerTest.
 */
@SuppressWarnings({"javadoc"})
public class SchedulerTest
{

  @Test
  public void testSchedule() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    Future<Void,Throwable> map=new Future<Void,Throwable>();
    long atTime=System.currentTimeMillis()+500;
    ExpectedRunnable run=new ExpectedRunnable(map,atTime);
    
    Object ref=Scheduler.getInstance().schedule(run,atTime);
    assertEquals(atTime,Scheduler.getInstance().getScheduledTime(ref));
    assertEquals(run,Scheduler.getInstance().getRunnable(ref));
    map.getResult();
  }

  @Test
  public void testCancel() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    Future<Void,Throwable> map=new Future<Void,Throwable>();
    long atTime=System.currentTimeMillis()+500;
    ExpectedRunnable run=new ExpectedRunnable(map,atTime+500);
    
    Object ref=Scheduler.getInstance().schedule(run,atTime);
    Thread.sleep(50);    
    Scheduler.getInstance().cancel(ref);
    Thread.sleep(1000);
    assertFalse(map.isResultAvailable());
  }

  @Test
  public void testReschedule() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    Future<Void,Throwable> map=new Future<Void,Throwable>();
    long atTime=System.currentTimeMillis()+1000;
    ExpectedRunnable run=new ExpectedRunnable(map,atTime+500);
    
    Object ref=Scheduler.getInstance().schedule(run,atTime);
    Thread.sleep(50);
    Scheduler.getInstance().reschedule(ref,atTime+500);
    map.getResult();
  }
  
  @Test
  public void testClockBackward() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    OffsetClock clock=new OffsetClock(Clock.systemUTC(),Duration.ZERO);
    Scheduler.getInstance().setClock(clock);
    try
    {
      Future<Void,Throwable> map=new Future<Void,Throwable>();
      ExpectedRunnable run=new ExpectedRunnable(map,clock.instant().minus(Duration.ofHours(1)).toEpochMilli()+2500);
      Scheduler.getInstance().schedule(run,clock.millis()+2500);
      Thread.sleep(10);
      clock.setOffset(Duration.ofHours(-1));
      map.getResult();
    }
    finally
    {
      Scheduler.getInstance().setClock(Clock.systemUTC());
    }
  }
  
  @Test
  public void testClockForward() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    OffsetClock clock=new OffsetClock(Clock.systemUTC(),Duration.ZERO);
    Scheduler.getInstance().setClock(clock);
    try
    {
      Future<Void,Throwable> map=new Future<Void,Throwable>();
      ExpectedRunnable run=new ExpectedRunnable(map,clock.instant().plus(Duration.ofHours(1)).toEpochMilli()+2500);
      Scheduler.getInstance().schedule(run,clock.millis()+2500);
      Thread.sleep(10);
      clock.setOffset(Duration.ofHours(1));
      map.getResult();
    }
    finally
    {
      Scheduler.getInstance().setClock(Clock.systemUTC());
    }
  }

}
