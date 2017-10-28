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

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Very efficient FIFO cyclic buffer based on ByteBuffer implementation.
 */
public class FIFOBuffer
{
  private int _minimumSize;
  private int _capacity;
  private int _size;
  private int _offset;
  private ByteBuffer _buffer;
  private ByteBuffer _tmp;
  
  /**
   * Create a new FIFOBuffer.
   * @param capacity capacity.
   */
  public FIFOBuffer(int capacity)
  {
    this(capacity,1024);
  }
  
  /**
   * Create a new FIFOBuffer.
   * @param capacity capacity.
   * @param minimumSize minimum internal buffer size.
   */
  public FIFOBuffer(int capacity,int minimumSize)
  {
    _capacity=capacity;
    _size=0;
    _offset=0;
    _minimumSize=Math.min(capacity,minimumSize);
    _buffer=ByteBuffer.allocate(_minimumSize);
    _tmp=ByteBuffer.allocate(8);
  }
  
  private void resize(int newSize)
  {
    ByteBuffer newBuffer=ByteBuffer.allocate(newSize);
    
    int last=_offset+_size;
    if(last<=_buffer.capacity())
    {
      newBuffer.put(_buffer.array(),_offset,_size);
    }
    else
    {
      newBuffer.put(_buffer.array(),_offset,_buffer.capacity()-_offset);
      newBuffer.put(_buffer.array(),0,_size+_offset-_buffer.capacity());
    }
        
    _buffer=newBuffer;
    _offset=0;
  }
  
  private void expand(int minimumCapacity)
  {
    if(minimumCapacity>_capacity) throw new BufferOverflowException();
    if(minimumCapacity<_minimumSize)
      minimumCapacity=_minimumSize;
    int newSize=_buffer.capacity();
    while(newSize<minimumCapacity)
      newSize<<=1;
    if(newSize!=_buffer.capacity())
      resize(newSize);
  }
  
  private void shrink(int minimumCapacity)
  {
    if(minimumCapacity<_minimumSize)
      minimumCapacity=_minimumSize;
    int newSize=_buffer.capacity();
    while(newSize>=minimumCapacity)
      newSize>>=1;
    newSize<<=1;
    if(newSize!=_buffer.capacity())
      resize(newSize);
  }
  
  private void adaptSize(int minimumCapacity)
  {
    if(_buffer.capacity()>minimumCapacity*2)
      shrink(minimumCapacity);
    else if(_buffer.capacity()<minimumCapacity)
      expand(minimumCapacity);
  }
  
  private void defrag()
  {
    if(_offset==0||_size==0)
    {
      _offset=0;
      return;
    }
    
    int last=_offset+_size;
    if(last>_buffer.capacity())
      resize(_buffer.capacity());
  }
   
  /**
   * Write at most getFreeSpace() bytes in the buffer, from the given readable
   * channel.
   * @param channel channel to read from.
   * @return number of actually written bytes.
   * @throws IOException in case of channel error.
   */
  public int write(ReadableByteChannel channel) throws IOException
  {
    if(_buffer.capacity()<_size*3/2)
      adaptSize(Math.min(_buffer.capacity()*2,_capacity));
    
    int first=(_offset+_size)%_buffer.capacity();
    int last=(first+_buffer.capacity()-_size)%_buffer.capacity();
    
    if(first<last)
    {
      _buffer.limit(last);
      _buffer.position(first);
      int read=channel.read(_buffer);
      _size+=read;
      return read;
    }
    
    _buffer.limit(_buffer.capacity());
    _buffer.position(first);
    int read=channel.read(_buffer);
    _size+=read;
    
    if(_buffer.position()!=_buffer.capacity())
      return read;
    
    _buffer.limit(last);
    _buffer.position(0);
    int read2=channel.read(_buffer);
    read+=read2;
    _size+=read2;
    return read;
    
  }

