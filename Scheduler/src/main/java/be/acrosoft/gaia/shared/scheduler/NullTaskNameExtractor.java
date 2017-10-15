package be.acrosoft.gaia.shared.scheduler;

/**
 * NullTaskNameExtractor.
 */
public class NullTaskNameExtractor implements TaskNameExtractor
{
  @Override
  public String getTaskName(String command)
  {
    return null;
  }

}
