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
package be.acrosoft.gaia.shared.icc;

import java.util.HashMap;
import java.util.Map;

import be.acrosoft.gaia.shared.util.Debug;

/**
 * IDCardReaderRegistry.
 */
public class CardReaderRegistry
{
  /**
   * The BE-ID driver name.
   */
  public final static String BEID="beid"; //$NON-NLS-1$
  private static CardReaderRegistry _instance;
  private Map<String,CardReader> _readers;
  private Map<String,Throwable> _errors;
  
  static
  {
    _instance=new CardReaderRegistry();
    //_instance.registerDriver(BEID);
  }
  
  private CardReaderRegistry()
  {
    _readers=new HashMap<String,CardReader>();
    _errors=new HashMap<String,Throwable>();
  }
  
  /**
   * Get the registry instance.
   * @return registry instance.
   */
  public static CardReaderRegistry getInstance()
  {
    return _instance;
  }
  
  /**
   * Register a ID card driver.
   * @param driverName driver name.
   * @return true if driver was successfully registered, false otherwise.
   */
  public boolean registerDriver(String driverName)
  {
    try
    {
      Class driverClass=Class.forName("com.acrosoft.gaia.common.core.icc.drivers."+driverName+".Driver"); //$NON-NLS-1$ //$NON-NLS-2$
      CardReaderDriver driver=(CardReaderDriver)driverClass.newInstance();
      driver.register();
      return true;
    }
    catch(Throwable th)
    {
      if(Debug.isDebug())
      {
        System.err.println("Could not load ICC driver "+driverName); //$NON-NLS-1$
        th.printStackTrace();
      }
      synchronized(_errors)
      {
        _errors.put(driverName,th);
      }
      return false;
    }
  }
  
  /**
   * Unregister an ID card driver.
   * @param driverName driver name.
   */
  public void unregisterDriver(String driverName)
  {
    CardReader reader=getReader(driverName);
    if(reader==null)
      return;
    reader.getDriver().unregister();
  }
  
  /**
   * Register a new card reader.
   * @param reader reader to register.
   */
  public void registerCardReader(CardReader reader)
  {
    synchronized(_readers)
    {
      _readers.put(reader.getDriver().getName(),reader);
    }
  }
  
  /**
   * Unregister a card reader.
   * @param reader reader to unregister.
   */
  public void unregisterCardReader(CardReader reader)
  {
    synchronized(_readers)
    {
      _readers.remove(reader.getDriver().getName());
    }
  }
  
  /**
   * Get a card reader from its driver name.
   * @param driverName driver name.
   * @return card reader, or null if none is found.
   */
  public CardReader getReader(String driverName)
  {
    synchronized(_readers)
    {
      return _readers.get(driverName);
    }
  }
  
  /**
   * Get all errors relative to driver registrations.
   * @return map of driver->throwable that occurred during registration.
   */
  public Map<String,Throwable> getErrors()
  {
    return new HashMap<String,Throwable>(_errors);
  }
  
  /**
   * Get all registered readers.
   * @return all readers.
   */
  public Map<String,CardReader> getReaders()
  {
    return new HashMap<String,CardReader>(_readers);
  }
  
  /**
   * Get the BE-ID card reader instance.
   * @return the BE-ID card reader instance, or null if BE-ID is not installed.
   */
  public static CardReader getBEIDReader()
  {
    return getInstance().getReader(BEID);
  }
}
