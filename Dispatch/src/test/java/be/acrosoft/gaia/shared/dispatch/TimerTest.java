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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Duration;

import org.junit.Test;

import be.acrosoft.gaia.shared.util.OffsetClock;

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
  
  @Test
  public void testBackwardsClock() throws Throwable
  {
    OffsetClock clock=new OffsetClock(Clock.systemUTC(),Duration.ZERO);
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    Dispatcher.init(invoker);
    Scheduler.getInstance().setClock(clock);
    try {
      MyRunnable run=new MyRunnable();
      Timer timer=new Timer(run,50);
      timer.enable();
      Thread.sleep(1000);
      assertTrue(""+run.count+">=18",run.count>=18);
      assertTrue(""+run.count+"<22",run.count<22);
      clock.setOffset(Duration.ofHours(-4));
      run.count=0;
      Thread.sleep(1000);
      assertTrue(""+run.count+">=18",run.count>=18);
      assertTrue(""+run.count+"<22",run.count<22);
    } finally {
      Scheduler.getInstance().setClock(Clock.systemUTC());
    }
    
  }
}
