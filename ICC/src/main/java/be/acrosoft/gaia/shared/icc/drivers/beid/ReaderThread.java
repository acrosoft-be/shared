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
package be.acrosoft.gaia.shared.icc.drivers.beid;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import be.acrosoft.gaia.shared.dispatch.Dispatcher;
import be.acrosoft.gaia.shared.dispatch.Listener;
import be.acrosoft.gaia.shared.icc.CardReader;
import be.acrosoft.gaia.shared.icc.CardReaderException;
import be.acrosoft.gaia.shared.icc.CardReaderListener;
import be.acrosoft.gaia.shared.icc.CardReaderMonitor;
import be.acrosoft.gaia.shared.icc.CardReaderMonitorListener;
import be.acrosoft.gaia.shared.icc.IDData;
import be.acrosoft.gaia.shared.util.GaiaRuntimeException;
import be.belgium.eid.eidlib.BeID;
import be.belgium.eid.exceptions.NoReadersFoundException;
import be.belgium.eid.objects.IDAddress;
import be.belgium.eid.objects.IDPhoto;
import be.belgium.eid.objects.SmartCardReadable;

/**
 * ReaderThread.
 */
@SuppressWarnings("restriction")
public class ReaderThread extends Thread implements CardReaderMonitor
{
  private static final Logger LOGGER=Logger.getLogger(ReaderThread.class.getName());
  
  private static class Request
  {
    public static enum Command
    {
      FETCH,
      TERMINATE,
    }
    
    public Command command;
    public CardReaderListener listener;
    public boolean idOnly;
  }
  
  private List<Request> _requests;
  private CardReader _reader;
  private CardReaderMonitorListener _listeners;
  
  private BeID _eID;
  