  /**
   * Write buffer.remaining() bytes into this FIFOBuffer.
   * @param buffer buffer to write.
   */
  public void write(ByteBuffer buffer)
  {
    int size=buffer.remaining();
    adaptSize(_size+size);
    
    int first=(_offset+_size)%_buffer.capacity();
    int last=(first+size)%_buffer.capacity();
    int read=size;
    
    if(first<last)
    {
      _buffer.limit(last);
      _buffer.position(first);
      _buffer.put(buffer);
      _size+=read;
      return;
    }
    
    _buffer.limit(_buffer.capacity());
    _buffer.position(first);
    int size1=_buffer.capacity()-first;
    
    buffer.limit(buffer.position()+size1);
    
    _buffer.put(buffer);
    _buffer.limit(last);
    _buffer.position(0);
    buffer.limit(buffer.position()+size-size1);
    _buffer.put(buffer);
    
    _size+=read;    
  }
  
  /**
   * Write the given array in the buffer. If size is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param array array.
   * @param offset first element of the array to write.
   * @param size number of elements to write.
   */
  public void write(byte[] array,int offset,int size)
  {
    adaptSize(_size+size);
    
    int first=(_offset+_size)%_buffer.capacity();
    int last=(first+size)%_buffer.capacity();
    int read=size;
    
    if(first<last)
    {
      _buffer.limit(last);
      _buffer.position(first);
      _buffer.put(array,offset,size);
      _size+=read;
      return;
    }
    
    _buffer.limit(_buffer.capacity());
    _buffer.position(first);
    int size1=_buffer.capacity()-first;
    _buffer.put(array,offset,size1);
    _buffer.limit(last);
    _buffer.position(0);
    int size2=size-size1;
    _buffer.put(array,offset+size1,size2);
    
    _size+=read;    
  }
  
  /**
   * Write the given array in the buffer. If size is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param array array.
   */
  public void write(byte[] array)
  {
    write(array,0,array.length);
  }
  
  /**
   * Write the given byte in the buffer. If 1 is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param aByte a byte.
   */
  public void writeByte(byte aByte)
  {
    _tmp.clear();
    _tmp.put(aByte);
    _tmp.flip(); 
    write(_tmp.array(), 0, 1);
  }  
  /**
   * Write the given char in the buffer. If size of char (2 byte) is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param aChar a char.
   */
  public void writeChar(char aChar)
  {
    _tmp.clear();
    _tmp.putChar(aChar);
    _tmp.flip(); 
    write(_tmp.array(), 0, 2);
  }
  /**
   * Write the given integer in the buffer. If size of int (8 bytes) is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param aDouble a double.
   */
  public void writeDouble(double aDouble)
  {
	_tmp.clear();
    _tmp.putDouble(aDouble);
    _tmp.flip(); 
    write(_tmp.array(), 0, 8);
  }
  /**
   * Write the given float in the buffer. If size of int (4 bytes) is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param aFloat a float.
   */
  public void writeFloat(float aFloat)
  {
	_tmp.clear();
    _tmp.putFloat(aFloat);
    _tmp.flip(); 
    write(_tmp.array(), 0, 4);
  }
  /**
   * Write the given integer in the buffer. If size of int (4 bytes) is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param anInt an int.
   */
  public void writeInt(int anInt)
  {
	_tmp.clear();
    _tmp.putInt(anInt);
    _tmp.flip();
    write(_tmp.array(), 0, 4);
  }
  /**
   * Write the given long in the buffer. If size of int (8 bytes) is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param aLong a long.
   */
  public void writeLong(long aLong)
  {
	_tmp.clear();
    _tmp.putLong(aLong);
    _tmp.flip(); 
    write(_tmp.array(), 0, 8);
  }
  /**
   * Write the given long in the buffer. If size of int (2 bytes) is greater than getFreeSpace(),
   * a BufferOverflowException is thrown and the buffer is left unchanged.
   * @param aShort a short.
   */
  public void writeShort(short aShort)
  {
	_tmp.clear();
    _tmp.putShort(aShort);
    _tmp.flip(); 
    write(_tmp.array(), 0, 2);
  }
  /**
   * Read from the buffer and write to the channel at most getSize() bytes.
   * @param channel channel to write to.
   * @return number of bytes actually read from the buffer and written to
   * the channel.
   * @throws IOException in case of channel error.
   */
  public int read(WritableByteChannel channel) throws IOException
  {
    if(getSize()==0) return 0;
    int first=_offset;
    int last=(_offset+_size)%_buffer.capacity();
    if(first<last)
    {
      _buffer.limit(last);
      _buffer.position(first);
      int written=channel.write(_buffer);
      _size-=written;
      _offset=(_offset+written)%_buffer.capacity();
      if(_size==0)
        _offset=0;
      adaptSize(_size);
      return written;
    }
    
    _buffer.limit(_buffer.capacity());
    _buffer.position(first);
    int written=channel.write(_buffer);
    _size-=written;
    _offset=(_offset+written)%_buffer.capacity();
    if(_size==0)
      _offset=0;
    
    if(_buffer.position()!=_buffer.capacity())
    {
      adaptSize(_size);
      return written;
    }
    
    _buffer.limit(last);
    _buffer.position(0);
    int written1=channel.write(_buffer);
    _size-=written1;
    _offset=(_offset+written1)%_buffer.capacity();
    if(_size==0)
      _offset=0;
    written+=written1;
    adaptSize(_size);
    return written;
  }
    
