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
package be.acrosoft.gaia.shared.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ScheduleTest
{
  @Test
  public void testEveryday()
  {
    Schedule schedule=new Schedule(14*60);
    assertEquals(Schedule.Type.ONCE_EVERY_DAY,schedule.getType());
    assertEquals(14*60,schedule.getTime());
    
    Schedule otherSchedule=new Schedule(14*60);
    assertEquals(otherSchedule,schedule);
    assertEquals(otherSchedule.hashCode(),schedule.hashCode());
    
    assertFalse(schedule.equals(new Schedule(14*60+1)));
    assertFalse(schedule.equals(new Schedule(4,14*60)));
    assertFalse(schedule.equals(null));
    assertFalse(schedule.equals(14));
  }
  
  @Test
  public void testSpecificDay()
  {
    Schedule schedule=new Schedule(4,14*60);
    assertEquals(Schedule.Type.SPECIFIC_DAY,schedule.getType());
    assertEquals(14*60,schedule.getTime());
    assertEquals(4,schedule.getDay());
    
    Schedule otherSchedule=new Schedule(4,14*60);
    assertEquals(otherSchedule,schedule);
    assertEquals(otherSchedule.hashCode(),schedule.hashCode());
  }
  
  @Test
  public void testInterval()
  {
    Schedule schedule=new Schedule(4*60,8*60,1);
    assertEquals(Schedule.Type.MANY_TIMES_EVERY_DAY,schedule.getType());
    assertEquals(4*60,schedule.getStart());
    assertEquals(8*60,schedule.getStop());
    assertEquals(1,schedule.getInterval());
    
    Schedule otherSchedule=new Schedule(4*60,8*60,1);
    assertEquals(otherSchedule,schedule);
    assertEquals(otherSchedule.hashCode(),schedule.hashCode());
  }
  
  @Test
  public void testIntervalFallback()
  {
    assertEquals(new Schedule(4),new Schedule(4,10,0));
    assertEquals(new Schedule(4),new Schedule(4,10,24));
    assertEquals(new Schedule(10),new Schedule(10,4,2));
  }
}
