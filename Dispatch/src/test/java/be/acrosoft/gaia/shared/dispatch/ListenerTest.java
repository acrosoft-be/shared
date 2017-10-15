package be.acrosoft.gaia.shared.dispatch;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class ListenerTest
{
  public static interface SomethingListener extends Listener<SomethingListener>
  {
    public void somethingHappenned(int what);
  }
  
  public static class Observable
  {
    public SomethingListener OnSomething=Listener.groupOf(SomethingListener.class);
    
    public void doSomething(int i)
    {
      OnSomething.somethingHappenned(i);
    }  
  }

  @Before
  public void before()
  {
    Dispatcher.init(new QueuingInvoker());
  }

  @After
  public void after()
  {
    Dispatcher.getInvoker().dispose();
  }
  
  @Test
  public void testSimpleListener()
  {
    Observable o=new Observable();
    Mockery mockery=new Mockery();
    SomethingListener listener=mockery.mock(SomethingListener.class);

    mockery.checking(new Expectations()
    {{
      oneOf(listener).somethingHappenned(14);
    }});
    
    o.OnSomething.add(listener);
    o.doSomething(14);

    Dispatcher.flush();
    
    mockery.assertIsSatisfied();
  }

  @Test
  public void testDualListener()
  {
    Observable o=new Observable();
    Mockery mockery=new Mockery();
    SomethingListener listener1=mockery.mock(SomethingListener.class,"listener1");
    SomethingListener listener2=mockery.mock(SomethingListener.class,"listener2");

    mockery.checking(new Expectations()
    {{
      oneOf(listener1).somethingHappenned(14);
      oneOf(listener2).somethingHappenned(14);
    }});
    
    o.OnSomething.add(listener1);
    o.OnSomething.add(listener2);
    o.doSomething(14);

    Dispatcher.flush();
    
    mockery.assertIsSatisfied();
  }

  @Test
  public void testRemoval()
  {
    Observable o=new Observable();
    Mockery mockery=new Mockery();
    SomethingListener listener1=mockery.mock(SomethingListener.class,"listener1");
    SomethingListener listener2=mockery.mock(SomethingListener.class,"listener2");

    mockery.checking(new Expectations()
    {{
      oneOf(listener2).somethingHappenned(14);
    }});
    
    o.OnSomething.add(listener1);
    o.OnSomething.add(listener2);
    assertEquals(2,o.OnSomething.getListenerCount());
    assertTrue(o.OnSomething.isListening(listener1));
    assertTrue(o.OnSomething.isListening(listener2));
    o.OnSomething.remove(listener1);
    assertEquals(1,o.OnSomething.getListenerCount());
    assertFalse(o.OnSomething.isListening(listener1));
    assertTrue(o.OnSomething.isListening(listener2));
    o.doSomething(14);

    Dispatcher.flush();
    
    mockery.assertIsSatisfied();
  }

  @Test
  public void testRemovalViaHandle()
  {
    Observable o=new Observable();
    Mockery mockery=new Mockery();
    SomethingListener listener1=mockery.mock(SomethingListener.class,"listener1");
    SomethingListener listener2=mockery.mock(SomethingListener.class,"listener2");

    mockery.checking(new Expectations()
    {{
      oneOf(listener2).somethingHappenned(14);
    }});
    
    o.OnSomething.add(listener1).remove();
    o.OnSomething.add(listener2);
    o.doSomething(14);

    Dispatcher.flush();
    
    mockery.assertIsSatisfied();
  }

  @Test
  public void testClear()
  {
    Observable o=new Observable();
    Mockery mockery=new Mockery();
    SomethingListener listener1=mockery.mock(SomethingListener.class,"listener1");
    SomethingListener listener2=mockery.mock(SomethingListener.class,"listener2");

    mockery.checking(new Expectations()
    {{
    }});
    
    o.OnSomething.add(listener1);
    o.OnSomething.add(listener2);
    o.OnSomething.removeAll();
    o.doSomething(14);

    Dispatcher.flush();
    
    mockery.assertIsSatisfied();
  }

  @Test
  public void testAliasing()
  {
    Observable o=new Observable();
    Mockery mockery=new Mockery();
    SomethingListener listener1=mockery.mock(SomethingListener.class,"listener1");
    SomethingListener listener2=mockery.mock(SomethingListener.class,"listener2");

    mockery.checking(new Expectations()
    {{
      oneOf(listener1).somethingHappenned(14);
    }});
    
    o.OnSomething.add(listener1).alias("b");
    o.OnSomething.add(listener2).alias("a").alias("b");
    o.OnSomething.removeAll("a");
    o.doSomething(14);
    Dispatcher.flush();

    o.OnSomething.removeAll("b");
    assertEquals(0,o.OnSomething.getListenerCount());
    
    mockery.assertIsSatisfied();
  }
  
  @Test
  public void testDrop()
  {
    Observable o=new Observable();
    Mockery mockery=new Mockery();
    SomethingListener listener1=mockery.mock(SomethingListener.class,"listener1");
    SomethingListener listener2=mockery.mock(SomethingListener.class,"listener2");

    mockery.checking(new Expectations()
    {{
    }});
    
    o.OnSomething.add(listener1).alias("a");
    o.OnSomething.add(listener2).alias("a");
    o.doSomething(14);
    o.OnSomething.removeAll("a");
    Dispatcher.flush();

    assertEquals(0,o.OnSomething.getListenerCount());
    
    mockery.assertIsSatisfied();
  }
}
