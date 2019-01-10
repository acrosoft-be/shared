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
package be.acrosoft.gaia.shared.dispatch;

/**
 * A timer proposes a mechanism for executing actions on a regular basis, using a specified internal of time between each
 * action. The next action is performed after the specified internal in milliseconds, counting from the *beginning* of the
 * previous action. Therefore, there is no possible global drift. Note that is the action must be executed more often than
 * what is possible (ie, the 'next' action should occur in the past), then actions are skipped. That is, there is no
 * possible action execution 'debt'.
 */
public class Timer implements Runnable
{
  private Runnable _runnable;
  private long _interval;
  private boolean _enabled;
  private Object _reference;
  private long _expected;

  /**
   * Create a new Timer. The timer is not enabled.
   * @param runnable runnable to be executed at each interval when the timer is enabled.
   * @param interval timer interval.
   */
  public Timer(Runnable runnable,long interval)
  {
    if(interval<=0) throw new IllegalArgumentException();
    _runnable=runnable;
    _interval=interval;
    _enabled=false;
    _reference=null;
  }
  
  /**
   * Enable the timer. If the timer is already enabled, this has no effect. The first action will be executed after
   * the interval exhaustion starting at this call.
   */
  public void enable()
  {
    if(enabled()) return;
    Scheduler sc=Scheduler.getInstance();
    _expected=sc.getClock().millis()+_interval;
    _reference=sc.schedule(this,_expected);
    _enabled=true;
  }
  
  /**
   * Disable the timer. If the timer is not enabled, this has no effect.
   */
  public void disable()
  {
    if(!enabled()) return;
    _enabled=false;
    if(_reference!=null) Scheduler.getInstance().cancel(_reference);
    _reference=null;
  }
  
  /**
   * Check whether the timer is enabled or not.
   * @return true if the timer is enabled, false otherwise.
   */
  public boolean enabled()
  {
    return _enabled;
  }
  
  @Override
  public void run()
  {
    _runnable.run();
    Scheduler sc=Scheduler.getInstance();
    if(enabled())
    {
      //Ignore action debt.
      _expected+=_interval;
      long now=sc.getClock().millis();
      if(_expected<now || _expected>now+_interval) _expected=now+_interval;
      _reference=sc.schedule(this,_expected);
    }
  }
}
