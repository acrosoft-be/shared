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
package be.acrosoft.gaia.shared.scheduler;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import be.acrosoft.gaia.shared.util.ProcessTool;

@SuppressWarnings({"javadoc","nls"})
public class AbstractTaskSchedulerTest
{
  @Test
  public void testToStringOutput()
  {
    ProcessTool.ProcessResult res=new ProcessTool.ProcessResult();
    res.output=Arrays.asList("o1","o2");
    res.error=Arrays.asList();
    res.result=17;
    
    String str=AbstractTaskScheduler.toString(res);
    assertTrue(str.contains("17"));
    assertTrue(str.contains("o1\no2"));
  }

  @Test
  public void testToStringError()
  {
    ProcessTool.ProcessResult res=new ProcessTool.ProcessResult();
    res.output=Arrays.asList();
    res.error=Arrays.asList("e1","e2");
    res.result=17;
    
    String str=AbstractTaskScheduler.toString(res);
    assertTrue(str.contains("17"));
    assertTrue(str.contains("e1\ne2"));
  }

  @Test
  public void testToStringBoth()
  {
    ProcessTool.ProcessResult res=new ProcessTool.ProcessResult();
    res.output=Arrays.asList("o1","o2");
    res.error=Arrays.asList("e1","e2");
    res.result=17;
    
    String str=AbstractTaskScheduler.toString(res);
    assertTrue(str.contains("17"));
    assertTrue(str.contains("o1\no2"));
    assertTrue(str.contains("e1\ne2"));
  }
  
}
