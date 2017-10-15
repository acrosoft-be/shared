package be.acrosoft.gaia.shared.icc.drivers.beid;

import be.acrosoft.gaia.shared.icc.CardReaderDriver;
import be.acrosoft.gaia.shared.icc.CardReaderRegistry;

/**
 * BEID Driver.
 */
public class Driver implements CardReaderDriver
{
  private BEIDReader _reader;

  @Override
  public String getName()
  {
    return CardReaderRegistry.BEID;
  }

  @Override
  public void register()
  {
    _reader=new BEIDReader(this);
    CardReaderRegistry.getInstance().registerCardReader(_reader);
  }

  @Override
  public void unregister()
  {
    CardReaderRegistry.getInstance().unregisterCardReader(_reader);
    _reader.dispose();
  }

}
