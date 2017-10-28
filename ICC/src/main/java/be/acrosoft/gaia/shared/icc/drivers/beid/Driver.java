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
