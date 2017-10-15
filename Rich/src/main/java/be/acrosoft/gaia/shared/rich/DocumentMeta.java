package be.acrosoft.gaia.shared.rich;

/**
 * DocumentMeta.
 */
public class DocumentMeta
{
  private String _title;
  private String _author;
  
  /**
   * Create a new DocumentMeta.
   */
  public DocumentMeta()
  {
  }

  /**
   * Set the title.
   * @param title The title to set.
   */
  public void setTitle(String title)
  {
    _title=title;
  }

  /**
   * Get the title.
   * @return Returns the title.
   */
  public String getTitle()
  {
    return _title;
  }

  /**
   * Set the author.
   * @param author The author to set.
   */
  public void setAuthor(String author)
  {
    _author=author;
  }

  /**
   * Get the author.
   * @return Returns the author.
   */
  public String getAuthor()
  {
    return _author;
  }
}
