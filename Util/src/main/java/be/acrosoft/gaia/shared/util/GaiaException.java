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
 * GaiaException
 */
public class GaiaException extends Exception
{
  private static final long serialVersionUID=1L;

  /**
   * Create a new GaiaException
   */
  protected GaiaException()
  {
    super();
  }
  
  /**
   * Create a new GaiaException
   * @param cause exception cause.
   */
  public GaiaException(Throwable cause)
  {
    super(cause);
  }

  /**
   * Create a new GaiaException
   * @param message error message.
   */
  protected GaiaException(String message)
  {
    super(message);
  }
  
  /**
   * Create a new GaiaException
   * @param message error message.
   * @param cause exception cause.
   */
  public GaiaException(String message,Throwable cause)
  {
    super(message,cause);
  }
  
  /**
   * Get the root cause of the given exception.
   * @param th throwable.
   * @return root cause.
   */
  public static Throwable getRootCause(Throwable th)
  {
    Throwable root=th.getCause();
    if(root==null) return th;
    return getRootCause(root);
  }
}
