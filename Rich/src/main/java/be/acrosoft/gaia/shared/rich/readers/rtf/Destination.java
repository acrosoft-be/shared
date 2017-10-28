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
package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * Destination.
 */
public interface Destination
{
  /**
   * Write raw string on the Destination.
   * @param s string to write.
   * @param context current context.
   * @param global global information.
   */
  public void write(String s,Context context,Global global);

  /**
   * Write one control code to the destination.
   * @param code control code.
   * @param parameter control parameter, or null if no parameter is supplied.
   * @param context current context.
   * @param global global information.
   */
  public void control(String code,String parameter,Context context,Global global);
  
  /**
   * Enter the destination.
   * @param context current context.
   * @param global global information.
   */
  public void enter(Context context,Global global);

  /**
   * Leave the destination.
   * @param context current context.
   * @param global global information.
   */
  public void leave(Context context,Global global);
  
  /**
   * Check whether the destination supports the given extended control code.
   * @param code control code.
   * @param context current context.
   * @param global global information.
   * @return true if destination supports control code, false otherwise.
   */
  public boolean supportsExtended(String code,Context context,Global global);
}
