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
