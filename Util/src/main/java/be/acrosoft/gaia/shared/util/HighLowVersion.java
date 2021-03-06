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

/**
 * Version.
 */
public class HighLowVersion implements Comparable<HighLowVersion>,Cloneable
{
  /**
   * NONE version.
   */
  public static HighLowVersion NONE=new HighLowVersion(0,0);

  private Version _version;
  
  /**
   * Create a new Version.
   * @param high high part.
   * @param low low part.
   */
  public HighLowVersion(int high,int low)
  {
    _version=new Version(high,low,0,0);
  }
  
  /**
   * Get the high part of the version.
   * @return high part.
   */
  public int getHigh()
  {
    return _version.getHigh();
  }
  
  /**
   * Get the low part of the version.
   * @return low part.
   */
  public int getLow()
  {
    return _version.getLow();
  }
  
  @Override
  public int hashCode()
  {
    return _version.hashCode();
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if(obj==null) return false;
    if(!(obj instanceof HighLowVersion)) return false;
    return _version.equals(((HighLowVersion)obj)._version);
  }
  
  @Override
  public String toString()
  {
    return getHigh()+"."+getLow(); //$NON-NLS-1$
  }

  @Override
  public int compareTo(HighLowVersion v)
  {
    return _version.compareTo(v._version);
  }
  @Override
  public HighLowVersion clone()
  {
    try
    {
      HighLowVersion ans=(HighLowVersion)super.clone();
      ans._version=ans._version.clone();
      return ans;
    }
    catch(CloneNotSupportedException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
  }
}
