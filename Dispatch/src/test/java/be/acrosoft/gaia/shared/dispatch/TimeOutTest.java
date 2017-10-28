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
