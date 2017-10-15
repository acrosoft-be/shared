package be.acrosoft.gaia.shared.rich;

/**
 * DocumentWriter.
 */
public interface DocumentWriter
{
  /**
   * Write the given document.
   * @param doc document to write.
   */
  public void write(RichDocument doc);
  
  /**
   * Release any resource this writer could hold on the target.
   */
  public void dispose();
}
