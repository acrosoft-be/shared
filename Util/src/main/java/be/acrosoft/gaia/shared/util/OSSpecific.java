package be.acrosoft.gaia.shared.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OSSpecific.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface OSSpecific
{
  /**
   * OS names.
   */
  String[] value();
}
