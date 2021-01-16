package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import io.netty.util.Signal;
import io.netty.util.internal.StringUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class ReplayingDecoderByteBuf extends ByteBuf {
   private static final Signal REPLAY;
   private ByteBuf buffer;
   private boolean terminated;
   private SwappedByteBuf swapped;
   static final ReplayingDecoderByteBuf EMPTY_BUFFER;

   ReplayingDecoderByteBuf() {
      super();
   }

   ReplayingDecoderByteBuf(ByteBuf var1) {
      super();
      this.setCumulation(var1);
   }

   void setCumulation(ByteBuf var1) {
      this.buffer = var1;
   }

   void terminate() {
      this.terminated = true;
   }

   public int capacity() {
      return this.terminated ? this.buffer.capacity() : 2147483647;
   }

   public ByteBuf capacity(int var1) {
      throw reject();
   }

   public int maxCapacity() {
      return this.capacity();
   }

   public ByteBufAllocator alloc() {
      return this.buffer.alloc();
   }

   public boolean isReadOnly() {
      return false;
   }

   public ByteBuf asReadOnly() {
      return Unpooled.unmodifiableBuffer((ByteBuf)this);
   }

   public boolean isDirect() {
      return this.buffer.isDirect();
   }

   public boolean hasArray() {
      return false;
   }

   public byte[] array() {
      throw new UnsupportedOperationException();
   }

   public int arrayOffset() {
      throw new UnsupportedOperationException();
   }

   public boolean hasMemoryAddress() {
      return false;
   }

   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   public ByteBuf clear() {
      throw reject();
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int compareTo(ByteBuf var1) {
      throw reject();
   }

   public ByteBuf copy() {
      throw reject();
   }

   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.buffer.copy(var1, var2);
   }

   public ByteBuf discardReadBytes() {
      throw reject();
   }

   public ByteBuf ensureWritable(int var1) {
      throw reject();
   }

   public int ensureWritable(int var1, boolean var2) {
      throw reject();
   }

   public ByteBuf duplicate() {
      throw reject();
   }

   public ByteBuf retainedDuplicate() {
      throw reject();
   }

   public boolean getBoolean(int var1) {
      this.checkIndex(var1, 1);
      return this.buffer.getBoolean(var1);
   }

   public byte getByte(int var1) {
      this.checkIndex(var1, 1);
      return this.buffer.getByte(var1);
   }

   public short getUnsignedByte(int var1) {
      this.checkIndex(var1, 1);
      return this.buffer.getUnsignedByte(var1);
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkIndex(var1, var4);
      this.buffer.getBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, byte[] var2) {
      this.checkIndex(var1, var2.length);
      this.buffer.getBytes(var1, var2);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      throw reject();
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkIndex(var1, var4);
      this.buffer.getBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      throw reject();
   }

   public ByteBuf getBytes(int var1, ByteBuf var2) {
      throw reject();
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) {
      throw reject();
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) {
      throw reject();
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) {
      throw reject();
   }

   public int getInt(int var1) {
      this.checkIndex(var1, 4);
      return this.buffer.getInt(var1);
   }

   public int getIntLE(int var1) {
      this.checkIndex(var1, 4);
      return this.buffer.getIntLE(var1);
   }

   public long getUnsignedInt(int var1) {
      this.checkIndex(var1, 4);
      return this.buffer.getUnsignedInt(var1);
   }

   public long getUnsignedIntLE(int var1) {
      this.checkIndex(var1, 4);
      return this.buffer.getUnsignedIntLE(var1);
   }

   public long getLong(int var1) {
      this.checkIndex(var1, 8);
      return this.buffer.getLong(var1);
   }

   public long getLongLE(int var1) {
      this.checkIndex(var1, 8);
      return this.buffer.getLongLE(var1);
   }

   public int getMedium(int var1) {
      this.checkIndex(var1, 3);
      return this.buffer.getMedium(var1);
   }

   public int getMediumLE(int var1) {
      this.checkIndex(var1, 3);
      return this.buffer.getMediumLE(var1);
   }

   public int getUnsignedMedium(int var1) {
      this.checkIndex(var1, 3);
      return this.buffer.getUnsignedMedium(var1);
   }

   public int getUnsignedMediumLE(int var1) {
      this.checkIndex(var1, 3);
      return this.buffer.getUnsignedMediumLE(var1);
   }

   public short getShort(int var1) {
      this.checkIndex(var1, 2);
      return this.buffer.getShort(var1);
   }

   public short getShortLE(int var1) {
      this.checkIndex(var1, 2);
      return this.buffer.getShortLE(var1);
   }

   public int getUnsignedShort(int var1) {
      this.checkIndex(var1, 2);
      return this.buffer.getUnsignedShort(var1);
   }

   public int getUnsignedShortLE(int var1) {
      this.checkIndex(var1, 2);
      return this.buffer.getUnsignedShortLE(var1);
   }

   public char getChar(int var1) {
      this.checkIndex(var1, 2);
      return this.buffer.getChar(var1);
   }

   public float getFloat(int var1) {
      this.checkIndex(var1, 4);
      return this.buffer.getFloat(var1);
   }

   public double getDouble(int var1) {
      this.checkIndex(var1, 8);
      return this.buffer.getDouble(var1);
   }

   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      this.checkIndex(var1, var2);
      return this.buffer.getCharSequence(var1, var2, var3);
   }

   public int hashCode() {
      throw reject();
   }

   public int indexOf(int var1, int var2, byte var3) {
      if (var1 == var2) {
         return -1;
      } else if (Math.max(var1, var2) > this.buffer.writerIndex()) {
         throw REPLAY;
      } else {
         return this.buffer.indexOf(var1, var2, var3);
      }
   }

   public int bytesBefore(byte var1) {
      int var2 = this.buffer.bytesBefore(var1);
      if (var2 < 0) {
         throw REPLAY;
      } else {
         return var2;
      }
   }

   public int bytesBefore(int var1, byte var2) {
      return this.bytesBefore(this.buffer.readerIndex(), var1, var2);
   }

   public int bytesBefore(int var1, int var2, byte var3) {
      int var4 = this.buffer.writerIndex();
      if (var1 >= var4) {
         throw REPLAY;
      } else if (var1 <= var4 - var2) {
         return this.buffer.bytesBefore(var1, var2, var3);
      } else {
         int var5 = this.buffer.bytesBefore(var1, var4 - var1, var3);
         if (var5 < 0) {
            throw REPLAY;
         } else {
            return var5;
         }
      }
   }

   public int forEachByte(ByteProcessor var1) {
      int var2 = this.buffer.forEachByte(var1);
      if (var2 < 0) {
         throw REPLAY;
      } else {
         return var2;
      }
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      int var4 = this.buffer.writerIndex();
      if (var1 >= var4) {
         throw REPLAY;
      } else if (var1 <= var4 - var2) {
         return this.buffer.forEachByte(var1, var2, var3);
      } else {
         int var5 = this.buffer.forEachByte(var1, var4 - var1, var3);
         if (var5 < 0) {
            throw REPLAY;
         } else {
            return var5;
         }
      }
   }

   public int forEachByteDesc(ByteProcessor var1) {
      if (this.terminated) {
         return this.buffer.forEachByteDesc(var1);
      } else {
         throw reject();
      }
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      if (var1 + var2 > this.buffer.writerIndex()) {
         throw REPLAY;
      } else {
         return this.buffer.forEachByteDesc(var1, var2, var3);
      }
   }

   public ByteBuf markReaderIndex() {
      this.buffer.markReaderIndex();
      return this;
   }

   public ByteBuf markWriterIndex() {
      throw reject();
   }

   public ByteOrder order() {
      return this.buffer.order();
   }

   public ByteBuf order(ByteOrder var1) {
      if (var1 == null) {
         throw new NullPointerException("endianness");
      } else if (var1 == this.order()) {
         return this;
      } else {
         SwappedByteBuf var2 = this.swapped;
         if (var2 == null) {
            this.swapped = var2 = new SwappedByteBuf(this);
         }

         return var2;
      }
   }

   public boolean isReadable() {
      return this.terminated ? this.buffer.isReadable() : true;
   }

   public boolean isReadable(int var1) {
      return this.terminated ? this.buffer.isReadable(var1) : true;
   }

   public int readableBytes() {
      return this.terminated ? this.buffer.readableBytes() : 2147483647 - this.buffer.readerIndex();
   }

   public boolean readBoolean() {
      this.checkReadableBytes(1);
      return this.buffer.readBoolean();
   }

   public byte readByte() {
      this.checkReadableBytes(1);
      return this.buffer.readByte();
   }

   public short readUnsignedByte() {
      this.checkReadableBytes(1);
      return this.buffer.readUnsignedByte();
   }

   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.checkReadableBytes(var3);
      this.buffer.readBytes(var1, var2, var3);
      return this;
   }

   public ByteBuf readBytes(byte[] var1) {
      this.checkReadableBytes(var1.length);
      this.buffer.readBytes(var1);
      return this;
   }

   public ByteBuf readBytes(ByteBuffer var1) {
      throw reject();
   }

   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      this.checkReadableBytes(var3);
      this.buffer.readBytes(var1, var2, var3);
      return this;
   }

   public ByteBuf readBytes(ByteBuf var1, int var2) {
      throw reject();
   }

   public ByteBuf readBytes(ByteBuf var1) {
      this.checkReadableBytes(var1.writableBytes());
      this.buffer.readBytes(var1);
      return this;
   }

   public int readBytes(GatheringByteChannel var1, int var2) {
      throw reject();
   }

   public int readBytes(FileChannel var1, long var2, int var4) {
      throw reject();
   }

   public ByteBuf readBytes(int var1) {
      this.checkReadableBytes(var1);
      return this.buffer.readBytes(var1);
   }

   public ByteBuf readSlice(int var1) {
      this.checkReadableBytes(var1);
      return this.buffer.readSlice(var1);
   }

   public ByteBuf readRetainedSlice(int var1) {
      this.checkReadableBytes(var1);
      return this.buffer.readRetainedSlice(var1);
   }

   public ByteBuf readBytes(OutputStream var1, int var2) {
      throw reject();
   }

   public int readerIndex() {
      return this.buffer.readerIndex();
   }

   public ByteBuf readerIndex(int var1) {
      this.buffer.readerIndex(var1);
      return this;
   }

   public int readInt() {
      this.checkReadableBytes(4);
      return this.buffer.readInt();
   }

   public int readIntLE() {
      this.checkReadableBytes(4);
      return this.buffer.readIntLE();
   }

   public long readUnsignedInt() {
      this.checkReadableBytes(4);
      return this.buffer.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      this.checkReadableBytes(4);
      return this.buffer.readUnsignedIntLE();
   }

   public long readLong() {
      this.checkReadableBytes(8);
      return this.buffer.readLong();
   }

   public long readLongLE() {
      this.checkReadableBytes(8);
      return this.buffer.readLongLE();
   }

   public int readMedium() {
      this.checkReadableBytes(3);
      return this.buffer.readMedium();
   }

   public int readMediumLE() {
      this.checkReadableBytes(3);
      return this.buffer.readMediumLE();
   }

   public int readUnsignedMedium() {
      this.checkReadableBytes(3);
      return this.buffer.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      this.checkReadableBytes(3);
      return this.buffer.readUnsignedMediumLE();
   }

   public short readShort() {
      this.checkReadableBytes(2);
      return this.buffer.readShort();
   }

   public short readShortLE() {
      this.checkReadableBytes(2);
      return this.buffer.readShortLE();
   }

   public int readUnsignedShort() {
      this.checkReadableBytes(2);
      return this.buffer.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      this.checkReadableBytes(2);
      return this.buffer.readUnsignedShortLE();
   }

   public char readChar() {
      this.checkReadableBytes(2);
      return this.buffer.readChar();
   }

   public float readFloat() {
      this.checkReadableBytes(4);
      return this.buffer.readFloat();
   }

   public double readDouble() {
      this.checkReadableBytes(8);
      return this.buffer.readDouble();
   }

   public CharSequence readCharSequence(int var1, Charset var2) {
      this.checkReadableBytes(var1);
      return this.buffer.readCharSequence(var1, var2);
   }

   public ByteBuf resetReaderIndex() {
      this.buffer.resetReaderIndex();
      return this;
   }

   public ByteBuf resetWriterIndex() {
      throw reject();
   }

   public ByteBuf setBoolean(int var1, boolean var2) {
      throw reject();
   }

   public ByteBuf setByte(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      throw reject();
   }

   public ByteBuf setBytes(int var1, byte[] var2) {
      throw reject();
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      throw reject();
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      throw reject();
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      throw reject();
   }

   public ByteBuf setBytes(int var1, ByteBuf var2) {
      throw reject();
   }

   public int setBytes(int var1, InputStream var2, int var3) {
      throw reject();
   }

   public ByteBuf setZero(int var1, int var2) {
      throw reject();
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) {
      throw reject();
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) {
      throw reject();
   }

   public ByteBuf setIndex(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setInt(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setIntLE(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setLong(int var1, long var2) {
      throw reject();
   }

   public ByteBuf setLongLE(int var1, long var2) {
      throw reject();
   }

   public ByteBuf setMedium(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setShort(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setShortLE(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setChar(int var1, int var2) {
      throw reject();
   }

   public ByteBuf setFloat(int var1, float var2) {
      throw reject();
   }

   public ByteBuf setDouble(int var1, double var2) {
      throw reject();
   }

   public ByteBuf skipBytes(int var1) {
      this.checkReadableBytes(var1);
      this.buffer.skipBytes(var1);
      return this;
   }

   public ByteBuf slice() {
      throw reject();
   }

   public ByteBuf retainedSlice() {
      throw reject();
   }

   public ByteBuf slice(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.buffer.slice(var1, var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.buffer.slice(var1, var2);
   }

   public int nioBufferCount() {
      return this.buffer.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      throw reject();
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.buffer.nioBuffer(var1, var2);
   }

   public ByteBuffer[] nioBuffers() {
      throw reject();
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.buffer.nioBuffers(var1, var2);
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.buffer.internalNioBuffer(var1, var2);
   }

   public String toString(int var1, int var2, Charset var3) {
      this.checkIndex(var1, var2);
      return this.buffer.toString(var1, var2, var3);
   }

   public String toString(Charset var1) {
      throw reject();
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '(' + "ridx=" + this.readerIndex() + ", widx=" + this.writerIndex() + ')';
   }

   public boolean isWritable() {
      return false;
   }

   public boolean isWritable(int var1) {
      return false;
   }

   public int writableBytes() {
      return 0;
   }

   public int maxWritableBytes() {
      return 0;
   }

   public ByteBuf writeBoolean(boolean var1) {
      throw reject();
   }

   public ByteBuf writeByte(int var1) {
      throw reject();
   }

   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      throw reject();
   }

   public ByteBuf writeBytes(byte[] var1) {
      throw reject();
   }

   public ByteBuf writeBytes(ByteBuffer var1) {
      throw reject();
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      throw reject();
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      throw reject();
   }

   public ByteBuf writeBytes(ByteBuf var1) {
      throw reject();
   }

   public int writeBytes(InputStream var1, int var2) {
      throw reject();
   }

   public int writeBytes(ScatteringByteChannel var1, int var2) {
      throw reject();
   }

   public int writeBytes(FileChannel var1, long var2, int var4) {
      throw reject();
   }

   public ByteBuf writeInt(int var1) {
      throw reject();
   }

   public ByteBuf writeIntLE(int var1) {
      throw reject();
   }

   public ByteBuf writeLong(long var1) {
      throw reject();
   }

   public ByteBuf writeLongLE(long var1) {
      throw reject();
   }

   public ByteBuf writeMedium(int var1) {
      throw reject();
   }

   public ByteBuf writeMediumLE(int var1) {
      throw reject();
   }

   public ByteBuf writeZero(int var1) {
      throw reject();
   }

   public int writerIndex() {
      return this.buffer.writerIndex();
   }

   public ByteBuf writerIndex(int var1) {
      throw reject();
   }

   public ByteBuf writeShort(int var1) {
      throw reject();
   }

   public ByteBuf writeShortLE(int var1) {
      throw reject();
   }

   public ByteBuf writeChar(int var1) {
      throw reject();
   }

   public ByteBuf writeFloat(float var1) {
      throw reject();
   }

   public ByteBuf writeDouble(double var1) {
      throw reject();
   }

   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      throw reject();
   }

   public int writeCharSequence(CharSequence var1, Charset var2) {
      throw reject();
   }

   private void checkIndex(int var1, int var2) {
      if (var1 + var2 > this.buffer.writerIndex()) {
         throw REPLAY;
      }
   }

   private void checkReadableBytes(int var1) {
      if (this.buffer.readableBytes() < var1) {
         throw REPLAY;
      }
   }

   public ByteBuf discardSomeReadBytes() {
      throw reject();
   }

   public int refCnt() {
      return this.buffer.refCnt();
   }

   public ByteBuf retain() {
      throw reject();
   }

   public ByteBuf retain(int var1) {
      throw reject();
   }

   public ByteBuf touch() {
      this.buffer.touch();
      return this;
   }

   public ByteBuf touch(Object var1) {
      this.buffer.touch(var1);
      return this;
   }

   public boolean release() {
      throw reject();
   }

   public boolean release(int var1) {
      throw reject();
   }

   public ByteBuf unwrap() {
      throw reject();
   }

   private static UnsupportedOperationException reject() {
      return new UnsupportedOperationException("not a replayable operation");
   }

   static {
      REPLAY = ReplayingDecoder.REPLAY;
      EMPTY_BUFFER = new ReplayingDecoderByteBuf(Unpooled.EMPTY_BUFFER);
      EMPTY_BUFFER.terminate();
   }
}
