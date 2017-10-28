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

/**
 * GaiaRuntimeException
 */
public class GaiaRuntimeException extends RuntimeException
{
  private static final long serialVersionUID=1L;
  
  /**
   * RootCause.
   */
  public enum RootCause
  {
    /**
     * Root cause is unexpected software error.
     */
    INTERNAL_ERROR,
    /**
     * Root cause is another exception.
     */
    EXCEPTION_OCCURED,
    /**
     * Root cause is described in the exception subclass.
     */
    SPECIALIZED_EXCEPTION
  }

  private RootCause _code;

  private static String toString(Object message)
  {
    if(message==null) return "null"; //$NON-NLS-1$
    try
    {
      return message.toString();
    }
    catch(Throwable ex)
    {
      return message.getClass().toString();
    }
  }

  /**
   * Create a new GaiaRuntimeException of type SPECIALIZED_EXCEPTION.
   * @param message message.
   */
  protected GaiaRuntimeException(Object message)
  {
    super(toString(message));
    _code=RootCause.SPECIALIZED_EXCEPTION;
  }

  /**
   * Create a new GaiaRuntimeException of type SPECIALIZED_EXCEPTION.
   */
  protected GaiaRuntimeException()
  {
    _code=RootCause.SPECIALIZED_EXCEPTION;
  }
  
  /**
   * Create a new GaiaRuntimeException
   * @param code root cause of the exception.
   */
  public GaiaRuntimeException(RootCause code)
  {
    _code=code;
  }

  /**
   * Create a new GaiaRuntimeException
   * @param code root cause of the exception.
   * @param cause linked exception.
   */
  public GaiaRuntimeException(RootCause code,Throwable cause)
  {
    super(cause);
    _code=code;
  }

  /**
   * Create a new GaiaRuntimeException of type EXCEPTION_OCCURED.
   * @param cause root cause exception.
   */
  public GaiaRuntimeException(Throwable cause)
  {
    this(RootCause.EXCEPTION_OCCURED,cause);
  }
  
  /**
   * Create a new GaiaRuntimeException.
   * @param message message.
   * @param code root cause code.
   * @param cause linked exception.
   */
  protected GaiaRuntimeException(String message,RootCause code,Throwable cause)
  {
    super(message,cause);
    _code=code;
  }
  
  @Override
  public String getMessage()
  {
    String message=super.getMessage();
    if(message!=null && message.contains("Exception")) //$NON-NLS-1$
    {
      return message;
    }

    String cname=getClass().getName();
    int pos=cname.lastIndexOf('.');
    if(pos>=0)
    {
      cname=cname.substring(pos+1);
    }
    
    return cname+": "+(message==null?"null":message); //$NON-NLS-1$ //$NON-NLS-2$
  }
  
  /**
   * Get the exception cause code.
   * @return exception cause code.
   */
  public RootCause getCode()
  {
    return _code;
  }
  
  /**
   * Get the first pertinent exception. Often, an exception is wrapped into several
   * layers of GaiaRuntimeException or other runtime exceptions. This method tries
   * to extract the first actual exception.
   * @param ex exception.
   * @return first pertinent exception.
   */
  public static Throwable getFirstPertinentException(RuntimeException ex)
  {
    Throwable cause=ex.getCause();
    if(cause==null) return ex;
    if(cause instanceof RuntimeException)
      return getFirstPertinentException((RuntimeException)cause);
    
    return cause;
  }
}