  /**
   * Read from the buffer to the given array the given amount of bytes. If
   * size is greater than getSize(), a BufferUnderflowException is thrown
   * and the buffer is left unchanged.
   * @param array array to write bytes into.
   * @param offset first byte of the array to be modified.
   * @param size number of bytes to be modified.
   */
  public void read(byte[] array,int offset,int size)
  {
    if(size>getSize()) throw new BufferUnderflowException();
    int first=_offset;
    int last=(_offset+size)%_buffer.capacity();
    int written=size;
    if(first<last)
    {
      _buffer.limit(last);
      _buffer.position(first);
      _buffer.get(array,offset,size);
      _size-=written;
      _offset=(_offset+written)%_buffer.capacity();
      if(_size==0)
        _offset=0;
      adaptSize(_size);
      return;
    }
    
    _buffer.limit(_buffer.capacity());
    _buffer.position(first);
    int size1=_buffer.capacity()-first;
    
    _buffer.get(array,offset,size1);   
    _buffer.limit(last);
    _buffer.position(0);
    int size2=size-size1;
    _buffer.get(array,offset+size1,size2);
    _size-=written;
    _offset=(_offset+written)%_buffer.capacity();
    if(_size==0)
      _offset=0;
    adaptSize(_size);
  }
  
  /**
   * Read from the buffer to the given array the given amount of bytes. If
   * size is greater than getSize(), a BufferUnderflowException is thrown
   * and the buffer is left unchanged.
   * @param array array to write bytes into.
   */
  public void read(byte[] array)
  {
    read(array,0,array.length);
  }
  
  /**
   * Get the current buffer size.
   * @return number of bytes that can be read from the buffer.
   */
  public int getSize()
  {
    return _size;
  }
  
  /**
   * Get the buffer capacity.
   * @return maximum buffer capacity.
   */
  public int getCapacity()
  {
    return _capacity;
  }
  
  /**
   * Get the current free space.
   * @return number of bytes that can be written to the buffer.
   */
  public int getFreeSpace()
  {
    return getCapacity()-getSize();
  }
  
  /**
   * Peek a byte at the given offset.
   * @param offset offset, must be non-negative and lower than getSize().
   * @return byte at given offset.
   */
  public byte peekByte(int offset)
  {
    _buffer.limit(_buffer.capacity());
    _buffer.position(0);
    offset=(_offset+offset)%_buffer.capacity();
    return _buffer.get(offset);
  }
  
  /**
   * Peek a short at the given offset.
   * @param offset offset, must be non-negative and lower than getSize()-1.
   * @return short at given offset.
   */
  public short peekShort(int offset)
  {
    _buffer.limit(_buffer.capacity());
    _buffer.position(0);
    int first=(_offset+offset)%_buffer.capacity();
    int last=(first+1)%_buffer.capacity();
    if(first<last) return _buffer.getShort(first);
    
    _tmp.clear();
    _tmp.put(peekByte(offset++));
    _tmp.put(peekByte(offset++));
    _tmp.rewind();
    return _tmp.getShort();
  }
  
