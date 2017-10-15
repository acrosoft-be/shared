package be.acrosoft.gaia.shared.dispatch;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings({"javadoc"})
public class TimeOutTest
{

  @Test
  public void testTimeOut() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    Future<Void,Throwable> map=new Future<Void,Throwable>();
    
    ExpectedRunnable run=new ExpectedRunnable(map,System.currentTimeMillis()+500);
    
    TimeOut to=new TimeOut(run,500);
    to.enable();
    
    map.getResult();
  }

  @Test
  public void testDisable() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    Future<Void,Throwable> map=new Future<Void,Throwable>();
    
    ExpectedRunnable run=new ExpectedRunnable(map,System.currentTimeMillis()+1000);
    
    TimeOut to=new TimeOut(run,500);
    assertFalse(to.enabled());
    to.enable();
    assertTrue(to.enabled());
    Thread.sleep(250);
    to.disable();
    assertFalse(to.enabled());
    Thread.sleep(250);
    to.enable();    
    assertTrue(to.enabled());
    
    map.getResult();
  }

  @Test
  public void testReset() throws Throwable
  {
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    
    Future<Void,Throwable> map=new Future<Void,Throwable>();
    
    ExpectedRunnable run=new ExpectedRunnable(map,System.currentTimeMillis()+1000);
    
    TimeOut to=new TimeOut(run,500);
    to.enable();
    Thread.sleep(250);
    to.reset();
    Thread.sleep(250);
    to.reset();
    
    map.getResult();
  }


}
