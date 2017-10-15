package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.junit.Test;

/**
 * DateToolsTest.
 */
@SuppressWarnings({"javadoc"})
public class DateToolsTest
{
  @Test
  public void testSimple()
  {
    GregorianCalendar birth=new GregorianCalendar(1980,2,6);
    GregorianCalendar against=new GregorianCalendar(1990,5,7);
    assertEquals(10,DateTools.getAge(birth.getTime(),against.getTime()));
  }
  
  @Test
  public void testBeforeInYear()
  {
    GregorianCalendar birth=new GregorianCalendar(1980,2,6);
    GregorianCalendar against=new GregorianCalendar(1990,1,3);
    assertEquals(9,DateTools.getAge(birth.getTime(),against.getTime()));
  }

  @Test
  public void testAtBirthDay()
  {
    GregorianCalendar birth=new GregorianCalendar(1980,2,6);
    GregorianCalendar against=new GregorianCalendar(1990,2,6);
    assertEquals(9,DateTools.getAge(birth.getTime(),against.getTime()));
  }
}
