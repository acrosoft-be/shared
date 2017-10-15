package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import org.junit.Test;

class DevNull implements ByteChannel
{
  private int _batch;
  
  public DevNull(int batch)
  {
    _batch=batch;
  }

  @Override
  public int read(ByteBuffer dst) throws IOException
  {
    return 0;
  }

  @Override
  public void close() throws IOException
  {
  }

  @Override
  public boolean isOpen()
  {
    return true;
  }

  @Override
  public int write(ByteBuffer src) throws IOException
  {
    int total=0;
    while(src.hasRemaining() && total<_batch)
    {
      src.get();
      total++;
    }
    return total;
  }
}

class TestChannel implements ByteChannel
{
  private byte _nextRead;
  private byte _nextWrite;
  
  /**
   * Create a new TestChannel.
   */
  public TestChannel()
  {
    _nextRead=1;
    _nextWrite=1;
  }
  
  @Override
  public int read(ByteBuffer dst) throws IOException
  {
    int actualCount=0;
    int rem=dst.remaining();
    for(int i=0;i<rem;i++)
    {
      dst.put(_nextRead++);
      actualCount++;
      if(_nextRead==6)
      {
        _nextRead=1;
        break;
      }
    }
    return actualCount;
  }

  @Override
  public void close() throws IOException
  {
  }

  @Override
  public boolean isOpen()
  {
    return true;
  }

  @Override
  public int write(ByteBuffer src) throws IOException
  {
    int actualCount=0;
    int rem=src.remaining();
    for(int i=0;i<rem;i++)
    {
      byte b=src.get();
      actualCount++;
      assertEquals(b,_nextWrite);
      _nextWrite++;
      if(_nextWrite==6)
      {
        _nextWrite=1;
        break;
      }
    }
    return actualCount;
  }
}

@SuppressWarnings({"javadoc","nls"})
public class FIFOBufferTest
{
  @Test
  public void testFIFOBuffer() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(8);
    
    for(int i=0;i<1000;i++)
    {
      buffer.write(new byte[] {1});
      buffer.write(new byte[] {2});
      buffer.write(new byte[] {3,4,5});
  
      byte[] b=new byte[5];
      buffer.read(b);
      for(int z=0;z<b.length;z++)
      {
        assertEquals(z,b[z]-1);
      }
    }
    
    TestChannel ch=new TestChannel();
    for(int i=0;i<1000;i++)
    {
      buffer.write(ch);
      buffer.read(ch);
    }
    
    buffer.write(new byte[] {1,2,3,4,5,6,7,8});
    int v;
    long l;
    
    v=buffer.peekInt(0);
    assertEquals(0x01020304,v);
    v=buffer.peekInt(4);
    assertEquals(0x05060708,v);
    
