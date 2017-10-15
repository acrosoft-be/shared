package be.acrosoft.gaia.shared.dispatch;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

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

}
