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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class CronTaskSchedulerTest
{
  private static class TestCronTab implements CronTaskScheduler.CronTab
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
  
  private static class TestFileCronTab extends CronTaskScheduler.AbstractFileCronTab
  {
    private CronTaskScheduler.CronTab delegate;
    
    public TestFileCronTab(CronTaskScheduler.CronTab d)
    {
      delegate=d;
    }
    
    @Override
    public String apply(List<String> tab)
    {
      return delegate.apply(tab);
    }
    
    @Override
    public String read(List<String> tab)
    {
      return delegate.read(tab);
    }
    
    @Override
    public void backupTab() throws IOException
    {
      super.backupTab();
    }
    
    @Override
    public void writeTab(List<String> tab,File file) throws IOException
    {
      super.writeTab(tab,file);
    }
  }

  private void delete(Path path)
  {
    try
    {
      if(Files.exists(path))
      {
        if(Files.isDirectory(path))
        {
          try(Stream<Path> stream=Files.list(path))
          {
            stream.forEach(this::delete);
          }
        }
        Files.delete(path);
      }
    }
    catch(IOException ex)
    {
      fail(ex.getMessage());
    }
  }
  
  @After
  public void cleanup()
  {
    delete(Paths.get("crontab.saved"));
  }
  
  @Test
  public void testAddEveryDay()
  {
    TestCronTab test=new TestCronTab();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(4*60),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * * command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }
  
  @Test
  public void testAddSpecificDay()
  {
    TestCronTab test=new TestCronTab();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(4,4*60),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * 5 command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }

  @Test
  public void testAddSunday()
  {
    TestCronTab test=new TestCronTab();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(6,4*60),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * 0 command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }
  
  @Test
  public void testAddInterval()
  {
    TestCronTab test=new TestCronTab();
    CronTaskScheduler scheduler=new CronTaskScheduler(test);
    String name=UUID.randomUUID().toString();
    assertNull(scheduler.createTask(new Task(name,new Schedule(2*60,23*60,2),"command","params")));
    assertEquals(1,test.value.size());
    assertEquals("0 2,4,6,8,10,12,14,16,18,20,22 * * * command params || /bin/echo 'Acrosoft taskname="+name+"' > /dev/null",test.value.get(0));
  }
  
  @Test
  public void testList()
  {
    TestCronTab test=new TestCronTab();
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
    TestCronTab test=new TestCronTab();
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
    TestCronTab test=new TestCronTab();
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
    TestCronTab test=new TestCronTab();
    test.readResult="test error";
    test.value.add("0 4 * * * command4 params4 taskname=oldname");
    new CronTaskScheduler(test);
    assertEquals(1,test.value.size());
    assertEquals("0 4 * * * command4 params4 taskname=oldname",test.value.get(0));
  }
  
  @Test
  public void testErrorOnAdd()
  {
    TestCronTab test=new TestCronTab();
    test.value.add("# some stuff");
    test.readResult="test error";
    assertEquals("test error",new CronTaskScheduler(test).createTask(new Task("name",new Schedule(0),"cmd","param")));
    assertEquals(1,test.value.size());
    assertEquals("# some stuff",test.value.get(0));
  }
  
  @Test
  public void testErrorOnDelete()
  {
    TestCronTab test=new TestCronTab();
    test.value.add("# some stuff");
    test.readResult="test error";
    assertEquals("test error",new CronTaskScheduler(test).deleteTask("task"));
    assertEquals(1,test.value.size());
    assertEquals("# some stuff",test.value.get(0));
  }
  
  @Test
  public void testErrorOnList()
  {
    TestCronTab test=new TestCronTab();
    test.value.add("# some stuff");
    test.readResult="test error";
    assertEquals(0,new CronTaskScheduler(test).listTasks().size());
  }
  
  @Test
  public void testAvailability()
  {
    TestCronTab test=new TestCronTab();
    assertNull(new CronTaskScheduler(test).checkAvailability());
  }
  
  @Test
  public void testBackupBase() throws IOException
  {
    TestCronTab test=new TestCronTab();
    test.value.add("# some stuff");
    TestFileCronTab tab=new TestFileCronTab(test);
    tab.cleanup();
    tab.backupTab();
    
    File folder=new File("crontab.saved");
    assertTrue(folder.isDirectory());
    File save=new File(folder,"crontab.save");
    assertTrue(save.isFile());
    
    List<String> lines=Files.readAllLines(save.toPath());
    assertEquals(1,lines.size());
    assertEquals("# some stuff",lines.get(0));
    
    test.value.add("# some other stuff");

    tab.backupTab();
    File save1=new File(folder,"crontab.save.1");
    assertTrue(save1.isFile());
    lines=Files.readAllLines(save1.toPath());
    assertEquals(2,lines.size());
    assertEquals("# some stuff",lines.get(0));
    assertEquals("# some other stuff",lines.get(1));
  }
  
  @Test
  public void testErrorOnBackup() throws IOException
  {
    TestCronTab test=new TestCronTab();
    test.value.add("# some stuff");
    test.readResult="test error";
    TestFileCronTab tab=new TestFileCronTab(test);
    tab.cleanup();
    tab.backupTab();
    File folder=new File("crontab.saved");
    assertTrue(folder.isDirectory());
    File save=new File(folder,"crontab.save");
    assertFalse(save.isFile());
  }
  
  @Test
  public void testCleanup() throws IOException
  {
    TestCronTab test=new TestCronTab();
    test.value.add("# some stuff");
    TestFileCronTab tab=new TestFileCronTab(test);
    tab.cleanup();
    tab.backupTab();
    tab.cleanup();
    
    File folder=new File("crontab.saved");
    assertTrue(folder.isDirectory());
    File save=new File(folder,"crontab.save");
    assertTrue(save.isFile());

    tab.backupTab();
    File save1=new File(folder,"crontab.save.1");
    assertTrue(save1.isFile());
    
    File unrelated=new File(folder,"unrelated");
    assertTrue(unrelated.createNewFile());

    unrelated.setLastModified(1L);
    save.setLastModified(1L);
    tab.cleanup();
    assertFalse(save.isFile());
    assertTrue(save1.isFile());
    assertTrue(unrelated.isFile());
  }
  
  @Test
  public void testCleanupError() throws IOException
  {
    TestCronTab test=new TestCronTab();
    test.value.add("# some stuff");
    TestFileCronTab tab=new TestFileCronTab(test);
    assertTrue(new File("crontab.saved").createNewFile());
    tab.cleanup();
    tab.backupTab();
    
    File folder=new File("crontab.saved");
    assertFalse(folder.isDirectory());
    
  }
}
