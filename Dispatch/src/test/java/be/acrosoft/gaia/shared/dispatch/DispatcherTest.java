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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.acrosoft.gaia.shared.util.Pair;

@SuppressWarnings({"javadoc"})
public class DispatcherTest
{
  private AsyncInvoker _invoker;
  private Pair<Throwable,Void> _exception;
  
  @Before
  public void before()
  {
    _exception=Pair.pair(null,null);
    _invoker=new SimpleAsyncInvoker(t->_exception.a=t);
    Dispatcher.init(_invoker);
  }
  
  @After
  public void after()
  {
    Dispatcher.init(null);
    _invoker.dispose();
    _exception=Pair.pair(null,null);
  }
  
  
  @Test
  public void testCall()
  {
    Pair<Integer,Integer> p=Pair.pair(0,0);
    Dispatcher.call(()->p.a=17);
    assertEquals(17,(int)p.a);
  }
  
  @Test
  public void testException()
  {
    Exception e=new RuntimeException();
    Dispatcher.reportException(e);
    while(_exception.a==null) Dispatcher.yield(false);
    assertEquals(e,_exception.a.getCause());
  }
  
  @Test
  public void testTransparentCall()
  {
    assertEquals("hello",Dispatcher.call(()->"hello")); //$NON-NLS-1$ //$NON-NLS-2$
  }
  
  @Test(expected=IOException.class)
  public void testTransparentException() throws IOException
  {
    Dispatcher.call(()->{throw new IOException();});
  }
  
  private void sleep(int time)
  {
    try
    {
      Thread.sleep(time);
    }
    catch(InterruptedException ex)
    {
      throw new RuntimeException(ex);
    }
  }
  
  @Test
  public void testBlockingYield()
  {
    Pair<Integer,Integer> p=Pair.pair(0,0);
    Dispatcher.dispatch(()->{sleep(100);p.a=17;});
    while(p.a!=17) Dispatcher.yield(true);
  }
  
  @Test
  public void testJoin() throws InterruptedException
  {
    Thread t=new Thread(()->sleep(200));
    t.start();
    Dispatcher.join(t);
    assertTrue(!t.isAlive());
  }
}
