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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
  
  private static class MyStrongListener implements MyListenerInterface
  {
    private List<Integer> _l;
    
    public MyStrongListener(List<Integer> l)
    {
      _l=l;
    }

    @Override
    public void my(MyEvent event)
    {
      _l.add(0);
    }
    
  }
  
  @Test
  public void testStrong()
  {
    List<Integer> list=new ArrayList<Integer>();
    QueuingInvoker invoker=new QueuingInvoker();
    Dispatcher.init(invoker);

    MyListenerInterface group=Listener.groupOf(MyListenerInterface.class);
    
    MyListenerInterface listener=new MyStrongListener(list);
    
    group.add(listener);
    listener=null;
    
    for(int i=0;i<100;i++) System.gc();
    
    group.my(null);
    invoker.flush();
    assertEquals(1,list.size());
  }
  
  @WeakListener
  private static interface MyWeakListenerInterface extends Listener<MyWeakListenerInterface>
  {
    public void my();
  }
  
  private static class MyWeakListener implements MyWeakListenerInterface
  {
    private List<Integer> _l;
    public MyWeakListener(List<Integer> l)
    {
      _l=l;
    }
    
    @Override
    public void my()
    {
      _l.add(0);
    }
  }
  
  @Test
  public void testWeak()
  {
    List<Integer> list=new ArrayList<Integer>();
    QueuingInvoker invoker=new QueuingInvoker();
    Dispatcher.init(invoker);

    MyWeakListenerInterface group=Listener.groupOf(MyWeakListenerInterface.class);
    
    MyWeakListenerInterface listener=new MyWeakListener(list);
    WeakReference<MyWeakListenerInterface> weak=new WeakReference<>(listener);
    
    group.add(listener);
    listener=null;
    
    while(weak.get()!=null) System.gc();
    
    group.my();
    invoker.flush();
    assertEquals(0,list.size());
  }
}
