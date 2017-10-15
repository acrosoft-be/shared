package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.ref.WeakReference;

import org.junit.Test;

/**
 * CacheTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class CacheTest
{
  @Test
  public void testExpiration() throws InterruptedException
  {
    String val="Val";
    Cache<String> cache=new Cache<String>();
    cache.put("Key",val,100);
    Thread.sleep(10);
    assertEquals(val,cache.get("Key"));
    Thread.sleep(100);
    assertEquals(Cache.NOT_PRESENT,cache.get("Key"));
  }
  
  @Test
  public void testWeak()
  {
    WeakReference<byte[]> ref=new WeakReference<>(new byte[1024*1024]);
    Cache<String> cache=new Cache<>(true);
    cache.put("Key",ref.get(),-1);
    while(ref.get()!=null) System.gc();
    assertEquals(Cache.NOT_PRESENT,cache.get("Key"));
  }
  
  @Test
  public void testPutNull()
  {
    Cache<String> cache=new Cache<>();
    cache.put("Key",null,-1);
    assertNull(cache.get("Key"));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testInvalidPut()
  {
    Cache<String> cache=new Cache<>();
    cache.put("Key",Cache.NOT_PRESENT,-1);
  }
    
  @Test
  public void testBasicIO()
  {
    Cache<String> cache=new Cache<String>();
    assertEquals(cache.get("Not there"),Cache.NOT_PRESENT);
    cache.put("One",1,-1);
    assertEquals(cache.get("One"),1);
    cache.discard("One");
    assertEquals(cache.get("One"),Cache.NOT_PRESENT);
    cache.put("One",1,-1);
    cache.clear();
    assertEquals(cache.get("One"),Cache.NOT_PRESENT);
  }
  
  private static class TestStorage implements Storage<String>
  {
    public int writeCount=0;
    public int readCount=0;
    
    @Override
    public boolean cachable(String key)
    {
      return key.startsWith("Cached");
    }

    @Override
    public long getExpiration(String key)
    {
      return cachable(key)?-1:0;
    }

    @Override
    public Object[] read(String[] key)
    {
      readCount+=key.length;
      return key;
    }

    @Override
    public void write(String[] key,Object[] value)
    {
      writeCount+=key.length;
    }
    
  }

  @Test
  public void testNonCachedStorage()
  {
    TestStorage store=new TestStorage();
    
    //Keep a strong ref to avoid GC due to soft refs in cache.
    String v1="Val1";
    String v2="Val2";

    Cache<String> cache=new Cache<String>();
    cache.write(new String[] {"One","Two"},new Object[] {v1,v2},store);
    assertArrayEquals(cache.read(new String[] {"One","Two"},store),new String[] {"One","Two"});
    assertArrayEquals(cache.read(new String[] {"One","Two"},store),new String[] {"One","Two"});
    
    assertEquals(2,store.writeCount);
    assertEquals(4,store.readCount);
  }
  
  @Test
  public void testCachedStorageWithEmptyCache()
  {
    TestStorage store=new TestStorage();
    
    Cache<String> cache=new Cache<String>();
    assertArrayEquals(cache.read(new String[] {"CachedOne","CachedTwo","CachedThree"},store),new String[] {"CachedOne","CachedTwo","CachedThree"});
    assertArrayEquals(cache.read(new String[] {"CachedOne","CachedTwo","CachedThree"},store),new String[] {"CachedOne","CachedTwo","CachedThree"});
    
    assertEquals(0,store.writeCount);
    assertEquals(3,store.readCount);
  }
  
  @Test
  public void testCachedStorageWithPreFilledCache()
  {
    TestStorage store=new TestStorage();
    
    //Keep a strong ref to avoid GC due to soft refs in cache.
    String v1="Val1";
    String v2="Val2";
    
    Cache<String> cache=new Cache<String>();
    cache.write(new String[] {"CachedOne","CachedTwo","CachedThree"},new Object[] {v1,v2,null},store);
    assertArrayEquals(cache.read(new String[] {"CachedOne","CachedTwo","CachedThree"},store),new String[] {v1,v2,null});
    assertArrayEquals(cache.read(new String[] {"CachedOne","CachedTwo","CachedThree"},store),new String[] {v1,v2,null});
    
    assertEquals(3,store.writeCount);
    assertEquals(0,store.readCount);
  }
  
  @Test
  public void testMixedStorage()
  {
    TestStorage store=new TestStorage();
    
    //Keep a strong ref to avoid GC due to soft refs in cache.
    String v1="Val1";
    String v2="Val2";
    
    Cache<String> cache=new Cache<String>();
    cache.write(new String[] {"CachedOne","Two"},new Object[] {v1,v2},store);
    assertArrayEquals(cache.read(new String[] {"CachedOne","Two"},store),new String[] {v1,"Two"});
    assertArrayEquals(cache.read(new String[] {"CachedOne","Two"},store),new String[] {v1,"Two"});
    
    assertEquals(2,store.writeCount);
    assertEquals(2,store.readCount);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testInvalidWrite()
  {
    TestStorage store=new TestStorage();
    Cache<String> cache=new Cache<String>();
    cache.write(new String[] {"Item"},new Object[] {Cache.NOT_PRESENT},store);
  }
}
