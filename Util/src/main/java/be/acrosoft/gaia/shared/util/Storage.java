package be.acrosoft.gaia.shared.util;

/**
 * Storage. Any implementation must be thread-safe.
 * @param <K> key type.
 */
public interface Storage<K>
{
  /**
   * Read the given keys.
   * @param key keys.
   * @return values.
   */
  public Object[] read(K[] key);
  /**
   * Write the given keys.
   * @param key keys.
   * @param value values.
   */
  public void write(K[] key,Object[] value);
  
  /**
   * Check whether the given key can be put in cache.
   * @param key key.
   * @return true if key can be put in cache, false otherwise.
   */
  public boolean cachable(K key);
  
  /**
   * Get cache expiration delay (in ms) for the given key.
   * @param key key.
   * @return expiration delay, or -1 if the key does not expire.
   */
  public long getExpiration(K key);
}
