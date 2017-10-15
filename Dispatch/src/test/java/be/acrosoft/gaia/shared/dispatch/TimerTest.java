package be.acrosoft.gaia.shared.dispatch;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class TimerTest
{
  private static class MyRunnable implements Runnable
  {
    public int count=0;
    public int sleep;
    
    @Override
    public void run()
    {
      try
      {
        Thread.sleep(sleep);
        count++;
      }
      catch(InterruptedException ex)
      {
      }
    }
  }

  @Test
  public void testTimer() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    MyRunnable run=new MyRunnable();
    Timer timer=new Timer(run,50);
    assertFalse(timer.enabled());
    timer.enable();
    assertTrue(timer.enabled());
    Thread.sleep(5000);
    assertTrue(""+run.count+">=90",run.count>=90);
    assertTrue(""+run.count+"<110",run.count<110);
    timer.disable();
    Thread.sleep(1000);
    assertTrue(""+run.count+">=90",run.count>=90);
    assertTrue(""+run.count+"<110",run.count<110);
    
  }
  
  @Test
  public void testDrift() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    MyRunnable run=new MyRunnable();
    run.sleep=100;
    Timer timer=new Timer(run,200);
    assertFalse(timer.enabled());
    timer.enable();
    assertTrue(timer.enabled());
    Thread.sleep(5000);
    assertTrue(""+run.count+">=22",run.count>=22);
    assertTrue(""+run.count+"<28",run.count<28);
    timer.disable();
    Thread.sleep(1000);
    assertTrue(""+run.count+">=22",run.count>=22);
    assertTrue(""+run.count+"<28",run.count<28);
  }

  @Test
  public void testOverRate() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    MyRunnable run=new MyRunnable();
    run.sleep=300;
    Timer timer=new Timer(run,200);
    assertFalse(timer.enabled());
    timer.enable();
    assertTrue(timer.enabled());
    Thread.sleep(5000);
    assertTrue(""+run.count+">=9",run.count>=9);
    assertTrue(""+run.count+"<17",run.count<17);
    timer.disable();
    Thread.sleep(1000);
    assertTrue(""+run.count+">=9",run.count>=9);
    assertTrue(""+run.count+"<17",run.count<17);
  }
}
