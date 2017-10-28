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
package be.acrosoft.gaia.shared.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DateTools.
 */
public class DateTools
{
  /**
   * Get the age of one person from the given birthdate.
   * @param birthDate birthdate.
   * @return age.
   */
  public static int getAge(Date birthDate)
  {
    return getAge(birthDate,new Date());
  }

  /**
   * Get the age of one person from the given birthdate.
   * @param birthDate birthdate.
   * @param refDate current date.
   * @return age.
   */
  public static int getAge(Date birthDate,Date refDate)
  {
    GregorianCalendar birth=new GregorianCalendar();
    birth.setTime(birthDate);
    GregorianCalendar ref=new GregorianCalendar();
    ref.setTime(refDate);
    
    int deltaYears=ref.get(Calendar.YEAR)-birth.get(Calendar.YEAR);
    
    if(ref.get(Calendar.DAY_OF_YEAR)<birth.get(Calendar.DAY_OF_YEAR))
    {
      deltaYears--;
    }
    
    
    return deltaYears;
  }
}
