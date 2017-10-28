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
package be.acrosoft.gaia.shared.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import be.acrosoft.gaia.shared.util.GaiaRuntimeException.RootCause;

class MyException extends GaiaRuntimeException
{
  private static final long serialVersionUID=1L;

  public MyException(String message,RootCause cause,Exception ex)
  {
    super(message,cause,ex);
  }

  public MyException(Object object)
  {
    super(object);
  }

  public MyException()
  {
    super();
  }

  public MyException(RootCause cause)
  {
    super(cause);
  }
}

class MyObject
{
  @Override
  public String toString()
  {
    throw new RuntimeException("crash"); //$NON-NLS-1$
  }
}

@SuppressWarnings({"javadoc","nls"})
public class GaiaRuntimeExceptionTest
{
  @Test
  public void testMessage()
  {
    assertEquals("MessageException",new MyException("MessageException",RootCause.EXCEPTION_OCCURED,new Exception("Argh")).getMessage());
    assertEquals("MyException: Message",new MyException("Message",RootCause.EXCEPTION_OCCURED,new Exception("Argh")).getMessage());
    assertEquals("MyException: null",new MyException(null,RootCause.EXCEPTION_OCCURED,new Exception("Argh")).getMessage());
  }
  
  @Test
  public void testCode()
  {
    assertEquals(RootCause.EXCEPTION_OCCURED,new MyException(null,RootCause.EXCEPTION_OCCURED,new Exception()).getCode());
  }

  @Test
  public void testFirstPertinentException()
  {
    IOException ex=new IOException();
    assertEquals(ex,GaiaRuntimeException.getFirstPertinentException(new MyException(null,RootCause.EXCEPTION_OCCURED,new RuntimeException(new GaiaRuntimeException(ex)))));
  }
  
  @Test
  public void testFromObject()
  {
    assertEquals("MyException: 14",new MyException(14).getMessage());
    assertEquals("MyException: null",new MyException(null).getMessage());
    assertEquals("MyException: class be.acrosoft.gaia.shared.util.MyObject",new MyException(new MyObject()).getMessage());
  }
  
  @Test
  public void testFromEmpty()
  {
    assertEquals("MyException: null",new MyException().getMessage());
    assertEquals(RootCause.SPECIALIZED_EXCEPTION,new MyException().getCode());
  }
  
  @Test
  public void testFromCode()
  {
    assertEquals("MyException: null",new MyException(RootCause.INTERNAL_ERROR).getMessage());
    assertEquals(RootCause.INTERNAL_ERROR,new MyException(RootCause.INTERNAL_ERROR).getCode());
  }
}
