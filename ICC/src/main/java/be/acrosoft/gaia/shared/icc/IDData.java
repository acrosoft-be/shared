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
