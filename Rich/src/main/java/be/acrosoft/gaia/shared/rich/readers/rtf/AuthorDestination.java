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
package be.acrosoft.gaia.shared.rich.readers.rtf;

/**
 * AuthorDestination.
 */
public class AuthorDestination extends AbstractDestination
{
  @Override
  public void write(String s,Context context,Global global)
  {
    global.getDocument().getMeta().setAuthor(global.getDocument().getMeta().getAuthor()+s);
  }
  
  @Override
  public void enter(Context context,Global global)
  {
    global.getDocument().getMeta().setAuthor(""); //$NON-NLS-1$
  }

}
