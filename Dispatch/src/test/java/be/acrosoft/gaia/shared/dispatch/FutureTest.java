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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class FutureTest
{

  @Test
  public void testResult() throws Exception
  {
    Dispatcher.init(new QueuingInvoker());
    final Future<Integer,IOException> map=new Future<Integer,IOException>();
    
    Thread t=new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          Thread.sleep(100);
          map.setResult(17);
        }
        catch(InterruptedException ex)
        {
          fail(ex.toString());
        }
      }
    };
    assertFalse(map.isResultAvailable());
    t.start();
    assertEquals(17,(int)map.getResult());
    assertTrue(map.isResultAvailable());
  }

  @Test
  public void testException() throws Exception
  {
    Dispatcher.init(new QueuingInvoker());
    final Future<Integer,IOException> map=new Future<Integer,IOException>();
    
    Thread t=new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          Thread.sleep(100);
          map.setThrowable(new IOException());
        }
        catch(InterruptedException ex)
        {
          fail(ex.toString());
        }
      }
    };
    assertFalse(map.isResultAvailable());
    t.start();
    try
    {
      map.getResult();
      assertTrue(map.isResultAvailable());
      fail("IOException expected");
    }
    catch(IOException ex)
    {
    }
  }

  @Test
  public void testResultEarly() throws Exception
  {
    Dispatcher.init(new QueuingInvoker());
    final Future<Integer,IOException> map=new Future<Integer,IOException>();
    
    Thread t=new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          Thread.sleep(100);
          map.setResult(17);
        }
        catch(InterruptedException ex)
        {
          fail(ex.toString());
        }
      }
    };
    t.start();
    t.join();
    assertTrue(map.isResultAvailable());
    assertEquals(17,(int)map.getResult());
  }
  
  @Test
  public void testTimeOut() throws Exception
  {
    Dispatcher.init(new QueuingInvoker());
    final Future<Integer,IOException> map=new Future<Integer,IOException>();
    
    Thread t=new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          Thread.sleep(1000);
          map.setResult(17);
        }
        catch(InterruptedException ex)
        {
          fail(ex.toString());
        }
      }
    };
    t.start();
    long before=System.currentTimeMillis();
    assertNull(map.getResult(500));
    long ellapsed=System.currentTimeMillis()-before;
    assertTrue(ellapsed>400);
    assertTrue(t.isAlive());
    t.join();
  }
}
