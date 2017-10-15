package be.acrosoft.gaia.shared.scheduler;


/**
 * CronTaskNameExtractor.
 */
public class CronTaskNameExtractor implements TaskNameExtractor
{
  @Override
  public String getTaskName(String command)
  {
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
        return null;
      return name;
    }
    if(pos==0)
    {
      return null;
    }
    return name.substring(0,pos);
  }
}
