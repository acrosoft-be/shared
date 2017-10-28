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
