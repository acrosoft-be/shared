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

import org.junit.Test;

import be.acrosoft.gaia.shared.util.Pair;

@SuppressWarnings({"javadoc"})
public class SimpleAsyncInvokerTest
{
  @Test
  public void testCallFromOutside()
  {
    Pair<Integer,Integer> p=Pair.pair(0,0);
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    try
    {
      invoker.call(()->p.a=17);
      assertEquals(17,(int)p.a);
    }
    finally
    {
      invoker.dispose();
    }
  }
  
  @Test
  public void testCallReentrant()
  {
    Pair<Integer,Integer> p=Pair.pair(0,0);
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    try
    {
      invoker.call(()->
      {
        invoker.call(()->p.a=17);
      });
      assertEquals(17,(int)p.a);
    }
    finally
    {
      invoker.dispose();
    }
  }
  
  @Test
  public void testError()
  {
    Pair<Throwable,Integer> p=Pair.pair(null,null);
    RuntimeException re=new RuntimeException();
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker(t->p.a=t);
    try
    {
      invoker.call(()->
      {
        throw re;
      });
      assertEquals(re,p.a);
    }
    finally
    {
      invoker.dispose();
    }
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
  public void testYieldFromOutside()
  {
    Pair<Integer,Integer> p=Pair.pair(0,0);
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    try
    {
      invoker.dispatch(()->{sleep(100);p.a=17;});
      while(p.a!=17) invoker.yield(false);
    }
    finally
    {
      invoker.dispose();
    }
  }
  
  @Test
  public void testYieldReentrant()
  {
    Pair<Integer,Integer> p=Pair.pair(0,0);
    SimpleAsyncInvoker invoker=new SimpleAsyncInvoker();
    try
    {
      invoker.dispatch(()->
      {
        invoker.dispatch(()->{sleep(100);p.a=17;});
        while(p.a!=17) invoker.yield(false);
      });
    }
    finally
    {
      invoker.dispose();
    }
  }
  
}
