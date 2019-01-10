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
import java.time.ZoneId;

/**
 * A test clock that uses an offset from a given base block.
 */
public final class OffsetClock extends Clock
{
  private Clock _clock;
  private Duration _offset;
  
  /**
   * Create a new OffsetClock.
   * @param clock base clock.
   * @param offset clock offset.
   */
  public OffsetClock(Clock clock,Duration offset)
  {
    _clock=clock;
    _offset=offset;
  }
  
  @Override
  public ZoneId getZone()
  {
    return _clock.getZone();
  }

  @Override
  public Instant instant()
  {
    return _clock.instant().plus(_offset);
  }

  @Override
  public Clock withZone(ZoneId zone)
  {
    return new OffsetClock(_clock.withZone(zone),_offset);
  }
  
  /**
   * Update the offset.
   * @param offset new offset.
   */
  public void setOffset(Duration offset)
  {
    _offset=offset;
  }
  
}
