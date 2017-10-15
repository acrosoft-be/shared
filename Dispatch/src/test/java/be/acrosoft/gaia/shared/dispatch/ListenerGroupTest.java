package be.acrosoft.gaia.shared.dispatch;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

class MyEvent
{
  public String event;
  public int count;
  public MyEvent(String aevent)
  {
    event=aevent;
    count=1;
  }
  
}

@SuppressWarnings({"javadoc","nls"})
public class ListenerGroupTest
{
  private static interface MyListenerInterface extends Listener
  {
    public void my(MyEvent event);
  }
  
  private static class MyListener implements MyListenerInterface
  {
    public int count=0;
    
    @Override
    public void my(MyEvent event)
    {
      count+=event.count;
    }
  }

  @Test
  public void testMerge()
  {
    QueuingInvoker invoker=new QueuingInvoker();
    Dispatcher.init(invoker);
    
    ListenerMergeStrategy<MyListenerInterface> mergeStrategy=ListenerMergeStrategy.whenParameter(MyEvent.class).mergeBy(e->e.event).using((e1,e2)->
    {
      assertEquals(e1.event,e2.event);
      e1.count++;
      return e1;
    }).toStrategy();
    
    MyListener listener=new MyListener();
    MyListenerInterface group=Listener.groupOf(MyListenerInterface.class,mergeStrategy);

    group.add(listener);

    group.my(new MyEvent("param"));
    assertEquals(1,invoker.getListSize());
    group.my(new MyEvent("param1"));
    assertEquals(2,invoker.getListSize());
    group.my(new MyEvent("param1"));
    assertEquals(2,invoker.getListSize());

    invoker.flush();
    assertEquals(3,listener.count);
    
  }
  
  @Test
  public void testRemovalDrop()
  {
    QueuingInvoker invoker=new QueuingInvoker();
    Dispatcher.init(invoker);
    
    MyListener listener=new MyListener();
    MyListenerInterface group=Listener.groupOf(MyListenerInterface.class);
    group.add(listener);
    
    group.my(new MyEvent("param"));
    group.remove(listener);
    assertEquals(0,listener.count);    
    invoker.flush();
    assertEquals(0,listener.count);    
  }
  
  @Test
  public void testBase()
  {
    QueuingInvoker invoker=new QueuingInvoker();
    Dispatcher.init(invoker);
    
    MyListener listener=new MyListener();
    MyListenerInterface group=Listener.groupOf(MyListenerInterface.class);
    group.add(listener);
    group.add(listener);
    
    group.my(new MyEvent("param"));
    invoker.flush();
    
    assertEquals(2,listener.count);
    
    group.remove(listener);
    group.my(new MyEvent("param"));
    invoker.flush();
    assertEquals(2,listener.count);
  }
}
