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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.Test;

/**
 * TimeThresholdTest.
 */
@SuppressWarnings({"javadoc"})
public class RateLimiterTest
{
  @Test
  public void testBuildUp()
  {
    RateLimiter tt=RateLimiter.over(Duration.ofMillis(500)).maxOccurrences(10).build();
    for(int i=0;i<10;i++) assertTrue(tt.accept());
    assertFalse(tt.accept());
  }
  
  @Test
  public void testReset() throws Exception
  {
    InstantClock clock=new InstantClock();
    RateLimiter tt=RateLimiter.over(Duration.ofMillis(500)).maxOccurrences(10).usingClock(clock).build();
    for(int i=0;i<10;i++) assertTrue(tt.accept());
    clock.set(clock.instant().plusMillis(1000));
    assertTrue(tt.accept());
  }

  @Test
  public void testContinuousNonReset() throws Exception
  {
    InstantClock clock=new InstantClock();
    RateLimiter tt=RateLimiter.over(Duration.ofMillis(1000)).maxOccurrences(10).usingClock(clock).build();
    for(int i=0;i<10;i++) assertTrue(tt.accept());
    assertFalse(tt.accept());
    clock.set(clock.instant().plusMillis(500));
    assertFalse(tt.accept());
    clock.set(clock.instant().plusMillis(500));
    assertFalse(tt.accept());
    clock.set(clock.instant().plusMillis(500));
    assertFalse(tt.accept());
    clock.set(clock.instant().plusMillis(500));
    assertFalse(tt.accept());
  }
  
  @Test
  public void testEmptyFilter() throws Exception
  {
    RateLimiter tt=RateLimiter.over(Duration.ofMillis(1000)).build();
    assertTrue(tt.accept());
    for(int i=0;i<9;i++) assertFalse(tt.accept());
  }

}
