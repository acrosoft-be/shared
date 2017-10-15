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
    monitor.addIDCardReaderMonitorListener(new CardReaderMonitorListener()
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
