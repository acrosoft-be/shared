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
import static org.junit.Assert.assertFalse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.acrosoft.gaia.shared.util.ProcessTool.PipeReader;
import be.acrosoft.gaia.shared.util.ProcessTool.ProcessResult;

@SuppressWarnings({"javadoc","nls"})
public class ProcessToolTest
{
  private String getName()
  {
    if(Platform.isWindows()) return "textgen.cmd";
    return "./textgen.sh";
  }
  
  @Before
  public void setup() throws IOException
  {
    Path p=Paths.get(getName());
    try(InputStream is=getClass().getClassLoader().getResourceAsStream("be/acrosoft/gaia/shared/util/"+getName()))
    {
      try(OutputStream os=new FileOutputStream(p.toFile()))
      {
        byte[] data=new byte[4096];
        int read=is.read(data);
        while(read>0)
        {
          os.write(data,0,read);
          read=is.read(data);
        }
      }
    }
    p.toFile().setExecutable(true);
  }
  
  @After
  public void cleanup() throws IOException
  {
    Path p=Paths.get(getName());
    Files.delete(p);
  }
  
  @Test
  public void testOutput() throws IOException
  {
    ProcessResult result=ProcessTool.execute(getName()+" 17 10000 TextTextTextTextTextTextTextTextTextText 0");
    assertEquals(17,result.result);
    assertEquals(10002,result.output.size());
    assertEquals(10000,result.error.size());
    assertEquals("TextTextTextTextTextTextTextTextTextText",result.output.get(4).trim());
    assertEquals("TextTextTextTextTextTextTextTextTextText",result.error.get(40).trim());
  }
  
  @Test
  public void testKilled() throws Exception
  {
    Process process=Runtime.getRuntime().exec(getName()+" 17 10 Text 3");
    PipeReader[] readers=ProcessTool.getReaders(process);
    Thread.sleep(1000);
    process.destroyForcibly();
    process.waitFor();
    List<String> out=readers[0].getStrings();
    List<String> err=readers[1].getStrings();
    
    assertFalse(process.exitValue()==17);
    assertEquals(11,out.size());
    assertEquals(10,err.size());
    assertEquals("before pause",out.get(10));
  }
  
}
