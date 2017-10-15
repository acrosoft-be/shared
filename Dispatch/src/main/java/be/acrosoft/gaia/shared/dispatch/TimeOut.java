package be.acrosoft.gaia.shared.dispatch;

/**
 * TimeOut implementation using the Scheduler.
 */
public class TimeOut implements Runnable
{
  private Runnable _runnable;
  private long _delay;
  private boolean _enabled;
  private Object _reference;
  
  /**
   * Create a new TimeOut. This timeout is not enabled.
   * @param runnable runnable to be executed upon timeout.
   * @param delay timeout delay.
   */
  public TimeOut(Runnable runnable,long delay)
  {
    _delay=delay;
    _runnable=runnable;
    _enabled=false;
    _reference=null;
  }
  
  /**
   * Enable the timeout. The timeout starts counting at this call.
   */
  public void enable()
  {
    if(enabled()) return;
    _reference=Scheduler.getInstance().schedule(this,System.currentTimeMillis()+_delay);
    _enabled=true;
  }
  
  /**
   * Disable the timeout.
   */
  public void disable()
  {
    if(!enabled()) return;
    _enabled=false;
    if(_reference!=null) Scheduler.getInstance().cancel(_reference);
    _reference=null;
  }
  
  /**
   * Check whether the timeout is enabled.
   * @return true if the timeout is enabled, false otherwise.
   */
  public boolean enabled()
  {
    return _enabled;
  }
  
  /**
   * Reset the timeout. If the timeout is not enabled, this method enables it. If the timeout is already enabled, the timeout restarts counting at this call.
   */
  public void reset()
  {
    if(!enabled())
      enable();
    else
      Scheduler.getInstance().reschedule(_reference,System.currentTimeMillis()+_delay);
  }

  @Override
  public void run()
  {
    _enabled=false;
    _runnable.run();
  }
  
  /**
   * Update the timeout delay. The new delay will only be used at next reset. This method
   * does not reset the current timeout.
   * @param delay new delay.
   */
  public void updateDelay(long delay)
  {
    _delay=delay;
  }
  
  /**
   * Get the current delay.
   * @return current delay.
   */
  public long getDelay()
  {
    return _delay;
  }
}
