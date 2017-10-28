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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * A Deferred will defer its execution until a condition becomes true.
 * This is implemented by re-dispatching the execution attempt while the ready
 * method returns false. The dispatch period (i.e., the delay between two
 * consecutive attempts) as specified in the constructor.
 * <br/>
 * There are two ways to use this class. Once is by extended it and implement the {@link #ready()}
 * as well as the {@link #run()} method. In some circumstances the {@link #ready()} method will perform
 * some computation that would need to be re-used by the {@link #run()} method. Such data can be
 * stored and fetched back via the get and put methods.
 * <br/>
 * The other, simpler but less sophisticated approach is to use the helper {@link #once(BooleanSupplier)} method
 * which allows the following construct: once(condition).execute(runnable).
 */
public abstract class Deferred implements Runnable
{
  private int _retryPeriod;
  private Map<Object,Object> _map;
  
  /**
   * Create a new Deferred. The attempts start immediately, hence
   * the run method will be invoked from the constructor if the ready
   * method returns true at first call.
   * @param retryPeriod retry period between execution attempts.
   */
  public Deferred(int retryPeriod)
  {
    _retryPeriod=retryPeriod;
    
    tryNow();
  }
  
  private void tryNow()
  {
    _map=new HashMap<Object,Object>();
    
    if(ready())
    {
      run();
      return;
    }
    TimeOut to=new TimeOut(new Runnable()
    {
      @Override
      public void run()
      {
        tryNow();
      }
    },_retryPeriod);
    to.enable();
  }
  
  /**
   * Get the value of the given buffer key.
   * @param <T> value type.
   * @param key key to get.
   * @return value associated with key.
   */
  protected <T> T get(Object key)
  {
    return (T)_map.get(key);
  }
  
  /**
   * Get the value of the null key.
   * @param <T> value type.
   * @return value associated with the null key.
   */
  protected <T> T get()
  {
    return (T)_map.get(null);
  }
  
  /**
   * Put the given value using the given key.
   * @param <T> value type.
   * @param key key.
   * @param value value.
   * @return value.
   */
  protected <T> T put(Object key,T value)
  {
    _map.put(key,value);
    return value;
  }
  
  /**
   * Put the given value using the null key.
   * @param <T> value type.
   * @param value value.
   * @return value.
   */
  protected <T> T put(T value)
  {
    _map.put(null,value);
    return value;
  }
  
  /**
   * Return true when the execution is ready to execute.
   * @return true if execution is ready to execute, false otherwise.
   */
  public abstract boolean ready();
  
  /**
   * ConditionHolder, which is used to create a Deferred from a condition.
   */
  public static interface ConditionHolder
  {
    /**
     * Create a new Deferred using the given Runnable instance.
     * @param r Runnable to run.
     * @return Deferred.
     */
    public Deferred execute(Runnable r);
  }
  
  /**
   * Create a new ConditionHolder that will be used to create a Deferred.
   * @param condition condition.
   * @return ConditionHolder.
   */
  public static ConditionHolder once(BooleanSupplier condition)
  {
    return new ConditionHolder()
    {
      @Override
      public Deferred execute(Runnable r)
      {
        return new Deferred(100)
        {
          @Override
          public boolean ready()
          {
            return condition.getAsBoolean();
          }
          
          @Override
          public void run()
          {
            r.run();
          }
        };
      }
    };
  }
}
