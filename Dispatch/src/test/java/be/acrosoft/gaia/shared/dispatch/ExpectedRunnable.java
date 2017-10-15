package be.acrosoft.gaia.shared.dispatch;

import static org.junit.Assert.assertTrue;

@SuppressWarnings({"javadoc","nls"})
public class ExpectedRunnable implements Runnable
{
  private long _expectedTime;
  private Future<Void,Throwable> _map;
  
  public ExpectedRunnable(Future<Void,Throwable> map,long time)
  {
    _expectedTime=time;
    _map=map;
  }

  @Override
  public void run()
  {
    try
    {
      long current=System.currentTimeMillis();
      assertTrue(""+current+">="+_expectedTime,current>=_expectedTime);
      assertTrue(""+current+"<"+(_expectedTime+100),current<_expectedTime+100);
      _map.setVoidResult();
    }
    catch(Throwable ex)
    {
      _map.setThrowable(ex);
    }
  }
}
