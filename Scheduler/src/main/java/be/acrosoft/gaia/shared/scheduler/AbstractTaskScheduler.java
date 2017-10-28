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

import java.util.List;

import be.acrosoft.gaia.shared.util.ProcessTool;

/**
 * AbstractTaskScheduler.
 */
public abstract class AbstractTaskScheduler implements TaskScheduler
{
  private TaskNameExtractor _extractor;
  
  /**
   * Create a new AbstractTaskScheduler.
   */
  public AbstractTaskScheduler()
  {
    _extractor=new NullTaskNameExtractor();
  }
  
  @Override
  public List<TaskSummary> listTasks()
  {
    return listTasks(getDefaultTaskNameExtractor());
  }
  
  @Override
  public void setDefaultTaskNameExtractor(TaskNameExtractor extractor)
  {
    _extractor=extractor;
  }
  
  @Override
  public TaskNameExtractor getDefaultTaskNameExtractor()
  {
    return _extractor;
  }
  
  private String asString(List<String> list)
  {
    StringBuilder bld=new StringBuilder();
    for(String l:list)
    {
      if(bld.length()>0)
      {
        bld.append('\n');
      }
      bld.append(l);
    }
    return bld.toString();
  }
  
  /**
   * Get a user-readable version of the given process result.
   * @param res process result.
   * @return user-readable string.
   */
  protected String toString(ProcessTool.ProcessResult res)
  {
    String o=asString(res.output);
    String e=asString(res.error);
    StringBuilder merged=new StringBuilder();
    if(o.length()>0)
    {
      merged.append(o);
    }
    if(e.length()>0)
    {
      if(merged.length()>0)
      {
        merged.append("\n"); //$NON-NLS-1$
      }
      merged.append(e);
    }
    return Messages.getString("AbstractTaskScheduler.ProcessResult",res.result,merged.toString()); //$NON-NLS-1$
    
  }
}
