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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * HighLowVersionTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class HighLowVersionTest
{

  @Test
  public void testHighLowVersion()
  {
    HighLowVersion version=new HighLowVersion(1,2);
    
    assertTrue(HighLowVersion.NONE.compareTo(version)<0);
    assertTrue(version.compareTo(HighLowVersion.NONE)>0);
    assertTrue(version.compareTo(version)==0);
    assertTrue(version.equals(version));

    assertTrue(new HighLowVersion(1,1).compareTo(version)<0);
    assertTrue(version.compareTo(new HighLowVersion(1,1))>0);
    assertTrue(version.compareTo(new HighLowVersion(1,2))==0);
    assertTrue(version.equals(new HighLowVersion(1,2)));
    
    assertEquals(1,version.getHigh());
    assertEquals(2,version.getLow());

    assertTrue(version.compareTo(version.clone())==0);
    assertTrue(version.equals(version.clone()));
    
    assertTrue(new HighLowVersion(1,0).compareTo(new HighLowVersion(1,1))<0);
    assertTrue(new HighLowVersion(1,0).compareTo(new HighLowVersion(4,0))<0);
    assertTrue(new HighLowVersion(1,0).compareTo(new HighLowVersion(4,1))<0);
    
    assertTrue(new HighLowVersion(1,1).compareTo(new HighLowVersion(1,0))>0);
    assertTrue(new HighLowVersion(4,0).compareTo(new HighLowVersion(1,0))>0);
    assertTrue(new HighLowVersion(4,1).compareTo(new HighLowVersion(1,0))>0);
    
    assertEquals("1.2",new HighLowVersion(1,2).toString());
}

}
