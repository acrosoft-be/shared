/**
 * Copyright Acropolis Software SPRL (http://www.acrosoft.be)
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


/**
 * Represents a schedule for a task.
 */
public class Schedule
{
  private Type _type;
  private int _day;
  private int _time;
  private int _start;
  private int _stop;
  private int _interval;
  
  /**
   * Schedule type.
   */
  public static enum Type
  {
    /**
     * Occurs at specific day.
     */
    SPECIFIC_DAY,
    /**
     * Occurs every day, once.
     */
    ONCE_EVERY_DAY,
    /**
     * Occurs every day, many times.
     */
    MANY_TIMES_EVERY_DAY
  }
  
  /**
   * Create a new Schedule occurring at specific days.
   * @param day day of occurrence, 0 to 6 for Monday to Sunday.
   * @param time time of the occurrence within the day, in minutes from midnight.
   */
  public Schedule(int day,int time)
  {
    _type=Type.SPECIFIC_DAY;
    _day=day%7;
    _time=time%(24*60);
  }
  
  /**
   * Create a new Schedule occurring everyday at specific time.
   * @param time time of the occurrence for each day, in minutes from midnight.
   */
  public Schedule(int time)
  {
    _type=Type.ONCE_EVERY_DAY;
    _time=time%(24*60);
  }
  
  /**
   * Create a new Schedule occurring every day at specific interval.
   * @param start start time of the daily occurrences, in minutes from midnight.
   * @param stop top time of the daily occurrences, in minutes from midnight.
   * @param interval interval between two occurrences, in hours.
   */
  public Schedule(int start,int stop,int interval)
  {
    if(interval<1 || interval>23 || (stop%(24*60))<(start%(24*60)))
    {
      _type=Type.ONCE_EVERY_DAY;
      _time=start%(24*60);
    }
    else
    {
      _type=Type.MANY_TIMES_EVERY_DAY;
      _start=start%(24*60);
      _stop=stop%(24*60);
      _interval=interval;
    }
  }
  
  /**
   * Get the schedule type.
   * @return schedule type.
   */
  public Type getType()
  {
    return _type;
  }
  
  /**
   * If schedule is of type SPECIFIC_DAY, returns the day of occurrence, 0-6 for Monday to Sunday.
   * @return day of specific day occurrence.
   */
  public int getDay()
  {
    return _day;
  }
  
  /**
   * If schedule is of type SPECIFIC_DAY or ONCE_EVERY_DAY, returns the time of occurrence, in minutes from midnight.
   * @return time of occurrence, in minutes from midnight.
   */
  public int getTime()
  {
    return _time;
  }
  
  /**
   * If schedule is of type MANY_TIMES_EVERY_DAY, returns the start time of occurrences, in minutes from midnight.
   * @return start time of occurrences, in minutes from midnight.
   */
  public int getStart()
  {
    return _start;
  }
  
  /**
   * If schedule is of type MANY_TIMES_EVERY_DAY, returns the stop time of occurrences, in minutes from midnight.
   * @return stop time of occurrences, in minutes from midnight.
   */
  public int getStop()
  {
    return _stop;
  }
  
  /**
   * If schedule is of type MANY_TIMES_EVERY_DAY, returns the interval between two occurrences, in hours.
   * @return interval between two occurrences, in hours.
   */
  public int getInterval()
  {
    return _interval;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if(obj==null || !(obj instanceof Schedule))
      return false;
    
    Schedule other=(Schedule)obj;
    if(_type!=other._type)
      return false;

    switch(_type)
    {
      case SPECIFIC_DAY:
        return _day==other._day&&_time==other._time;
      case ONCE_EVERY_DAY:
        return _time==other._time;
      case MANY_TIMES_EVERY_DAY:
        return _start==other._start&&_stop==other._stop&&_interval==other._interval;
    }
    return false;
  }
  
  @Override
  public int hashCode()
  {
    switch(_type)
    {
      case SPECIFIC_DAY:
        return _type.hashCode()^_day^_time;
      case ONCE_EVERY_DAY:
        return _type.hashCode()^_time;
      case MANY_TIMES_EVERY_DAY:
        return _type.hashCode()^_start^_stop^_interval;
    }
    return 0;
  }
}
