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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A high.low.revision.buildmod version. For instance, a version 1.14.0.1w has a high value of 1, a low value of 14, a revision value of 0, a build of 1 and a mod of "w".
 */
public class Version implements Comparable<Version>,Serializable,Cloneable
{
  private static final long serialVersionUID=1L;

  private int _high;
  private int _low;
  private int _revision;
  private int _build;
  private String _mod;
  
  private static String readFromFile(File file) throws IOException
  {
    try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.defaultCharset())))
    {
      return br.readLine();
    }
  }
  
  /**
   * Create a new Version by reading the given version file.
   * @param file file to read from.
   * @throws IOException in case of read problem.
   */
  public Version(File file) throws IOException
  {
    this(readFromFile(file));
  }
  
  /**
   * Create a new Version with an empty modification string.
   * @param high high value.
   * @param low low value.
   * @param revision revision value.
   * @param build build value.
   */
  public Version(int high,int low,int revision,int build)
  {
    this(high,low,revision,build,""); //$NON-NLS-1$
  }
  
  /**
   * Create a new Version.
   * @param high high value.
   * @param low low value.
   * @param revision revision value.
   * @param build build value.
   * @param mod modification string.
   */
  public Version(int high,int low,int revision,int build,String mod)
  {
    _high=high;
    _low=low;
    _revision=revision;
    _build=build;
    _mod=mod;
  }
  
  /**
   * Create a new version by parsing the given string.
   * @param version version string.
   */
  public Version(String version)
  {
    if(version==null)
    {
      _high=0;
      _low=0;
      _revision=0;
      _build=0;
      _mod=""; //$NON-NLS-1$
      return;
    }
    Pattern pattern=Pattern.compile("(\\d*)\\.(\\d*)\\.(\\d*)\\.(\\d*)(.*)"); //$NON-NLS-1$
    Matcher matcher=pattern.matcher(version);
    
    if(matcher.matches())
    {
      _high=Integer.parseInt(matcher.group(1));
      _low=Integer.parseInt(matcher.group(2));
      _revision=Integer.parseInt(matcher.group(3));
      _build=Integer.parseInt(matcher.group(4));
      _mod=matcher.group(5);
    }
  }
  
  /**
   * Get the high value.
   * @return high value.
   */
  public int getHigh()
  {
    return _high;
  }
  
  /**
   * Get the low value.
   * @return low value.
   */
  public int getLow()
  {
    return _low;
  }
  
  /**
   * Get the revision value.
   * @return revision value.
   */
  public int getRevision()
  {
    return _revision;
  }
  
  /**
   * Get the build value.
   * @return build value.
   */
  public int getBuild()
  {
    return _build;
  }
  
  /**
   * Get the modification string.
   * @return modification string.
   */
  public String getMod()
  {
    return _mod;
  }
  
  @Override
  public int hashCode()
  {
    return getHigh()^getLow()^getRevision()^getBuild()^getMod().hashCode();
  }
  
  @Override
  public boolean equals(Object other)
  {
    if(other==null) return false;
    if(!(other instanceof Version)) return false;
    return compareTo((Version)other)==0;
  }

  @Override
  public int compareTo(Version other)
  {
    if(getHigh()<other.getHigh()) return -1;
    if(getHigh()>other.getHigh()) return 1;

    if(getLow()<other.getLow()) return -1;
    if(getLow()>other.getLow()) return 1;

    if(getRevision()<other.getRevision()) return -1;
    if(getRevision()>other.getRevision()) return 1;

    if(getBuild()<other.getBuild()) return -1;
    if(getBuild()>other.getBuild()) return 1;
    
    return getMod().compareTo(other.getMod());
  }
  
  @Override
  public String toString()
  {
    return String.format("%d.%d.%d.%d%s",getHigh(),getLow(),getRevision(),getBuild(),getMod()); //$NON-NLS-1$
  }
  
  /**
   * Get a user readable formatting of the version. A user formatting has the form high.low.revision.
   * @return user readable string.
   */
  public String toUserString()
  {
    return String.format("%d.%d.%d",getHigh(),getLow(),getRevision()); //$NON-NLS-1$
  }
  
  @Override
  public Version clone()
  {
    try
    {
      return (Version)super.clone();
    }
    catch(CloneNotSupportedException ex)
    {
      throw new GaiaRuntimeException(ex);
    }
  }
  
}
