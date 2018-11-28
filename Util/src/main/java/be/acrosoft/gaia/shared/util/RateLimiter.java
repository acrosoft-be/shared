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
package be.acrosoft.gaia.shared.util;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * The RateLimiter is used to filter consecutive executions and limit their rate of execution.
 * <p>
 * The behavior is based on a maximum number of occurrences within a specified duration. These allowed occurrences
 * will be accepted. Any subsequent occurrence within the duration will be rejected.
 * <p>
 * Instances of this class are thread-safe.
 */
public class RateLimiter
{
  private Duration _duration;
  private int _maxOccurrences;
  private Clock _clock;

  private int _currentCount;
  private Instant _previousInstant;
  
  private Object _lock=new Object();

  private RateLimiter(Duration duration,int maxOccurrences,Clock clock)
  {
    _duration=duration;
    _maxOccurrences=maxOccurrences;
    _clock=clock;
    _currentCount=0;
    _previousInstant=null;
  }
  
  /**
   * Return whether a new occurrence should be accepted at this time.
   * @return true if the occurrence is accepted, which means that the number of occurrences does not exceed the
   * maximum within the allowed duration. Return false if the occurrence is rejected.
   */
  public boolean accept()
  {
    Instant now=_clock.instant();
    
    synchronized(_lock)
    {
      if(_previousInstant!=null)
      {
        Duration delta=Duration.between(_previousInstant,now);
        if(delta.compareTo(_duration)>0)
        {
          _currentCount=0;
        }
      }
      _currentCount++;
      _previousInstant=now;
      return _currentCount<=_maxOccurrences;
    }
    
  }
  
  /**
   * Builder.
   */
  public static class Builder
  {
    private Duration duration;
    private int maxOccurrences;
    private Clock clock;

    private Builder(Duration d)
    {
      duration=d;
      maxOccurrences=1;
      clock=Clock.systemUTC();
    }
    
    /**
     * Create the RateLimiter using the builder options.
     * @return new RateLimiter.
     */
    public RateLimiter build()
    {
      return new RateLimiter(duration,maxOccurrences,clock);
    }
    
    /**
     * Set the maximum occurrences within the minimum period. The default value is 1.
     * @param occurrences number of occurrences accepted within the minimum period.
     * @return this instance.
     */
    public Builder maxOccurrences(int occurrences)
    {
      maxOccurrences=occurrences;
      return this;
    }

    /**
     * Set the clock used to get the current time.
     * @param c clock to use.
     * @return this instance.
     */
    public Builder usingClock(Clock c)
    {
      clock=c;
      return this;
    }
  }
  
  /**
   * Create a new builder, specifying the minimum duration between occurrences.
   * @param duration minimum duration between occurrences.
   * @return a new builder.
   */
  public static Builder over(Duration duration)
  {
    return new Builder(duration);
  }
}
