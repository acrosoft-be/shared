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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
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
  static interface CronTab
  {
    public void cleanup();
    public String read(List<String> tab);
    public String apply(List<String> tab);
  }
  
  static abstract class AbstractFileCronTab implements CronTab
  {
    @Override
    public void cleanup()
    {
      File back=new File("crontab.saved"); //$NON-NLS-1$
      if(!back.mkdirs())
      {
        if(!back.isDirectory()) return;
      }
      long now=System.currentTimeMillis();
      File[] files=back.listFiles();
      if(files==null) return;
      for(File file:files)
      {
        if(file.getName().startsWith("crontab.save")) //$NON-NLS-1$
        {
          long lastModified=file.lastModified();
          if(lastModified>0 && now-lastModified>1L*30*24*60*60*1000)  //30 days
          {
            if(!file.delete())
              file.deleteOnExit();
          }
        }
      }
    }
    
    protected void writeTab(List<String> tab,File file) throws IOException
    {
      //Use default charset on purpose
      try(Writer wr=new OutputStreamWriter(new FileOutputStream(file),Charset.defaultCharset()))
      {
        for(String line:tab)
        {
          wr.write(line);
          wr.write("\n"); //$NON-NLS-1$
        }
        wr.flush();
      }
    }
    
    protected void backupTab() throws IOException
    {
      File back=new File("crontab.saved"); //$NON-NLS-1$
      if(!back.isDirectory()) return;
      File file=new File(back,"crontab.save"); //$NON-NLS-1$
      int i=0;
      while(file.exists())
      {
        i++;
        file=new File(back,"crontab.save."+i); //$NON-NLS-1$
      }
      List<String> tab=new ArrayList<String>();
      String readResult=read(tab);
      if(readResult==null)
      {
        writeTab(tab,file);
      }
      else
      {
        System.err.println(readResult);
      }
    }
    
  }
  
  private static class ProcessCronTab extends AbstractFileCronTab
  {
    @Override
    public String read(List<String> ans)
    {
      try
      {
        ProcessResult res=ProcessTool.execute("crontab","-l"); //$NON-NLS-1$ //$NON-NLS-2$
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
          return AbstractTaskScheduler.toString(res);
        }
      }
      catch(IOException ex)
      {
        return ex.getMessage();
      }
    }
    
    @Override
    public String apply(List<String> tab)
    {
      try
      {
        backupTab();
        File tmp=File.createTempFile("acrosoft","tmp"); //$NON-NLS-1$ //$NON-NLS-2$
        try
        {
          writeTab(tab,tmp);
          ProcessResult res=ProcessTool.execute("crontab",tmp.getAbsolutePath()); //$NON-NLS-1$
          if(res.result==0)
          {
            return null;
          }
          return AbstractTaskScheduler.toString(res);
        }
        finally
        {
          if(!tmp.delete())
            tmp.deleteOnExit();
        }
      }
      catch(IOException ex)
      {
        return ex.getMessage();
      }
    }
    
  }
  
  private static final String PREFIX=" || /bin/echo 'Acrosoft taskname="; //$NON-NLS-1$
  private static final String POSTFIX="' > /dev/null"; //$NON-NLS-1$
  
  private CronTab _tab;
  
  CronTaskScheduler(CronTab tab)
  {
    _tab=tab;
    _tab.cleanup();
    migrate();
  }
  
  /**
   * Create a new CronTaskScheduler.
   */
  public CronTaskScheduler()
  {
    this(new ProcessCronTab());
  }
  
  private String getTaskName(String command)
  {
    if(command==null) return null;
    
    String key="taskname="; //$NON-NLS-1$
    int pos=command.indexOf(key);
    if(pos<0)
    {
      return null;
    }
    String name=command.substring(pos+key.length());
    pos=name.indexOf(' ');
    if(pos<0)
    {
      if(name.length()==0)
      {
        return null;
      }
      return name;
    }
    if(pos==0)
    {
      return null;
    }
    return name.substring(0,pos);
  }
  
  private void migrate()
  {
    List<String> existingTab=new ArrayList<>();
    String error=_tab.read(existingTab);
    if(error!=null) return;
    
    List<String> newTab=new ArrayList<>();
    for(String line:existingTab)
    {
      if(line.startsWith("#") && line.contains("Acrosoft")) //$NON-NLS-1$ //$NON-NLS-2$
      {
        continue;
      }
      if(!line.contains(PREFIX))
      {
        String name=getTaskName(getCommand(line));
        if(name!=null)
        {
          line=line.replace(" taskname="+name,""); //$NON-NLS-1$ //$NON-NLS-2$
          line=line+PREFIX+name+POSTFIX;
        }
      }
      
      newTab.add(line);
    }
    
    _tab.apply(newTab);
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
    String error=_tab.read(existingTab);
    if(error!=null)
    {
      return error;
    }
    
    String command=String.format("%1$s %2$s%3$s%4$s%5$s",task.getCommand(),task.getParameters(),PREFIX,task.getName(),POSTFIX); //$NON-NLS-1$
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
        toAdd=String.format("%1$s %2$s * * %3$s %4$s",min,hour,day,command); //$NON-NLS-1$
        break;
      }
      case ONCE_EVERY_DAY:
      {
        int min=task.getSchedule().getTime()%60;
        int hour=task.getSchedule().getTime()/60;
        toAdd=String.format("%1$s %2$s * * * %3$s",min,hour,command); //$NON-NLS-1$
        break;
      }
      case MANY_TIMES_EVERY_DAY:
      {
        int start=task.getSchedule().getStart();
        int stop=task.getSchedule().getStop();
        int interval=task.getSchedule().getInterval();
        
        int min=start%60;
        
        int current=start;
        StringBuilder hours=new StringBuilder();
        while(current<=stop)
        {
          if(hours.length()>0)
          {
            hours.append(","); //$NON-NLS-1$
          }
          hours.append(current/60);
          current+=interval*60;
        }
        
        toAdd=String.format("%1$s %2$s * * * %3$s",min,hours.toString(),command); //$NON-NLS-1$
        break;
      }
    }
    
    if(toAdd!=null)
    {
      existingTab.add(toAdd);
    }
    return _tab.apply(existingTab);
  }

  @Override
  public String deleteTask(String name)
  {
    List<String> newTab=new ArrayList<String>();
    List<String> existingTab=new ArrayList<String>();
    String error=_tab.read(existingTab);
    if(error!=null)
    {
      return error;
    }
    for(String line:existingTab)
    {
      String cmd=getCommand(line);
      if(cmd!=null)
      {
        int pos=cmd.indexOf(PREFIX);
        if(pos>=0)
        {
          String task=cmd.substring(pos+PREFIX.length());
          pos=task.indexOf(POSTFIX);
          if(pos>0)
          {
            task=task.substring(0,pos);
            if(name.equals(task))
            {
              continue;
            }
          }
        }
      }
      newTab.add(line);
    }
    error=_tab.apply(newTab);
    return error;
  }

  @Override
  public List<TaskSummary> listTasks()
  {
    List<String> tab=new ArrayList<String>();
    List<TaskSummary> ans=new ArrayList<TaskSummary>();
    String error=_tab.read(tab);
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
        int pos=cmd.indexOf(PREFIX);
        if(pos>=0)
        {
          String task=cmd.substring(pos+PREFIX.length());
          cmd=cmd.substring(0,pos);
          pos=task.indexOf(POSTFIX);
          if(pos>0)
          {
            task=task.substring(0,pos);
            String params=""; //$NON-NLS-1$
            pos=cmd.indexOf(' ');
            if(pos>=0)
            {
              params=cmd.substring(pos+1);
              cmd=cmd.substring(0,pos);
            }
        
            ans.add(new TaskSummary(task,cmd,params));
          }
        }
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
