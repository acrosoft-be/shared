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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import be.acrosoft.gaia.shared.util.GaiaConstants;
import be.acrosoft.gaia.shared.util.OSSpecific;
import be.acrosoft.gaia.shared.util.ProcessTool;
import be.acrosoft.gaia.shared.util.ProcessTool.ProcessResult;

/**
 * CronTaskScheduler.
 */
@OSSpecific({GaiaConstants.OS_NAME_WINDOWS,GaiaConstants.OS_NAME_MACOSX})
public class CronTaskScheduler extends AbstractTaskScheduler
{
  /**
   * Create a new CronTaskScheduler.
   */
  public CronTaskScheduler()
  {
    setDefaultTaskNameExtractor(new CronTaskNameExtractor());
    cleanup();
  }
  
  private void cleanup()
  {
    File back=new File("crontab.saved"); //$NON-NLS-1$
    back.mkdirs();
    long now=System.currentTimeMillis();
    File[] files=back.listFiles();
    for(File file:files)
    {
      if(file.getName().startsWith("crontab.save")) //$NON-NLS-1$
      {
        long lastModified=file.lastModified();
        if(now-lastModified>1L*30*24*60*60*1000)  //30 days
        {
          file.delete();
        }
      }
    }
  }
  
  private void writeTab(List<String> tab,File file) throws IOException
  {
    Writer wr=new FileWriter(file);
    try
    {
      for(String line:tab)
      {
        wr.write(line);
        wr.write("\n"); //$NON-NLS-1$
      }
      wr.flush();
    }
    finally
    {
      wr.close();
    }
  }
  
  private String readTab(List<String> ans)
  {
    try
    {
      ProcessResult res=ProcessTool.execute("crontab -l"); //$NON-NLS-1$
      if(res.result==0)
      {
        ans.addAll(res.output);
        return null;
      }
      else if(res.result==1)
      {
        return null;
        //Probably empty
      }
      else
      {
        return res.error.toString();
      }
    }
    catch(IOException ex)
    {
      return ex.getMessage();
    }
  }
  
  private void backupTab() throws IOException
  {
    File back=new File("crontab.saved"); //$NON-NLS-1$
    File file=new File(back,"crontab.save"); //$NON-NLS-1$
    int i=0;
    while(file.exists())
    {
      i++;
      file=new File(back,"crontab.save."+i); //$NON-NLS-1$
    }
    List<String> tab=new ArrayList<String>();
    readTab(tab);
    writeTab(tab,file);
  }

  private String updateTab(List<String> tab)
  {
    try
    {
      backupTab();
      File tmp=File.createTempFile("backrest","tmp"); //$NON-NLS-1$ //$NON-NLS-2$
      try
      {
        writeTab(tab,tmp);
        ProcessResult res=ProcessTool.execute("crontab "+tmp.getAbsolutePath()); //$NON-NLS-1$
        if(res.result==0)
        {
          return null;
        }
        return toString(res);
      }
      finally
      {
        tmp.delete();
      }
    }
    catch(IOException ex)
    {
      return ex.getMessage();
    }
  }
  
  private String getCommand(String line)
  {
    if(line.startsWith("#") || line.startsWith("@")) //$NON-NLS-1$ //$NON-NLS-2$
      return null;

    String[] items=line.replace('\t',' ').split(" +",6); //$NON-NLS-1$
    if(items.length<6)
      return null;
    
    return items[5];
  }
  
  @Override
  public String createTask(Task task)
  {
    List<String> existingTab=new ArrayList<String>();
    String error=readTab(existingTab);
    if(error!=null)
    {
      return error;
    }
    
    String toAdd=null;
    
    switch(task.getSchedule().getType())
    {
      case SPECIFIC_DAY:
      {
        int day=task.getSchedule().getDay();
        day++;
        if(day==7)
        {
          day=0;
        }
        int min=task.getSchedule().getTime()%60;
        int hour=task.getSchedule().getTime()/60;
        toAdd=String.format("%1$s %2$s * * %3$s %4$s %5$s",min,hour,day,task.getCommand(),task.getParameters()); //$NON-NLS-1$
        break;
      }
      case ONCE_EVERY_DAY:
      {
        int min=task.getSchedule().getTime()%60;
        int hour=task.getSchedule().getTime()/60;
        toAdd=String.format("%1$s %2$s * * * %3$s %4$s",min,hour,task.getCommand(),task.getParameters()); //$NON-NLS-1$
        break;
      }
      case MANY_TIMES_EVERY_DAY:
      {
        int start=task.getSchedule().getStart();
        int stop=task.getSchedule().getStop();
        int interval=task.getSchedule().getInterval();
        
        int min=start%60;
        
        int current=start;
        String hours=""; //$NON-NLS-1$
        while(current<=stop)
        {
          if(hours.length()>0)
          {
            hours+=","; //$NON-NLS-1$
          }
          hours+=(current/60);
          current+=interval*60;
        }
        
        toAdd=String.format("%1$s %2$s * * * %3$s %4$s",min,hours,task.getCommand(),task.getParameters()); //$NON-NLS-1$
        break;
      }
    }
    
    if(toAdd!=null)
    {
      existingTab.add("# The following line has been added by Acrosoft's Task Scheduler utility for task "+task.getName()+". Backup of original crontab is kept in installation directory for 30 days."); //$NON-NLS-1$ //$NON-NLS-2$
      existingTab.add(toAdd);
    }
    return updateTab(existingTab);
  }

  @Override
  public String deleteTask(String name)
  {
    List<String> newTab=new ArrayList<String>();
    List<String> existingTab=new ArrayList<String>();
    String error=readTab(existingTab);
    if(error!=null)
    {
      return error;
    }
    for(String line:existingTab)
    {
      String cmd=getCommand(line);
      if(cmd!=null)
      {
        String lineName=getDefaultTaskNameExtractor().getTaskName(cmd);
        if(lineName!=null && lineName.equals(name))
        {
          continue;
        }
      }
      if(line.startsWith("#") && line.contains("Acrosoft") && line.contains("backrest") && line.contains(name)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      {
        continue;
      }
      newTab.add(line);
    }
    error=updateTab(newTab);
    return error;
  }

  @Override
  public List<TaskSummary> listTasks(TaskNameExtractor extractor)
  {
    List<String> tab=new ArrayList<String>();
    List<TaskSummary> ans=new ArrayList<TaskSummary>();
    String error=readTab(tab);
    if(error!=null)
    {
      System.err.println(error);
      return ans;
    }
    
    for(String line:tab)
    {
      String cmd=getCommand(line);
      if(cmd!=null)
      {
        String name=extractor.getTaskName(cmd);
        String params=""; //$NON-NLS-1$
        int pos=cmd.indexOf(' ');
        if(pos>=0)
        {
          params=cmd.substring(pos+1);
          cmd=cmd.substring(0,pos);
        }
        
        if(name==null)
        {
          name=cmd;
        }
          
        ans.add(new TaskSummary(name,cmd,params));
      }
    }
    
    return ans;
  }  
  
  @Override
  public String checkAvailability()
  {
    return null;
  }

}
