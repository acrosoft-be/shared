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
package be.acrosoft.gaia.shared.dispatch;

import static org.junit.Assert.assertTrue;

@SuppressWarnings({"javadoc","nls"})
public class ExpectedRunnable implements Runnable
{
  private long _expectedTime;
  private Future<Void,Throwable> _map;
  
  public ExpectedRunnable(Future<Void,Throwable> map,long time)
  {
    _expectedTime=time;
    _map=map;
  }

  @Override
  public void run()
  {
    try
    {
      long current=System.currentTimeMillis();
      assertTrue(""+current+">="+_expectedTime,current>=_expectedTime);
      assertTrue(""+current+"<"+(_expectedTime+100),current<_expectedTime+100);
      _map.setVoidResult();
    }
    catch(Throwable ex)
    {
      _map.setThrowable(ex);
    }
  }
}
