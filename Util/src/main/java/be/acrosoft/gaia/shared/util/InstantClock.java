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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * A test clock that always return the (mutable) specified instant.
 */
public final class InstantClock extends Clock
{
  private ZoneId _zone;
  private Instant _instant;
  
  /**
   * Create a new InstantClock. The Instant returned is the time of the InstantClock creation.
   */
  public InstantClock()
  {
    this(ZoneOffset.UTC);
  }
  
  /**
   * Create a new InstantClock. The Instant returned is the time of the InstantClock creation.
   * @param zone timezone.
   */
  public InstantClock(ZoneId zone)
  {
    _zone=zone;
    _instant=Instant.now();
  }
  
  @Override
  public ZoneId getZone()
  {
    return _zone;
  }

  @Override
  public Instant instant()
  {
    return _instant;
  }

  @Override
  public Clock withZone(ZoneId zone)
  {
    return new InstantClock(zone);
  }
  
  /**
   * Update the Instant that is returned by the {@link #instant()} method.
   * @param instant new instant.
   */
  public void set(Instant instant)
  {
    _instant=instant;
  }
  
  /**
   * Update the Instant that is returned by the {@link #instant()} method by moving time forward by the given
   * number of milliseconds.
   * @param millis milliseconds.
   */
  public void sleep(long millis)
  {
    set(instant().plusMillis(millis));
  }
  
}
