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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.acrosoft.gaia.shared.util.GaiaConstants;
import be.acrosoft.gaia.shared.util.OSSpecific;
import be.acrosoft.gaia.shared.util.ProcessTool;
import be.acrosoft.gaia.shared.util.ProcessTool.ProcessResult;

/**
 * WindowsTaskScheduler.
 */
@OSSpecific(GaiaConstants.OS_NAME_WINDOWS)
public class WindowsTaskScheduler extends AbstractTaskScheduler
{
  private static final Logger LOGGER=Logger.getLogger(WindowsTaskScheduler.class.getName());

  private String _unavailable=null;
  private long _lastCheck=0;
  private Object _lock=new Object();
  
  private String pad2(String v)
  {
    String ans=v;
    while(ans.length()<2)
    {
      ans="0"+ans; //$NON-NLS-1$
    }
    return ans;
  }
  
  private String time(int mins)
  {
    return pad2(""+mins/60)+":"+pad2(""+mins%60); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
  
  @Override
  public void createTask(Task task) throws TaskSchedulerException
  {
    checkAvailability();

    String versionString=System.getProperty("os.version"); //$NON-NLS-1$
    int pos=versionString.indexOf('.');
    if(pos>0)
    {
      versionString=versionString.substring(0,pos);
    }
    int version;
    try
    {
      version=Integer.parseInt(versionString);
    }
    catch(NumberFormatException ex)
    {
      version=0;
    }

    StringBuilder bld=new StringBuilder();
    bld.append("schtasks /create /tn \""); //$NON-NLS-1$
    bld.append(task.getName());
    bld.append("\" /tr \"'"); //$NON-NLS-1$
    bld.append(task.getCommand());
    bld.append("'"); //$NON-NLS-1$
    if(task.getParameters().length()>0)
    {
      bld.append(' ');
      bld.append(task.getParameters());
    }
    bld.append("\" "); //$NON-NLS-1$
    if(version>5)
    {
      bld.append("/f "); //$NON-NLS-1$
    }
    
    switch(task.getSchedule().getType())
    {
      case SPECIFIC_DAY:
        String weekly="WEEKLY"; //$NON-NLS-1$
        String day=null;
        switch(task.getSchedule().getDay()%7)
        {
          case 0:
            day="MON"; //$NON-NLS-1$
            break;
          case 1:
            day="TUE"; //$NON-NLS-1$
            break;
          case 2:
            day="WED"; //$NON-NLS-1$
            break;
          case 3:
            day="THU"; //$NON-NLS-1$
            break;
          case 4:
            day="FRI"; //$NON-NLS-1$
            break;
          case 5:
            day="SAT"; //$NON-NLS-1$
            break;
          case 6:
            day="SUN"; //$NON-NLS-1$
            break;
          default:
            break;
        }
        
        if(version<=5)
        {
          weekly=Messages.getString("WindowsTaskScheduler."+weekly); //$NON-NLS-1$
          day=Messages.getString("WindowsTaskScheduler."+day); //$NON-NLS-1$
        }
        
        bld.append("/sc "); //$NON-NLS-1$
        bld.append(weekly);
        bld.append(" /d "); //$NON-NLS-1$
        bld.append(day);
        
        bld.append(" /st "); //$NON-NLS-1$
        bld.append(time(task.getSchedule().getTime())+":00"); //$NON-NLS-1$
        if(version<=5)
        {
          bld.append(" /ru SYSTEM "); //$NON-NLS-1$
        }
        break;
      case ONCE_EVERY_DAY:
        bld.append("/sc DAILY /st "); //$NON-NLS-1$
        bld.append(time(task.getSchedule().getTime())+":00"); //$NON-NLS-1$
        if(version<=5)
        {
          bld.append(" /ru SYSTEM "); //$NON-NLS-1$
        }
        break;
      case MANY_TIMES_EVERY_DAY:
        
        if(version<=5)
        {
          bld.append("/sc MINUTE /mo "); //$NON-NLS-1$
          bld.append(task.getSchedule().getInterval()*60);
          bld.append(" /st "); //$NON-NLS-1$
          bld.append(time(task.getSchedule().getStart())+":00"); //$NON-NLS-1$
          bld.append(" /ru SYSTEM "); //$NON-NLS-1$
        }
        else
        {
          bld.append("/sc DAILY /st "); //$NON-NLS-1$
          bld.append(time(task.getSchedule().getStart()));
          bld.append(" /du "); //$NON-NLS-1$
          bld.append(time(task.getSchedule().getStop()-task.getSchedule().getStart()+1));
          bld.append(" /ri "); //$NON-NLS-1$
          bld.append(task.getSchedule().getInterval()*60);
        }
        break;
    }
    
    try
    {
      ProcessResult res=ProcessTool.execute(bld.toString().split(" ")); //$NON-NLS-1$
      if(res.result!=0)
      {
        throw new TaskSchedulerException(toString(res));
      }
    }
    catch(IOException ex)
    {
      throw new TaskSchedulerException(ex.getMessage(),ex);
    }
  }

  @Override
  public void deleteTask(String name) throws TaskSchedulerException
  {
    checkAvailability();
    try
    {
      ProcessResult res=ProcessTool.execute("schtasks","/delete","/TN","\""+name+"\"","/F"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
      if(res.result!=0 && res.result!=1)
      {
        throw new TaskSchedulerException(toString(res));
      }
    }
    catch(IOException ex)
    {
      throw new TaskSchedulerException(ex.getMessage(),ex);
    }
  }

  private List<String> parse(String line)
  {
    List<String> ans=new ArrayList<String>();
    if(line.length()==0)
    {
      return ans;
    }
    
    boolean inQuote=false;
    
    int lastStart=0;
    
    for(int i=0;i<line.length();i++)
    {
      char c=line.charAt(i);
      if(c=='"')
      {
        inQuote=!inQuote;
      }
      
      if(!inQuote && c==',')
      {
        String item=line.substring(lastStart+1,i-1);
        lastStart=i+1;
        ans.add(item);
      }
    }

    String item=line.substring(lastStart+1,line.length()-1);
    ans.add(item);
    
    return ans;
  }
  
  @Override
  public List<TaskSummary> listTasks() throws TaskSchedulerException
  {
    List<TaskSummary> ans=new ArrayList<TaskSummary>();
    checkAvailability();
    
    try
    {
      ProcessResult res=ProcessTool.execute("schtasks","/query","/FO","csv","/V"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
      if(res.result==0)
      {
        for(String line:res.output)
        {
          List<String> parsed=parse(line);
          if(parsed.size()>8)
          {
            String name=parsed.get(1);
            String cmd=parsed.get(8);
            if(name.startsWith("\\")) //$NON-NLS-1$
            {
              int pos=name.lastIndexOf('\\');
              if(pos>=0)
              {
                name=name.substring(pos+1);
              }
              
              String params=""; //$NON-NLS-1$
              
              if(cmd.startsWith("\"")) //$NON-NLS-1$
              {
                cmd=cmd.substring(1);
                int endOfQuote=cmd.indexOf('"');
                if(endOfQuote>=0)
                {
                  params=cmd.substring(endOfQuote+1).trim();
                  cmd=cmd.substring(0,endOfQuote).trim();
                }
              }
              else
              {
                int split=cmd.indexOf(' ');
                if(split>=0)
                {
                  params=cmd.substring(split+1).trim();
                  cmd=cmd.substring(0,split).trim();
                }
              }
              
              ans.add(new TaskSummary(name,cmd,params));
            }
          }
        }
      }
      else
      {
        LOGGER.warning(toString(res));
      }
    }
    catch(IOException ex)
    {
      LOGGER.log(Level.WARNING,ex.getLocalizedMessage(),ex);
    }
    
    return ans;
  }
  
  @Override
  public void checkAvailability() throws TaskSchedulerException
  {
    synchronized(_lock)
    {
      long now=System.currentTimeMillis();
      if(now-_lastCheck>30*1000L)
      {
        _lastCheck=now;
        try
        {
          ProcessResult res=ProcessTool.execute("sc","query","Schedule"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          if(res.result!=0)
          {
            _unavailable=Messages.getString("WindowsTaskScheduler.StatusError",toString(res)); //$NON-NLS-1$
          }
          else
          {
            _unavailable=Messages.getString("WindowsTaskScheduler.StatusNotRunning"); //$NON-NLS-1$
            for(String l:res.output)
            {
              if(l.contains("STATE") && l.contains("RUNNING")) //$NON-NLS-1$ //$NON-NLS-2$
              {
                _unavailable=null;
                break;
              }
            }
          }
        }
        catch(IOException ex)
        {
          _unavailable=Messages.getString("WindowsTaskScheduler.StatusError",ex.getMessage()); //$NON-NLS-1$
        }
      }
      if(_unavailable!=null) throw new TaskSchedulerException(_unavailable);
    }
  }
  
}
