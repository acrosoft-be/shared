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
package be.acrosoft.gaia.shared.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class CronTaskSchedulerTest
{
  private static class CronTabTest implements CronTaskScheduler.CronTab
  {
    public List<String> value=new ArrayList<>();
    public String readResult;
    public String applyResult;
    
    @Override
    public void cleanup()
    {      
    }

    @Override
    public String read(List<String> tab)
    {
      tab.clear();
      tab.addAll(value);
      return readResult;
    }

    @Override
    public String apply(List<String> tab)
    {
      value.clear();
      value.addAll(tab);
      return applyResult;
    }
  }

  @Test
  public void testAddEveryDay()
  {
    CronTabTest test=new CronTabTest();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(4*60),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * * command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }
  
  @Test
  public void testAddSpecificDay()
  {
    CronTabTest test=new CronTabTest();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(4,4*60),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * 5 command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }

  @Test
  public void testAddSunday()
  {
    CronTabTest test=new CronTabTest();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(6,4*60),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * 0 command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }
  
  @Test
  public void testAddInterval()
  {
    CronTabTest test=new CronTabTest();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(2*60,23*60,2),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 2,4,6,8,10,12,14,16,18,20,22 * * * command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }
  
  @Test
  public void testList()
  {
    CronTabTest test=new CronTabTest();
    test.value.add("# some stuff");
    test.value.add("0 4 * * * command1 params1");
    test.value.add("0 4 * * * command2 params2 || /bin/echo 'Acrosoft taskname=name2' > /dev/null");
    test.value.add("0 4 * * * command3 params3");
    test.value.add("0 4 * * * command4 params4 || /bin/echo 'Acrosoft taskname=name'");
    test.value.add("0 4 * * * command5 || /bin/echo 'Acrosoft taskname=name5' > /dev/null");
    CronTaskScheduler scheduler=new CronTaskScheduler(test);

    List<TaskSummary> tasks=scheduler.listTasks();
    assertEquals(2,tasks.size());
    assertEquals("name2",tasks.get(0).getName());
    assertEquals("command2",tasks.get(0).getCommand());
    assertEquals("params2",tasks.get(0).getParameters());
    assertEquals("name5",tasks.get(1).getName());
    assertEquals("command5",tasks.get(1).getCommand());
    assertEquals("",tasks.get(1).getParameters());
  }
  
  @Test
  public void testDelete()
  {
    CronTabTest test=new CronTabTest();
    test.value.add("# some stuff");
    test.value.add("0 4 * * * command1 params1");
    test.value.add("0 4 * * * command2 params2 || /bin/echo 'Acrosoft taskname=name' > /dev/null");
    test.value.add("0 4 * * * command2 params2 || /bin/echo 'Acrosoft taskname=name'");
    test.value.add("0 4 * * * command3 params3");
    test.value.add("0 4 * * * command4 params4 || /bin/echo 'Acrosoft taskname=notthisone' > /dev/null");
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    assertNull(scheduler.deleteTask("name"));
    assertEquals(5,test.value.size());
    assertEquals("# some stuff",test.value.get(0));
    assertEquals("0 4 * * * command1 params1",test.value.get(1));
    assertEquals("0 4 * * * command2 params2 || /bin/echo 'Acrosoft taskname=name'",test.value.get(2));
    assertEquals("0 4 * * * command3 params3",test.value.get(3));
    assertEquals("0 4 * * * command4 params4 || /bin/echo 'Acrosoft taskname=notthisone' > /dev/null",test.value.get(4));
  }
  
  @Test
  public void testMigrate()
  {
    CronTabTest test=new CronTabTest();
    test.value.add("# some stuff");
    test.value.add("0 4 * * * command1 params1");
    test.value.add("0 4 * * * command2 params2 || /bin/echo 'Acrosoft taskname=name' > /dev/null");
    test.value.add("0 4 * * * command3 params3");
    test.value.add("# This line added by Acrosoft tool");
    test.value.add("0 4 * * * command4 params4 taskname=oldname");
    test.value.add("0 4 * * * command5 params5");
    test.value.add("0 4 * * * command params taskname=oldname param=value");
    test.value.add("0 4 * * * command params taskname=");
    test.value.add("0 4 * * * command params taskname= ");
    test.value.add("@ some stuff");
    test.value.add("ENV=VAL");
    new CronTaskScheduler(test);
    assertEquals(11,test.value.size());
    assertEquals("# some stuff",test.value.get(0));
    assertEquals("0 4 * * * command1 params1",test.value.get(1));
    assertEquals("0 4 * * * command2 params2 || /bin/echo 'Acrosoft taskname=name' > /dev/null",test.value.get(2));
    assertEquals("0 4 * * * command3 params3",test.value.get(3));
    assertEquals("0 4 * * * command4 params4 || /bin/echo 'Acrosoft taskname=oldname' > /dev/null",test.value.get(4));
    assertEquals("0 4 * * * command5 params5",test.value.get(5));
    assertEquals("0 4 * * * command params param=value || /bin/echo 'Acrosoft taskname=oldname' > /dev/null",test.value.get(6));
    assertEquals("0 4 * * * command params taskname=",test.value.get(7));
    assertEquals("0 4 * * * command params taskname= ",test.value.get(8));
    assertEquals("@ some stuff",test.value.get(9));
    assertEquals("ENV=VAL",test.value.get(10));
  }
  
  @Test
  public void testErrorOnMigrate()
  {
    CronTabTest test=new CronTabTest();
    test.readResult="test error";
    test.value.add("0 4 * * * command4 params4 taskname=oldname");
    new CronTaskScheduler(test);
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * * command4 params4 taskname=oldname",test.value.get(0));
  }
  
  @Test
  public void testErrorOnAdd()
  {
    CronTabTest test=new CronTabTest();
    test.value.add("# some stuff");
    test.readResult="test error";
    assertEquals("test error",new CronTaskScheduler(test).createTask(new Task("name",new Schedule(0),"cmd","param")));
    assertEquals(1,test.value.size());
    assertEquals("# some stuff",test.value.get(0));
  }
  
  @Test
  public void testErrorOnDelete()
  {
    CronTabTest test=new CronTabTest();
    test.value.add("# some stuff");
    test.readResult="test error";
    assertEquals("test error",new CronTaskScheduler(test).deleteTask("task"));
    assertEquals(1,test.value.size());
    assertEquals("# some stuff",test.value.get(0));
  }
  
  @Test
  public void testErrorOnList()
  {
    CronTabTest test=new CronTabTest();
    test.value.add("# some stuff");
    test.readResult="test error";
    assertEquals(0,new CronTaskScheduler(test).listTasks().size());
  }
  
  @Test
  public void testAvailability()
  {
    CronTabTest test=new CronTabTest();
    assertNull(new CronTaskScheduler(test).checkAvailability());
  }
}
