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
package be.acrosoft.gaia.shared.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.junit.Test;

/**
 * LetsEncryptToolsTest
 */
@SuppressWarnings({"javadoc","nls"})
public class LetsEncryptToolsTest
{
  @Test
  public void testWithTLS() throws IOException
  {
    URL url=new URL("https://www.acrosoft.be/maven/");
    HttpURLConnection conn=(HttpURLConnection)LetsEncryptTools.openConnection(url);
    conn.connect();
    assertEquals(200,conn.getResponseCode());
    conn.disconnect();
  }
  
  @Test
  public void testWithoutTLS() throws IOException
  {
    URL url=new URL("http://www.acrosoft.be/maven/");
    HttpURLConnection conn=(HttpURLConnection)LetsEncryptTools.openConnection(url);
    conn.connect();
    assertEquals(200,conn.getResponseCode());
    conn.disconnect();
  }
  
  @Test
  public void testFallback() throws IOException
  {
    if(Platform.isWindows())
    {
      String old=LetsEncryptTools.pathToPem;
      Level previousLevel=LogManager.getLogManager().getLogger("").getLevel();
      try
      {
        LogManager.getLogManager().getLogger("").setLevel(Level.OFF);
        LetsEncryptTools.pathToPem="nowhere - this exception is EXPECTED and should not be worried about";
        URL url=new URL("https://www.acrosoft.be/maven/");
        HttpURLConnection conn=(HttpURLConnection)LetsEncryptTools.openConnection(url);
        conn.connect();
        assertEquals(200,conn.getResponseCode());
        conn.disconnect();
      }
      finally
      {
        LetsEncryptTools.pathToPem=old;
        LogManager.getLogManager().getLogger("").setLevel(previousLevel);
      }
    }
  }
}