  /**
   * Create a new ReaderThread.
   * @param reader card reader.
   */
  public ReaderThread(CardReader reader)
  {
    super("BE-ID reader thread"); //$NON-NLS-1$
    _reader=reader;
    _listeners=Listener.groupOf(CardReaderMonitorListener.class);
    _requests=new LinkedList<Request>();
    _eID=null;

    CardTerminals terminals=TerminalFactory.getDefault().terminals();
    try
    {
      if(terminals.list().size()==0)
      {
        throw new GaiaRuntimeException(new CardReaderException("No card reader found")); //$NON-NLS-1$
      }
    }
    catch(CardException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
    
    start();
  }
  
  /**
   * Fetch ID data.
   * @param listener listener.
   * @param idOnly true if only id should be retreived.
   */
  public void fetchData(CardReaderListener listener,boolean idOnly)
  {
    synchronized(_requests)
    {
      Request req=new Request();
      req.command=Request.Command.FETCH;
      req.listener=listener;
      req.idOnly=idOnly;
      
      _requests.add(req);
      _requests.notify();
    }
  }
  
  private void terminate()
  {
    synchronized(_requests)
    {
      Request req=new Request();
      req.command=Request.Command.TERMINATE;
      
      _requests.add(req);
      _requests.notify();
    }
  }

  private static Date betterDate(Date date) throws ParseException
  {
    GregorianCalendar cal=new GregorianCalendar();
    cal.setTime(date);
    
    int year=cal.get(Calendar.YEAR);
    if(year<30)
    {
      cal.set(Calendar.YEAR,year+2000);
    }
    else if(year<100)
    {
      cal.set(Calendar.YEAR,year+1900);
    }
    else if(year>100 && year<1700)
    {
      throw new ParseException("Invalid year",0); //$NON-NLS-1$
    }
    
    return cal.getTime();
  }
  
  private static String betterString(String string)
  {
    if(string==null) return null;
    try
    {
      byte[] data=string.getBytes();
      return new String(data,"UTF-8").trim(); //$NON-NLS-1$
    }
    catch(UnsupportedEncodingException e)
    {
      return string.trim();
    }
  }
  
  /**
   * Fill the given data from the given raw byte arrays.
   * @param data data to fill.
   * @param idOnly true if only id should be extracted (other byte arrays will be ignored).
   * @param citizen citizen file.
   * @param address address file (ignored if idOnly is true).
   * @param photo photo file (ignored if idOnly is true).
   * @throws Exception in case of problem.
   */
  public static void fillData(IDData data,boolean idOnly,byte[] citizen,byte[] address,byte[] photo) throws Exception
  {
    data.rawCitizenInfo=citizen;
    be.belgium.eid.objects.IDData beData=be.belgium.eid.objects.IDData.parse(data.rawCitizenInfo);

    data.id=betterString(beData.getNationalNumber());
    if(!idOnly)
    {
      data.gender=beData.getSex()!=be.belgium.eid.objects.IDData.fgMALE;
      data.familyName=betterString(beData.getName());
      data.firstName=betterString(beData.get1stFirstname());
      data.birthDate=beData.getBirthDate();
      if(data.birthDate==null && data.id!=null && data.id.length()>=6)
      {
        try
        {
          Date d=new SimpleDateFormat("yyMMdd").parse(data.id.substring(0,6)); //$NON-NLS-1$
          data.birthDate=betterDate(d);
        }
        catch(ParseException ex)
        {
          LOGGER.log(Level.WARNING,ex.getMessage(),ex);
        }
      }
      data.country="be"; //$NON-NLS-1$
      for(Locale loc:Locale.getAvailableLocales())
      {
        if(loc.getCountry().equalsIgnoreCase(data.country))
        {
          data.country=loc.getDisplayCountry();
          break;
        }
      }
      
      data.rawAddressInfo=address;
      
      IDAddress beAddress=IDAddress.parse(data.rawAddressInfo);
      data.street=betterString(beAddress.getStreet());
      data.city=betterString(beAddress.getMunicipality());
      data.zip=betterString(beAddress.getZipCode());
      
      data.rawPhotoInfo=photo;
      
      IDPhoto bePhoto=IDPhoto.parse(data.rawPhotoInfo);
      data.photo=bePhoto.getPhoto();
    }
  }
  
  private void fillData(IDData data,boolean idOnly) throws CardReaderException
  {
    if(!_eID.isConnected())
    {
      throw new CardReaderException("No ID Card found"); //$NON-NLS-1$
    }

    try
    {
      
      byte[] citizen=_eID.readFile(new byte[]
        {
          SmartCardReadable.fgDFID[0],
          SmartCardReadable.fgDFID[1],
          SmartCardReadable.fgDataTag,
          SmartCardReadable.fgDataTagID
        },be.belgium.eid.objects.IDData.fgMAX_RAW_LEN);
      
      byte[] address=null;
      byte[] photo=null;
      
      if(!idOnly)
      {

        address=_eID.readFile(new byte[]
          {
            SmartCardReadable.fgDFID[0],
            SmartCardReadable.fgDFID[1],
            SmartCardReadable.fgDataTag,
            SmartCardReadable.fgDataTagADDR
          },be.belgium.eid.objects.IDAddress.fgMAX_RAW_LEN);
        
        
        photo=_eID.readFile(new byte[]
          {
            SmartCardReadable.fgDFID[0],
            SmartCardReadable.fgDFID[1],
            SmartCardReadable.fgDataTag,
            SmartCardReadable.fgDataTagPHOTO
          },be.belgium.eid.objects.IDPhoto.fgMAX_RAW_LEN);
      }
      
      fillData(data,idOnly,citizen,address,photo);
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new CardReaderException(e.getMessage());
    }
  }
  
  @Override
  public void run()
  {
    _eID=new BeID(true);
    
    String currentTerminal=null;
    
    try
    {
      while(true)
      {
        Request req=null;
        synchronized(_requests)
        {
          if(_requests.size()==0)
            _requests.wait(100);
          if(_requests.size()>0)
            req=_requests.remove(0);
        }
        
        if(req!=null)
        {
          switch(req.command)
          {
            case FETCH:
            {
              final IDData data=new IDData();
              final CardReaderListener listener=req.listener;
              
              try
              {
                fillData(data,req.idOnly);
                
                Dispatcher.dispatch(new Runnable()
                {
                  @Override
                  public void run()
                  {
                    listener.dataRetreived(data);
                  }
                });
              }
              catch(final Throwable th)
              {
                Dispatcher.dispatch(new Runnable()
                {
                  @Override
                  public void run()
                  {
                    listener.errorOccured(th);
                  }
                });
              }
              break;
            }  
            case TERMINATE:
            {
              if(currentTerminal!=null)
              {
                try
                {
                  _eID.disconnect();
                }
                catch(CardException ex)
                {
                  ex.printStackTrace();
                }
                currentTerminal=null;
              }
              return;
            }
          }
        }
        else
        {
          try
          {
            CardTerminals terminals=TerminalFactory.getDefault().terminals();
            for(CardTerminal terminal:terminals.list())
            {
              String name=terminal.getName();
              if(terminal.isCardPresent())
              {
                if(currentTerminal==null)
                {
                  for(int i=0;i<10;i++)
                  {
                    try
                    {
                      _eID.connectCard(name);
                      IDData data=new IDData();
                      fillData(data,false);
                      currentTerminal=name;
                      fireInsertedListeners(data);
                      break;
                    }
                    catch(NoReadersFoundException ex)
                    {
                      ex.printStackTrace();
                    }
                    catch(CardReaderException ex)
                    {
                      _eID.disconnect();
                      Thread.sleep(500);
                    }
                  }
                }
              }
              else
              {
                if(name.equals(currentTerminal))
                {
                  _eID.disconnect();
                  fireRemovedListeners();
                  currentTerminal=null;
                }
              }
            }
          }
          catch(CardException ex)
          {
            if(currentTerminal!=null)
            {
              fireRemovedListeners();
              currentTerminal=null;
            }
          }
        }
      }
    }
    catch(InterruptedException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
  }

  
  @Override
  public CardReader getMonitoredReader()
  {
    return _reader;
  }

  /**
   * Fire inserted listeners.
   * @param data id data.
   */
  protected void fireInsertedListeners(IDData data)
  {
    _listeners.cardInserted(getMonitoredReader(),data);
  }
  
  /**
   * Fire removed listeners.
   */
  protected void fireRemovedListeners()
  {
    _listeners.cardRemoved(getMonitoredReader());
  }
  
  @Override
  public CardReaderMonitorListener getEventDelegate()
  {
    return _listeners;
  }
  
  /**
   * Dispose the thread.
   */
  public void dispose()
  {
    terminate();
    try
    {
      join();
    }
    catch(InterruptedException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
  }
}
