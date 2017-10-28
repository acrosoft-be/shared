/**
 * Copyright Acropolis Software SPRL (https://www.acrosoft.be)
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

import org.junit.Test;

import be.acrosoft.gaia.shared.dispatch.Dispatcher;
import be.acrosoft.gaia.shared.dispatch.SimpleAsyncInvoker;


/**
 * IDCardReaderTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class IDCardReaderTest
{
  private void printData(IDData data)
  {
    System.out.println(data.id);
    System.out.println(data.familyName);
    System.out.println(data.firstName);
    System.out.println(data.street);
    System.out.println(data.city);
    System.out.println(data.zip);
    System.out.println(data.country);
    System.out.println(data.birthDate);
  }  
  
  @Test
  public void testBase() throws Throwable
  {
    Dispatcher.init(new SimpleAsyncInvoker());
    CardReaderRegistry.getInstance().registerDriver(CardReaderRegistry.BEID);
    
    CardReader reader=CardReaderRegistry.getBEIDReader();
    if(reader==null)
    {
      System.out.println("No BE-ID installed, skipping test");
      return;
    }
    CardReaderMonitor monitor=reader.getMonitor();
    monitor.getEventDelegate().add(new CardReaderMonitorListener()
    {

      @Override
      public void cardInserted(CardReader r,IDData data)
      {
        System.out.println("*** card inserted ***");
        printData(data);
      }

      @Override
      public void cardRemoved(CardReader r)
      {
        System.out.println("*** card removed ***");
      }
    });
    
    
    /*final AsyncToSyncMapping<IDData,Throwable> map=new AsyncToSyncMapping<IDData,Throwable>();
    
    reader.readIDData(new CardReaderListener()
        {
          @Override
          public void dataRetreived(DataRetreivedEvent event)
          {
            map.setResult(event.getIDData());
          }

          @Override
          public void errorOccured(ErrorOccuredEvent event)
          {
            map.setThrowable(event.getThrowable());
          }
      
        },false,null);
    
    try
    {
      IDData data=map.getResult();
      printData(data);
    }
    catch(CardReaderException ex)
    {
      ex.printStackTrace();
    }*/
    
    while(System.in.available()==0)
      Dispatcher.yield(true);
    
    CardReaderRegistry.getInstance().unregisterDriver(CardReaderRegistry.BEID);
    Dispatcher.flush();
    
  }
}
