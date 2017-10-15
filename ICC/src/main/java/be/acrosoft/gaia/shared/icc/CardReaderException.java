package be.acrosoft.gaia.shared.icc;

import be.acrosoft.gaia.shared.util.GaiaException;

/**
 * IDCardReaderException.
 */
public class CardReaderException extends GaiaException
{
  private static final long serialVersionUID=1L;

  /**
   * Create a new IDCardReaderException.
   * @param string
   */
  public CardReaderException(String string)
  {
    super(string);
  }
}
