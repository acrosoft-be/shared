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

import java.time.Clock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.acrosoft.gaia.shared.util.InstantClock;


/**
 * DeferredRunnableTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class DeferredTest
{
  private InstantClock _clock;

  private static class Container
  {
    public int content;
  }
  
  @Before
  public void before()
  {
    _clock=new InstantClock();
    Scheduler.getInstance().setClock(_clock);
  }
  
  @After
  public void after()
  {
    Scheduler.getInstance().setClock(Clock.systemUTC());
  }

  @Test
  public void testMain() throws Exception
  {
    QueuingInvoker invoker=new QueuingInvoker();
    Dispatcher.init(invoker);
    
    final Container container=new Container();
    container.content=0;
    
    new Deferred(500)
    {
      @Override
      public boolean ready()
      {
        return container.content==1;
      }

      @Override
      public void run()
      {
        container.content=2;
      }
    };
    
    assertEquals(0,container.content);
    
    Dispatcher.flush();
    assertEquals(0,container.content);
    
    _clock.sleep(100);
    Dispatcher.flush();
    assertEquals(0,container.content);
    
    _clock.sleep(500);
    Dispatcher.flush();
    assertEquals(0,container.content);
    
    container.content=1;
    _clock.sleep(1000);
    Dispatcher.flush();
    assertEquals(2,container.content);
    
  }
  
  @Test
  public void testBuffer()
  {
    final Container container=new Container();
    container.content=0;
    
    new Deferred(500)
    {
      @Override
      public boolean ready()
      {
        assertEquals("a",put("a"));
        assertEquals("b",put("kb","b"));
        return true;
      }

      @Override
      public void run()
      {
        assertEquals("a",get());
        assertEquals("b",get("kb"));
      }
    };
  }
}