  /**
   * Peek a char at the given offset.
   * @param offset offset, must be non-negative and lower than getSize()-1.
   * @return char at given offset.
   */
  public char peekChar(int offset)
  {
    _buffer.limit(_buffer.capacity());
    _buffer.position(0);
    int first=(_offset+offset)%_buffer.capacity();
    int last=(first+1)%_buffer.capacity();
    if(first<last) return _buffer.getChar(first);
    
    _tmp.clear();
    _tmp.put(peekByte(offset++));
    _tmp.put(peekByte(offset++));
    _tmp.rewind();
    return _tmp.getChar();
  }
  
  /**
   * Peek an int at the given offset.
   * @param offset offset, must be non-negative and lower than getSize()-3.
   * @return int at given offset.
   */
  public int peekInt(int offset)
  {
    _buffer.limit(_buffer.capacity());
    _buffer.position(0);
    int first=(_offset+offset)%_buffer.capacity();
    int last=(first+3)%_buffer.capacity();
    if(first<last){
    	return _buffer.getInt(first);
    }
    
    _tmp.clear();
    for(int i=0;i<4;i++){
      _tmp.put(peekByte(offset++));
    }
    _tmp.rewind();
    return _tmp.getInt();
  }
  
  /**
   * Peek a float at the given offset.
   * @param offset offset, must be non-negative and lower than getSize()-3.
   * @return float at given offset.
   */
  public float peekFloat(int offset)
  {
    _buffer.limit(_buffer.capacity());
    _buffer.position(0);
    int first=(_offset+offset)%_buffer.capacity();
    int last=(first+3)%_buffer.capacity();
    if(first<last) return _buffer.getFloat(first);
    
    _tmp.clear();
    for(int i=0;i<4;i++)
      _tmp.put(peekByte(offset++));
    _tmp.rewind();
    return _tmp.getFloat();
  }
  
  /**
   * Peek a long at the given offset.
   * @param offset offset, must be non-negative and lower than getSize()-7.
   * @return long at given offset.
   */
  public long peekLong(int offset)
  {
    _buffer.limit(_buffer.capacity());
    _buffer.position(0);
    int first=(_offset+offset)%_buffer.capacity();
    int last=(first+7)%_buffer.capacity();
    if(first<last) return _buffer.getLong(first);
    
    _tmp.clear();
    for(int i=0;i<8;i++)
      _tmp.put(peekByte(offset++));
    _tmp.rewind();
    return _tmp.getLong();
  }
  
  /**
   * Peek a double at the given offset.
   * @param offset offset, must be non-negative and lower than getSize()-7.
   * @return double at given offset.
   */
  public double peekDouble(int offset)
  {
    _buffer.limit(_buffer.capacity());
    _buffer.position(0);
    int first=(_offset+offset)%_buffer.capacity();
    int last=(first+7)%_buffer.capacity();
    if(first<last) return _buffer.getDouble(first);
    
    _tmp.clear();
    for(int i=0;i<8;i++)
      _tmp.put(peekByte(offset++));
    _tmp.rewind();
    return _tmp.getDouble();
  }
  
  /**
   * Clear the buffer.
   */
  public void clear()
  {
    _size=0;
    _offset=0;
  }
  
  /**
   * Get a continuous representation of the buffer. This method will first defrag
   * the buffer if needed. The returned byte array is the internal byte buffer,
   * therefore any modification done on this array will be reflected into the buffer.
   * The array returned by this method remains valid as long as no read or clear
   * operation is performed on the buffer. 
   * @return continuous byte array, the first index to read, and the array size,
   * in that order.
   */
  public Pair<byte[],Pair<Integer,Integer>> getArray()
  {
    defrag();
    return new Pair<byte[],Pair<Integer,Integer>>(_buffer.array(),new Pair<Integer,Integer>(_offset,_size));
  }
  
  /**
   * Remove the count first bytes of the buffer.
   * @param count number of bytes to remove. It must not be greater than getSize().
   */
  public void remove(int count)
  {
    if(count>getSize()) throw new BufferUnderflowException();
    _size-=count;
    _offset=(_offset+count)%_buffer.capacity();
    if(_size==0)
      _offset=0;
  }
  
  @SuppressWarnings("nls")
  @Override
  public String toString()
  {
    String ans=super.toString();
    return ans+" [size = "+getSize()+" capacity = "+getCapacity()+" free space = "+getFreeSpace()+"]";
  }

}
