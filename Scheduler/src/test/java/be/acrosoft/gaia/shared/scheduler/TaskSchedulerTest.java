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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Test;

import be.acrosoft.gaia.shared.util.Platform;

@SuppressWarnings({"javadoc","nls"})
public class TaskSchedulerTest
{
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

  private boolean available()
  {
    try
    {
      TaskSchedulerRegistry.getTaskScheduler().checkAvailability();
      return true;
    }
    catch(TaskSchedulerException ex)
    {
      return false;
    }
  }
  
  private void testSchedule(Schedule schedule) throws TaskSchedulerException
  {
    TaskScheduler sch=TaskSchedulerRegistry.getTaskScheduler();
    if(!available()) return;
    
    String name=UUID.randomUUID().toString();
    
    Task task=new Task(name,schedule,"nothing","param");
    
    sch.createTask(task);
    try
    {
      boolean found=false;
      List<TaskSummary> tasks = sch.listTasks();
      for(TaskSummary t:tasks)
      {
        if(t.getName().equals(name))
        {
          found=true;
          assertEquals("nothing",t.getCommand());
          assertEquals("param",t.getParameters());
        }
      }
      assertTrue(found);
    }
    finally
    {
      sch.deleteTask(name);
    }
    
    List<TaskSummary> tasks = sch.listTasks();
    for(TaskSummary t:tasks)
    {
      if(t.getName().equals(name))
      {
        fail("Unexpected task");
      }
    }
  }
  
  @Test
  public void testSchedule() throws TaskSchedulerException
  {
    testSchedule(new Schedule(12*60));
    for(int d=0;d<=6;d++)
    {
      testSchedule(new Schedule(d,12*60));
    }
    testSchedule(new Schedule(4*60,20*60,2));
  }
  
  @Test
  public void testWrongTime()
  {
    if(!Platform.isWindows()) return;
    TaskScheduler sch=TaskSchedulerRegistry.getTaskScheduler();
    if(!available()) return;
    String name=UUID.randomUUID().toString();
    Task task=new Task(name,new Schedule(-60),"nothing","param");
    try
    {
      sch.createTask(task);
      fail("Unexpected success");
    }
    catch(TaskSchedulerException ex)
    {
    }
  }

  @Test
  public void testWrongDay()
  {
    if(!Platform.isWindows()) return;
    TaskScheduler sch=TaskSchedulerRegistry.getTaskScheduler();
    if(!available()) return;
    String name=UUID.randomUUID().toString();
    Task task=new Task(name,new Schedule(-1,60),"nothing","param");
    try
    {
      sch.createTask(task);
      fail("Unexpected success");
    }
    catch(TaskSchedulerException ex)
    {
    }
  }

  @Test
  public void testWrongInterval()
  {
    if(!Platform.isWindows()) return;
    TaskScheduler sch=TaskSchedulerRegistry.getTaskScheduler();
    if(!available()) return;
    String name=UUID.randomUUID().toString();
    Task task=new Task(name,new Schedule(10,20,2),"nothing","param");
    try
    {
      sch.createTask(task);
      fail("Unexpected success");
    }
    catch(TaskSchedulerException ex)
    {
    }
  }
  
  @Test
  public void deleteWrongTask() throws TaskSchedulerException
  {
    TaskScheduler sch=TaskSchedulerRegistry.getTaskScheduler();
    if(!available()) return;
    String name=UUID.randomUUID().toString();
    sch.deleteTask(name);
  }
}
