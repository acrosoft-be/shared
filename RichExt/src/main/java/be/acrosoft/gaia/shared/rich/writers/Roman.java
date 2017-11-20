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
package be.acrosoft.gaia.shared.rich.writers;

/**
 * Roman.
 */
public class Roman
{
  /**
   * Get the roman number for the given integer.
   * @param v decimal value.
   * @return roman value.
   */
  @SuppressWarnings("nls")
  public static String toString(int v)
  {
    if(v==1) return "i";
    if(v==2) return "ii";
    if(v==3) return "iii";
    if(v==4) return "iv";
    if(v==5) return "v";
    if(v==6) return "vi";
    if(v==7) return "vii";
    if(v==8) return "viii";
    if(v==9) return "ix";
    if(v==10) return "x";
    if(v==11) return "xi";
    if(v==12) return "xii";
    if(v==13) return "xiii";
    if(v==14) return "xiv";
    if(v==15) return "xv";
    if(v==16) return "xvi";
    if(v==17) return "xvii";
    if(v==18) return "xviii";
    if(v==19) return "xix";
    if(v==20) return "xx";
    return "";
  }
}
