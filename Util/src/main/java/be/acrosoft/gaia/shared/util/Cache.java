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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Thread-safe cache, storing values through soft references. The key mustn't be null, but
 * the value can.<br/>
 * We can't really use {@link java.util.Optional} here since our values can be null.
 * @param <K> key type.
 */
public class Cache<K>
{
  private static class CacheItem
  {
    /**
     * Create a new CacheItem.
     * @param exp expiration.
     * @param v value.
     * @param weak use weak instead of soft reference.
     */
    public CacheItem(long exp,Object v,boolean weak)
    {
      added=System.currentTimeMillis();
      expiration=exp;
      if(weak)
        value=new WeakReference<Object>(v);
      else
        value=new SoftReference<Object>(v);
    }

    /**
     * Added time.
     */
    public long added;
    /**
     * Expiration.
     */
    public long expiration;
    /**
     * Value.
     */
    public Reference<Object> value;
  }
  
  /**
   * User-provided null values will be stored as NULL.
   */
  private HashMap<K,CacheItem> _map;
  /**
   * Whether to use soft or weak references for the values.
   */
  private boolean _weak;
  
  /**
   * Because values can be null, we need a safe way to
   * differentiate between null and "not there". This
   * value is used for null values, whereas null
   * is used for "not there" entries.
   */
  private static final Object NULL=new Object();
  
  /**
   * Special NOT_PRESENT value.
   */
  public static final Object NOT_PRESENT=new Object();
  
  /**
   * Create a new Cache using SoftReference.
   */
  public Cache()
  {
    this(false);
  }
  
  /**
   * Create a new Cache.
   * @param weak true if weak references should be used, false otherwise.
   */
  public Cache(boolean weak)
  {
    _map=new HashMap<K,CacheItem>();
    _weak=weak;
  }
    
  /**
   * Get value for the key. Must be called from the lock.
   * @param key key.
   * @return value, or null if not defined. Can be NULL, but will not be NOT_PRESENT.
   */
  private Object getInternal(K key)
  {
    CacheItem ref=_map.get(key);
    if(ref==null) return null;
    Object v=ref.value.get();
    if(v==null)
    {
      _map.remove(key);
      return null;
    }
    long exp=ref.expiration;
    if(exp!=-1 && System.currentTimeMillis()-ref.added>exp)
    {
      _map.remove(key);
      return null;
    }
    
    return v;
  }
  
  /**
   * Put the given value in cache, using the given key.
   * @param key key.
   * @param value value to put in cache. Can be null, but mustn't be equal to NOT_PRESENT.
   * @param expiration expiration, or -1 if no expiration.
   */
  public void put(K key,Object value,long expiration)
  {
    if(value==null) value=NULL;
    if(value==NOT_PRESENT) throw new IllegalArgumentException(value.toString());
    synchronized(_map)
    {
      _map.put(key,new CacheItem(expiration,value,_weak));
    }
  }
  
  /**
   * Get the cached value from the given key.
   * @param key key.
   * @return cached value, or NOT_PRESENT if not available.
   */
  public Object get(K key)
  {
    Object v;
    synchronized(_map)
    {
      v=getInternal(key);
    }
    if(v==null) v=NOT_PRESENT;
    if(v==NULL) v=null;
    return v;
  }
    
  /**
   * Clear the cache.
   */
  public void clear()
  {
    synchronized(_map)
    {
      _map.clear();
    }
  }
  
  /**
   * Discard the given cache key.
   * @param key key.
   */
  public void discard(K key)
  {
    synchronized(_map)
    {
      _map.remove(key);
    }
  }
  
  /**
   * Read all keys from the cache, or from the storage if not available. The cache will try
   * to minimize the number of read calls to the storage, and will collate all results.
   * @param keys all keys to read.
   * @param storage storage to read the values from.
   * @return all values. The length of the returned array is the same as the length of keys. Individual items
   * will be as returned from the storage.
   */
  public Object[] read(K[] keys,Storage<K> storage)
  {
    boolean someCachable=false;
    for(K k:keys)
    {
      if(storage.cachable(k))
      {
        someCachable=true;
        break;
      }
    }
    
    if(!someCachable)
      return storage.read(keys);
    
    Object[] ans=new Object[keys.length];
    HashMap<K, Object> fromCacheValues=new HashMap<K, Object>();

    //Let us only hit the storage for items that are not in the cache.
    Partition<K, Boolean> part;
    
    K[] fromStorageKeys=null;
    Object[] fromStorageValues=null;
    
    synchronized(_map)
    {
      part=Partition.array(keys).using(item->{
          Object v=getInternal(item);
          if(v==null) return Boolean.FALSE;
          fromCacheValues.put(item,v);
          return Boolean.TRUE;
      }).merge();
    }
    
    //From now on, the cache might be stale, so we won't be using it for
    //reading anymore.
    
    for(Partition<K, Boolean>.Element el : part)
    {
      Boolean cat=el.getCategory();
      K[] k=el.getItems();
      if(cat.equals(Boolean.TRUE))
      {
        Object[] v=new Object[k.length];
        for(int i=0;i<k.length;i++)
        {
          Object vv=fromCacheValues.get(k[i]);
          if(vv==NULL) vv=null;
          v[i]=vv;
        }
        el.inject(v).into(ans);
      }
      else
      {
        fromStorageKeys=k;
        fromStorageValues=storage.read(k);
        el.inject(fromStorageValues).into(ans);
      }
    }

    if(fromStorageKeys!=null)
    {
      assert fromStorageValues!=null;
      synchronized(_map)
      {
        for(int i=0;i<fromStorageKeys.length;i++)
        {
          if(storage.cachable(fromStorageKeys[i]))
            _map.put(fromStorageKeys[i],new CacheItem(storage.getExpiration(fromStorageKeys[i]),fromStorageValues[i],_weak));
        }
      }
    }

    return ans;
  }
  
  /**
   * Write to the cache and to the storage, using a write-through policy. The cache
   * is updated only if the storage write is successful.
   * @param keys keys.
   * @param values values.
   * @param storage storage.
   */
  public void write(K[] keys,Object[] values,Storage<K> storage)
  {
    for(int i=0;i<values.length;i++)
      if(values[i]==NOT_PRESENT) throw new IllegalArgumentException(values[i].toString());
    
    boolean someCachable=false;
    for(K k:keys)
    {
      if(storage.cachable(k))
      {
        someCachable=true;
        break;
      }
    }
    
    storage.write(keys,values);

    if(someCachable)
    {
      synchronized(_map)
      {
        for(int i=0;i<keys.length;i++)
        {
          if(storage.cachable(keys[i]))
            _map.put(keys[i],new CacheItem(storage.getExpiration(keys[i]),values[i]==null?NULL:values[i],_weak));
        }
      }
    }
  }
  
}