    l=buffer.peekLong(0);
    assertEquals(0x0102030405060708l,l);
  }
  
  @Test
  public void testResize() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(1024*1024);
    buffer.write(new byte[1024*64]);
    buffer.read(new byte[1024]);
    assertEquals(1024*63,buffer.getSize());
    buffer.remove(buffer.getSize());
    assertEquals(0,buffer.getSize());
    
    buffer=new FIFOBuffer(1024*1024,1024);
    buffer.write(new byte[1024*1024]);
    buffer.remove(2048);
    assertEquals(1024*1022,buffer.getSize());

    buffer.write(new byte[1024]);
    assertEquals(1024*1023,buffer.getSize());
    
    buffer.read(new DevNull(1024*600));
    assertEquals(1024*423,buffer.getSize());
  }
  
  @Test
  public void testExpansionViaChannel() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(8);
    TestChannel ch=new TestChannel();
    int total=0;
    for(int i=0;i<1000;i++)
    {
      total+=buffer.write(ch);
      buffer.read(new byte[1]);
      total--;
    }
    assertEquals(7,total);
    assertEquals(total,buffer.getSize());
  }

  @Test
  public void testExpansionViaByteBuffer() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(1024*10,8);
    int total=0;
    for(int i=0;i<1000;i++)
    {
      buffer.write(ByteBuffer.wrap(new byte[] {1,2,3,4,5,6}));
      buffer.read(new byte[1]);
      total+=5;
    }
    assertEquals(total,buffer.getSize());
  }
  
  @Test
  public void testDefrag() throws Exception
  {
    FIFOBuffer buffer;
    Pair<byte[],Pair<Integer,Integer>> array;

    buffer=new FIFOBuffer(8);
    array=buffer.getArray();
    assertEquals(0,(int)array.b.a);
    assertEquals(0,(int)array.b.b);

    buffer=new FIFOBuffer(8);
    buffer.writeByte((byte)0);
    buffer.remove(1);
    array=buffer.getArray();
    assertEquals(0,(int)array.b.a);
    assertEquals(0,(int)array.b.b);
    
    buffer=new FIFOBuffer(8);
    buffer.writeByte((byte)0);
    array=buffer.getArray();
    assertEquals(0,(int)array.b.a);
    assertEquals(1,(int)array.b.b);
    assertEquals(0,array.a[0]);

    buffer=new FIFOBuffer(8);
    buffer.writeByte((byte)0);
    buffer.writeByte((byte)1);
    buffer.remove(1);
    array=buffer.getArray();
    assertEquals(1,(int)array.b.a);
    assertEquals(1,(int)array.b.b);
    assertEquals(1,array.a[1]);
    
    buffer=new FIFOBuffer(8);
    buffer.writeByte((byte)0);
    buffer.writeByte((byte)1);
    buffer.writeByte((byte)2);
    buffer.writeByte((byte)3);
    buffer.remove(1);
    buffer.writeByte((byte)4);
    buffer.writeByte((byte)5);
    buffer.writeByte((byte)6);
    buffer.writeByte((byte)7);
    buffer.remove(1);
    buffer.writeByte((byte)8);
    buffer.writeByte((byte)9);
    
    array=buffer.getArray();
    assertEquals(0,(int)array.b.a);
    assertEquals(8,(int)array.b.b);
    org.junit.Assert.assertArrayEquals(new byte[] {2,3,4,5,6,7,8,9},array.a);
  }
  
  @Test(expected=java.nio.BufferOverflowException.class)
  public void testOverFlow() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.write(new byte[10]);
  }
  
  @Test(expected=java.nio.BufferUnderflowException.class)
  public void testUnderflowReading() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.read(new byte[10]);
  }

  @Test(expected=java.nio.BufferUnderflowException.class)
  public void testUnderflowRemoving() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.remove(10);
  }
  
  @Test
  public void testClear() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.writeByte((byte)0);
    buffer.clear();
    assertEquals(0,buffer.getSize());
    assertEquals(8,buffer.getFreeSpace());
    assertTrue(buffer.toString().contains("[size = 0 capacity = 8 free space = 8]"));
  }
  
  @Test
  public void testShort()
  {
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.writeShort((short)4);
    assertEquals((short)4,buffer.peekShort(0));
    buffer.clear();
    
    buffer.write(new byte[7]);
    buffer.remove(6);
    buffer.writeShort((short)4);
    assertEquals((short)4,buffer.peekShort(1));
  }

  @Test
  public void testChar()
  {
    char v=0xaf02;
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.writeChar(v);
    assertEquals(v,buffer.peekChar(0));
    buffer.clear();
    
    buffer.write(new byte[7]);
    buffer.remove(6);
    buffer.writeChar(v);
    assertEquals(v,buffer.peekChar(1));
  }

  @Test
  public void testInt()
  {
    int v=0xaf02af02;
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.writeInt(v);
    assertEquals(v,buffer.peekInt(0));
    buffer.clear();
    
    buffer.write(new byte[7]);
    buffer.remove(6);
    buffer.writeInt(v);
    assertEquals(v,buffer.peekInt(1));
  }

  @Test
  public void testFloat()
  {
    float v=Float.intBitsToFloat(0xaf02af02);
    FIFOBuffer buffer=new FIFOBuffer(8);
    buffer.writeFloat(v);
    assertEquals(v,buffer.peekFloat(0),0);
    buffer.clear();
    
    buffer.write(new byte[7]);
    buffer.remove(6);
    buffer.writeFloat(v);
    assertEquals(v,buffer.peekFloat(1),0);
  }

  @Test
  public void testLong()
  {
    long v=0xaf02af02af02af02L;
    FIFOBuffer buffer=new FIFOBuffer(12);
    buffer.writeLong(v);
    assertEquals(v,buffer.peekLong(0));
    buffer.clear();
    
    buffer.write(new byte[7]);
    buffer.remove(6);
    buffer.writeLong(v);
    assertEquals(v,buffer.peekLong(1));
  }

  @Test
  public void testDouble()
  {
    double v=Double.longBitsToDouble(0xaf02af02af02af02L);
    FIFOBuffer buffer=new FIFOBuffer(12);
    buffer.writeDouble(v);
    assertEquals(v,buffer.peekDouble(0),0);
    buffer.clear();
    
    buffer.write(new byte[7]);
    buffer.remove(6);
    buffer.writeDouble(v);
    assertEquals(v,buffer.peekDouble(1),0);
  }
  
  @Test
  public void testResetAfterFullRead() throws Exception
  {
    FIFOBuffer buffer=new FIFOBuffer(32);
    buffer.write(new byte[30]);
    buffer.remove(10);
    buffer.write(new byte[10]);
    buffer.read(new byte[buffer.getSize()]);
    buffer.write(new byte[4]);
    assertEquals(0,(int)buffer.getArray().b.a);
    
    buffer.clear();
    buffer.write(new byte[30]);
    buffer.remove(10);
    buffer.write(new byte[10]);
    buffer.read(new DevNull(buffer.getSize()));
    buffer.write(new byte[4]);
    assertEquals(0,(int)buffer.getArray().b.a);
    
  }
}
