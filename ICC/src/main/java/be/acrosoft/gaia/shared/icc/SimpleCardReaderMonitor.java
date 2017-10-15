package be.acrosoft.gaia.shared.icc;

import be.acrosoft.gaia.shared.dispatch.TimeOut;

/**
 * IDCardReaderMonitor.
 */
public class SimpleCardReaderMonitor extends AbstractCardReaderMonitor
{
  private TimeOut _timeOut;
  private boolean _disposed;
  private Object _lock=new Object();
  
  private class Monitor implements Runnable,CardReaderListener
  {
    private String _lastId;
    private boolean _knowLast;
    
    public Monitor()
    {
      _lastId=null;
      _knowLast=false;
    }
    
    @Override
    public void run()
    {
      getMonitoredReader().readIDData(this,true);
    }
    
    private void schedule(int to)
    {
      synchronized(_lock)
      {
        if(_disposed)
          return;
        _timeOut=new TimeOut(this,to);
        _timeOut.enable();
      }
    }

    @Override
    public void dataRetreived(IDData data)
    {
      if(_lastId==null || !_lastId.equals(data.id))
      {
        //Don't fire an event if the card was inserted at the time we started
        //monitoring.
        if(!_knowLast)
        {
          _lastId=data.id;
          _knowLast=true;
          //We have a card, next check in 10s
          schedule(10000);
          return;
        }
        
        if(_lastId!=null)
          fireRemovedListeners();
        _lastId=null;
        
        getMonitoredReader().readIDData(new CardReaderListener()
        {
          @Override
          public void dataRetreived(IDData dta)
          {
            _lastId=dta.id;
            _knowLast=true;
            fireInsertedListeners(dta);
            //We have a card, next check in 10s
            schedule(10000);
          }
          
          @Override
          public void errorOccured(Throwable th)
          {
            //We don't have a card, next check in 1s
            schedule(1000);
          }
        },false);
        
      }
      else
      {
        //We have a card, next check in 10s
        schedule(10000);
      }
    }

    @Override
    public void errorOccured(Throwable th)
    {
      _knowLast=true;
      if(_lastId!=null)
      {
        _lastId=null;
        fireRemovedListeners();
      }
      //We don't have a card, next check in 1s
      schedule(1000);
      
    }
  }
  
  /**
   * Create a new CardReaderMonitor.
   * @param reader reader to monitor.
   */
  protected SimpleCardReaderMonitor(CardReader reader)
  {
    super(reader);
    _timeOut=new TimeOut(new Monitor(),1000);
    _timeOut.enable();
    _disposed=false;
  }
  
  /**
   * Dispose the monitor.
   */
  public void dispose()
  {
    synchronized(_lock)
    {
      _disposed=true;
      _timeOut.disable();
    }
  }
  

}
