package be.acrosoft.gaia.shared.dispatch;


import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * DeferredRunnableTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class DeferredTest
{
  private static class Container
  {
    public int content;
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
    
    Thread.sleep(100);
    Dispatcher.flush();
    assertEquals(0,container.content);
    
    Thread.sleep(500);
    Dispatcher.flush();
    assertEquals(0,container.content);
    
    container.content=1;
    Thread.sleep(1000);
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
