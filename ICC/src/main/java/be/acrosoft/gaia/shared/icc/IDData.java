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
package be.acrosoft.gaia.shared.icc;

import java.io.Serializable;
import java.util.Date;

/**
 * IDData.
 */
public class IDData implements Serializable
{
  private static final long serialVersionUID=1L;

  /**
   * National ID.
   */
  public String id;
  /**
   * Gender (false=male, true=female).
   */
  public boolean gender;
  /**
   * First name.
   */
  public String firstName;
  /**
   * Family name.
   */
  public String familyName;
  /**
   * Address street.
   */
  public String street;
  /**
   * Address city.
   */
  public String city;
  /**
   * Address zip code.
   */
  public String zip;
  /**
   * Address country.
   */
  public String country;
  /**
   * Birth date.
   */
  public Date birthDate;
  /**
   * Photo data.
   */
  public byte[] photo;
  /**
   * Raw citizen info.
   */
  public byte[] rawCitizenInfo;
  /**
   * Raw address info.
   */
  public byte[] rawAddressInfo;
  /**
   * Raw photo info.
   */
  public byte[] rawPhotoInfo;
}
