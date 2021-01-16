package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public abstract class AbstractByteBuf extends ByteBuf {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractByteBuf.class);
   private static final String PROP_MODE = "io.netty.buffer.bytebuf.checkAccessible";
   private static final boolean checkAccessible = SystemPropertyUtil.getBoolean("io.netty.buffer.bytebuf.checkAccessible", true);
   static final ResourceLeakDetector<ByteBuf> leakDetector;
   int readerIndex;
   int writerIndex;
   private int markedReaderIndex;
   private int markedWriterIndex;
   private int maxCapacity;

   protected AbstractByteBuf(int var1) {
      super();
      if (var1 < 0) {
         throw new IllegalArgumentException("maxCapacity: " + var1 + " (expected: >= 0)");
      } else {
         this.maxCapacity = var1;
      }
   }

   public boolean isReadOnly() {
      return false;
   }

   public ByteBuf asReadOnly() {
      return (ByteBuf)(this.isReadOnly() ? this : Unpooled.unmodifiableBuffer((ByteBuf)this));
   }

   public int maxCapacity() {
      return this.maxCapacity;
   }

   protected final void maxCapacity(int var1) {
      this.maxCapacity = var1;
   }

   public int readerIndex() {
      return this.readerIndex;
   }

   public ByteBuf readerIndex(int var1) {
      if (var1 >= 0 && var1 <= this.writerIndex) {
         this.readerIndex = var1;
         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("readerIndex: %d (expected: 0 <= readerIndex <= writerIndex(%d))", var1, this.writerIndex));
      }
   }

   public int writerIndex() {
      return this.writerIndex;
   }

   public ByteBuf writerIndex(int var1) {
      if (var1 >= this.readerIndex && var1 <= this.capacity()) {
         this.writerIndex = var1;
         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("writerIndex: %d (expected: readerIndex(%d) <= writerIndex <= capacity(%d))", var1, this.readerIndex, this.capacity()));
      }
   }

   public ByteBuf setIndex(int var1, int var2) {
      if (var1 >= 0 && var1 <= var2 && var2 <= this.capacity()) {
         this.setIndex0(var1, var2);
         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", var1, var2, this.capacity()));
      }
   }

   public ByteBuf clear() {
      this.readerIndex = this.writerIndex = 0;
      return this;
   }

   public boolean isReadable() {
      return this.writerIndex > this.readerIndex;
   }

   public boolean isReadable(int var1) {
      return this.writerIndex - this.readerIndex >= var1;
   }

   public boolean isWritable() {
      return this.capacity() > this.writerIndex;
   }

   public boolean isWritable(int var1) {
      return this.capacity() - this.writerIndex >= var1;
   }

   public int readableBytes() {
      return this.writerIndex - this.readerIndex;
   }

   public int writableBytes() {
      return this.capacity() - this.writerIndex;
   }

   public int maxWritableBytes() {
      return this.maxCapacity() - this.writerIndex;
   }

   public ByteBuf markReaderIndex() {
      this.markedReaderIndex = this.readerIndex;
      return this;
   }

   public ByteBuf resetReaderIndex() {
      this.readerIndex(this.markedReaderIndex);
      return this;
   }

   public ByteBuf markWriterIndex() {
      this.markedWriterIndex = this.writerIndex;
      return this;
   }

   public ByteBuf resetWriterIndex() {
      this.writerIndex(this.markedWriterIndex);
      return this;
   }

   public ByteBuf discardReadBytes() {
      this.ensureAccessible();
      if (this.readerIndex == 0) {
         return this;
      } else {
         if (this.readerIndex != this.writerIndex) {
            this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
            this.writerIndex -= this.readerIndex;
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
         } else {
            this.adjustMarkers(this.readerIndex);
            this.writerIndex = this.readerIndex = 0;
         }

         return this;
      }
   }

   public ByteBuf discardSomeReadBytes() {
      this.ensureAccessible();
      if (this.readerIndex == 0) {
         return this;
      } else if (this.readerIndex == this.writerIndex) {
         this.adjustMarkers(this.readerIndex);
         this.writerIndex = this.readerIndex = 0;
         return this;
      } else {
         if (this.readerIndex >= this.capacity() >>> 1) {
            this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
            this.writerIndex -= this.readerIndex;
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
         }

         return this;
      }
   }

   protected final void adjustMarkers(int var1) {
      int var2 = this.markedReaderIndex;
      if (var2 <= var1) {
         this.markedReaderIndex = 0;
         int var3 = this.markedWriterIndex;
         if (var3 <= var1) {
            this.markedWriterIndex = 0;
         } else {
            this.markedWriterIndex = var3 - var1;
         }
      } else {
         this.markedReaderIndex = var2 - var1;
         this.markedWriterIndex -= var1;
      }

   }

   public ByteBuf ensureWritable(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", var1));
      } else {
         this.ensureWritable0(var1);
         return this;
      }
   }

   final void ensureWritable0(int var1) {
      this.ensureAccessible();
      if (var1 > this.writableBytes()) {
         if (var1 > this.maxCapacity - this.writerIndex) {
            throw new IndexOutOfBoundsException(String.format("writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s", this.writerIndex, var1, this.maxCapacity, this));
         } else {
            int var2 = this.alloc().calculateNewCapacity(this.writerIndex + var1, this.maxCapacity);
            this.capacity(var2);
         }
      }
   }

   public int ensureWritable(int var1, boolean var2) {
      this.ensureAccessible();
      if (var1 < 0) {
         throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", var1));
      } else if (var1 <= this.writableBytes()) {
         return 0;
      } else {
         int var3 = this.maxCapacity();
         int var4 = this.writerIndex();
         if (var1 > var3 - var4) {
            if (var2 && this.capacity() != var3) {
               this.capacity(var3);
               return 3;
            } else {
               return 1;
            }
         } else {
            int var5 = this.alloc().calculateNewCapacity(var4 + var1, var3);
            this.capacity(var5);
            return 2;
         }
      }
   }

   public ByteBuf order(ByteOrder var1) {
      if (var1 == null) {
         throw new NullPointerException("endianness");
      } else {
         return (ByteBuf)(var1 == this.order() ? this : this.newSwappedByteBuf());
      }
   }

   protected SwappedByteBuf newSwappedByteBuf() {
      return new SwappedByteBuf(this);
   }

   public byte getByte(int var1) {
      this.checkIndex(var1);
      return this._getByte(var1);
   }

   protected abstract byte _getByte(int var1);

   public boolean getBoolean(int var1) {
      return this.getByte(var1) != 0;
   }

   public short getUnsignedByte(int var1) {
      return (short)(this.getByte(var1) & 255);
   }

   public short getShort(int var1) {
      this.checkIndex(var1, 2);
      return this._getShort(var1);
   }

   protected abstract short _getShort(int var1);

   public short getShortLE(int var1) {
      this.checkIndex(var1, 2);
      return this._getShortLE(var1);
   }

   protected abstract short _getShortLE(int var1);

   public int getUnsignedShort(int var1) {
      return this.getShort(var1) & '\uffff';
   }

   public int getUnsignedShortLE(int var1) {
      return this.getShortLE(var1) & '\uffff';
   }

   public int getUnsignedMedium(int var1) {
      this.checkIndex(var1, 3);
      return this._getUnsignedMedium(var1);
   }

   protected abstract int _getUnsignedMedium(int var1);

   public int getUnsignedMediumLE(int var1) {
      this.checkIndex(var1, 3);
      return this._getUnsignedMediumLE(var1);
   }

   protected abstract int _getUnsignedMediumLE(int var1);

   public int getMedium(int var1) {
      int var2 = this.getUnsignedMedium(var1);
      if ((var2 & 8388608) != 0) {
         var2 |= -16777216;
      }

      return var2;
   }

   public int getMediumLE(int var1) {
      int var2 = this.getUnsignedMediumLE(var1);
      if ((var2 & 8388608) != 0) {
         var2 |= -16777216;
      }

      return var2;
   }

   public int getInt(int var1) {
      this.checkIndex(var1, 4);
      return this._getInt(var1);
   }

   protected abstract int _getInt(int var1);

   public int getIntLE(int var1) {
      this.checkIndex(var1, 4);
      return this._getIntLE(var1);
   }

   protected abstract int _getIntLE(int var1);

   public long getUnsignedInt(int var1) {
      return (long)this.getInt(var1) & 4294967295L;
   }

   public long getUnsignedIntLE(int var1) {
      return (long)this.getIntLE(var1) & 4294967295L;
   }

   public long getLong(int var1) {
      this.checkIndex(var1, 8);
      return this._getLong(var1);
   }

   protected abstract long _getLong(int var1);

   public long getLongLE(int var1) {
      this.checkIndex(var1, 8);
      return this._getLongLE(var1);
   }

   protected abstract long _getLongLE(int var1);

   public char getChar(int var1) {
      return (char)this.getShort(var1);
   }

   public float getFloat(int var1) {
      return Float.intBitsToFloat(this.getInt(var1));
   }

   public double getDouble(int var1) {
      return Double.longBitsToDouble(this.getLong(var1));
   }

   public ByteBuf getBytes(int var1, byte[] var2) {
      this.getBytes(var1, var2, 0, var2.length);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuf var2) {
      this.getBytes(var1, var2, var2.writableBytes());
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      this.getBytes(var1, var2, var2.writerIndex(), var3);
      var2.writerIndex(var2.writerIndex() + var3);
      return this;
   }

   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      return this.toString(var1, var2, var3);
   }

   public CharSequence readCharSequence(int var1, Charset var2) {
      CharSequence var3 = this.getCharSequence(this.readerIndex, var1, var2);
      this.readerIndex += var1;
      return var3;
   }

   public ByteBuf setByte(int var1, int var2) {
      this.checkIndex(var1);
      this._setByte(var1, var2);
      return this;
   }

   protected abstract void _setByte(int var1, int var2);

   public ByteBuf setBoolean(int var1, boolean var2) {
      this.setByte(var1, var2 ? 1 : 0);
      return this;
   }

   public ByteBuf setShort(int var1, int var2) {
      this.checkIndex(var1, 2);
      this._setShort(var1, var2);
      return this;
   }

   protected abstract void _setShort(int var1, int var2);

   public ByteBuf setShortLE(int var1, int var2) {
      this.checkIndex(var1, 2);
      this._setShortLE(var1, var2);
      return this;
   }

   protected abstract void _setShortLE(int var1, int var2);

   public ByteBuf setChar(int var1, int var2) {
      this.setShort(var1, var2);
      return this;
   }

   public ByteBuf setMedium(int var1, int var2) {
      this.checkIndex(var1, 3);
      this._setMedium(var1, var2);
      return this;
   }

   protected abstract void _setMedium(int var1, int var2);

   public ByteBuf setMediumLE(int var1, int var2) {
      this.checkIndex(var1, 3);
      this._setMediumLE(var1, var2);
      return this;
   }

   protected abstract void _setMediumLE(int var1, int var2);

   public ByteBuf setInt(int var1, int var2) {
      this.checkIndex(var1, 4);
      this._setInt(var1, var2);
      return this;
   }

   protected abstract void _setInt(int var1, int var2);

   public ByteBuf setIntLE(int var1, int var2) {
      this.checkIndex(var1, 4);
      this._setIntLE(var1, var2);
      return this;
   }

   protected abstract void _setIntLE(int var1, int var2);

   public ByteBuf setFloat(int var1, float var2) {
      this.setInt(var1, Float.floatToRawIntBits(var2));
      return this;
   }

   public ByteBuf setLong(int var1, long var2) {
      this.checkIndex(var1, 8);
      this._setLong(var1, var2);
      return this;
   }

   protected abstract void _setLong(int var1, long var2);

   public ByteBuf setLongLE(int var1, long var2) {
      this.checkIndex(var1, 8);
      this._setLongLE(var1, var2);
      return this;
   }

   protected abstract void _setLongLE(int var1, long var2);

   public ByteBuf setDouble(int var1, double var2) {
      this.setLong(var1, Double.doubleToRawLongBits(var2));
      return this;
   }

   public ByteBuf setBytes(int var1, byte[] var2) {
      this.setBytes(var1, var2, 0, var2.length);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuf var2) {
      this.setBytes(var1, var2, var2.readableBytes());
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      this.checkIndex(var1, var3);
      if (var2 == null) {
         throw new NullPointerException("src");
      } else if (var3 > var2.readableBytes()) {
         throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", var3, var2.readableBytes(), var2));
      } else {
         this.setBytes(var1, var2, var2.readerIndex(), var3);
         var2.readerIndex(var2.readerIndex() + var3);
         return this;
      }
   }

   public ByteBuf setZero(int var1, int var2) {
      if (var2 == 0) {
         return this;
      } else {
         this.checkIndex(var1, var2);
         int var3 = var2 >>> 3;
         int var4 = var2 & 7;

         int var5;
         for(var5 = var3; var5 > 0; --var5) {
            this._setLong(var1, 0L);
            var1 += 8;
         }

         if (var4 == 4) {
            this._setInt(var1, 0);
         } else if (var4 < 4) {
            for(var5 = var4; var5 > 0; --var5) {
               this._setByte(var1, 0);
               ++var1;
            }
         } else {
            this._setInt(var1, 0);
            var1 += 4;

            for(var5 = var4 - 4; var5 > 0; --var5) {
               this._setByte(var1, 0);
               ++var1;
            }
         }

         return this;
      }
   }

   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      return this.setCharSequence0(var1, var2, var3, false);
   }

   private int setCharSequence0(int var1, CharSequence var2, Charset var3, boolean var4) {
      int var5;
      if (var3.equals(CharsetUtil.UTF_8)) {
         var5 = ByteBufUtil.utf8MaxBytes(var2);
         if (var4) {
            this.ensureWritable0(var5);
            this.checkIndex0(var1, var5);
         } else {
            this.checkIndex(var1, var5);
         }

         return ByteBufUtil.writeUtf8(this, var1, var2, var2.length());
      } else if (!var3.equals(CharsetUtil.US_ASCII) && !var3.equals(CharsetUtil.ISO_8859_1)) {
         byte[] var6 = var2.toString().getBytes(var3);
         if (var4) {
            this.ensureWritable0(var6.length);
         }

         this.setBytes(var1, var6);
         return var6.length;
      } else {
         var5 = var2.length();
         if (var4) {
            this.ensureWritable0(var5);
            this.checkIndex0(var1, var5);
         } else {
            this.checkIndex(var1, var5);
         }

         return ByteBufUtil.writeAscii(this, var1, var2, var5);
      }
   }

   public byte readByte() {
      this.checkReadableBytes0(1);
      int var1 = this.readerIndex;
      byte var2 = this._getByte(var1);
      this.readerIndex = var1 + 1;
      return var2;
   }

   public boolean readBoolean() {
      return this.readByte() != 0;
   }

   public short readUnsignedByte() {
      return (short)(this.readByte() & 255);
   }

   public short readShort() {
      this.checkReadableBytes0(2);
      short var1 = this._getShort(this.readerIndex);
      this.readerIndex += 2;
      return var1;
   }

   public short readShortLE() {
      this.checkReadableBytes0(2);
      short var1 = this._getShortLE(this.readerIndex);
      this.readerIndex += 2;
      return var1;
   }

   public int readUnsignedShort() {
      return this.readShort() & '\uffff';
   }

   public int readUnsignedShortLE() {
      return this.readShortLE() & '\uffff';
   }

   public int readMedium() {
      int var1 = this.readUnsignedMedium();
      if ((var1 & 8388608) != 0) {
         var1 |= -16777216;
      }

      return var1;
   }

   public int readMediumLE() {
      int var1 = this.readUnsignedMediumLE();
      if ((var1 & 8388608) != 0) {
         var1 |= -16777216;
      }

      return var1;
   }

   public int readUnsignedMedium() {
      this.checkReadableBytes0(3);
      int var1 = this._getUnsignedMedium(this.readerIndex);
      this.readerIndex += 3;
      return var1;
   }

   public int readUnsignedMediumLE() {
      this.checkReadableBytes0(3);
      int var1 = this._getUnsignedMediumLE(this.readerIndex);
      this.readerIndex += 3;
      return var1;
   }

   public int readInt() {
      this.checkReadableBytes0(4);
      int var1 = this._getInt(this.readerIndex);
      this.readerIndex += 4;
      return var1;
   }

   public int readIntLE() {
      this.checkReadableBytes0(4);
      int var1 = this._getIntLE(this.readerIndex);
      this.readerIndex += 4;
      return var1;
   }

   public long readUnsignedInt() {
      return (long)this.readInt() & 4294967295L;
   }

   public long readUnsignedIntLE() {
      return (long)this.readIntLE() & 4294967295L;
   }

   public long readLong() {
      this.checkReadableBytes0(8);
      long var1 = this._getLong(this.readerIndex);
      this.readerIndex += 8;
      return var1;
   }

   public long readLongLE() {
      this.checkReadableBytes0(8);
      long var1 = this._getLongLE(this.readerIndex);
      this.readerIndex += 8;
      return var1;
   }

   public char readChar() {
      return (char)this.readShort();
   }

   public float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   public double readDouble() {
      return Double.longBitsToDouble(this.readLong());
   }

   public ByteBuf readBytes(int var1) {
      this.checkReadableBytes(var1);
      if (var1 == 0) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBuf var2 = this.alloc().buffer(var1, this.maxCapacity);
         var2.writeBytes((ByteBuf)this, this.readerIndex, var1);
         this.readerIndex += var1;
         return var2;
      }
   }

   public ByteBuf readSlice(int var1) {
      this.checkReadableBytes(var1);
      ByteBuf var2 = this.slice(this.readerIndex, var1);
      this.readerIndex += var1;
      return var2;
   }

   public ByteBuf readRetainedSlice(int var1) {
      this.checkReadableBytes(var1);
      ByteBuf var2 = this.retainedSlice(this.readerIndex, var1);
      this.readerIndex += var1;
      return var2;
   }

   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.checkReadableBytes(var3);
      this.getBytes(this.readerIndex, var1, var2, var3);
      this.readerIndex += var3;
      return this;
   }

   public ByteBuf readBytes(byte[] var1) {
      this.readBytes((byte[])var1, 0, var1.length);
      return this;
   }

   public ByteBuf readBytes(ByteBuf var1) {
      this.readBytes(var1, var1.writableBytes());
      return this;
   }

   public ByteBuf readBytes(ByteBuf var1, int var2) {
      if (var2 > var1.writableBytes()) {
         throw new IndexOutOfBoundsException(String.format("length(%d) exceeds dst.writableBytes(%d) where dst is: %s", var2, var1.writableBytes(), var1));
      } else {
         this.readBytes(var1, var1.writerIndex(), var2);
         var1.writerIndex(var1.writerIndex() + var2);
         return this;
      }
   }

   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      this.checkReadableBytes(var3);
      this.getBytes(this.readerIndex, var1, var2, var3);
      this.readerIndex += var3;
      return this;
   }

   public ByteBuf readBytes(ByteBuffer var1) {
      int var2 = var1.remaining();
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, (ByteBuffer)var1);
      this.readerIndex += var2;
      return this;
   }

   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      int var3 = this.getBytes(this.readerIndex, var1, var2);
      this.readerIndex += var3;
      return var3;
   }

   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      this.checkReadableBytes(var4);
      int var5 = this.getBytes(this.readerIndex, var1, var2, var4);
      this.readerIndex += var5;
      return var5;
   }

   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, var1, var2);
      this.readerIndex += var2;
      return this;
   }

   public ByteBuf skipBytes(int var1) {
      this.checkReadableBytes(var1);
      this.readerIndex += var1;
      return this;
   }

   public ByteBuf writeBoolean(boolean var1) {
      this.writeByte(var1 ? 1 : 0);
      return this;
   }

   public ByteBuf writeByte(int var1) {
      this.ensureWritable0(1);
      this._setByte(this.writerIndex++, var1);
      return this;
   }

   public ByteBuf writeShort(int var1) {
      this.ensureWritable0(2);
      this._setShort(this.writerIndex, var1);
      this.writerIndex += 2;
      return this;
   }

   public ByteBuf writeShortLE(int var1) {
      this.ensureWritable0(2);
      this._setShortLE(this.writerIndex, var1);
      this.writerIndex += 2;
      return this;
   }

   public ByteBuf writeMedium(int var1) {
      this.ensureWritable0(3);
      this._setMedium(this.writerIndex, var1);
      this.writerIndex += 3;
      return this;
   }

   public ByteBuf writeMediumLE(int var1) {
      this.ensureWritable0(3);
      this._setMediumLE(this.writerIndex, var1);
      this.writerIndex += 3;
      return this;
   }

   public ByteBuf writeInt(int var1) {
      this.ensureWritable0(4);
      this._setInt(this.writerIndex, var1);
      this.writerIndex += 4;
      return this;
   }

   public ByteBuf writeIntLE(int var1) {
      this.ensureWritable0(4);
      this._setIntLE(this.writerIndex, var1);
      this.writerIndex += 4;
      return this;
   }

   public ByteBuf writeLong(long var1) {
      this.ensureWritable0(8);
      this._setLong(this.writerIndex, var1);
      this.writerIndex += 8;
      return this;
   }

   public ByteBuf writeLongLE(long var1) {
      this.ensureWritable0(8);
      this._setLongLE(this.writerIndex, var1);
      this.writerIndex += 8;
      return this;
   }

   public ByteBuf writeChar(int var1) {
      this.writeShort(var1);
      return this;
   }

   public ByteBuf writeFloat(float var1) {
      this.writeInt(Float.floatToRawIntBits(var1));
      return this;
   }

   public ByteBuf writeDouble(double var1) {
      this.writeLong(Double.doubleToRawLongBits(var1));
      return this;
   }

   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      this.ensureWritable(var3);
      this.setBytes(this.writerIndex, var1, var2, var3);
      this.writerIndex += var3;
      return this;
   }

   public ByteBuf writeBytes(byte[] var1) {
      this.writeBytes((byte[])var1, 0, var1.length);
      return this;
   }

   public ByteBuf writeBytes(ByteBuf var1) {
      this.writeBytes(var1, var1.readableBytes());
      return this;
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      if (var2 > var1.readableBytes()) {
         throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", var2, var1.readableBytes(), var1));
      } else {
         this.writeBytes(var1, var1.readerIndex(), var2);
         var1.readerIndex(var1.readerIndex() + var2);
         return this;
      }
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      this.ensureWritable(var3);
      this.setBytes(this.writerIndex, var1, var2, var3);
      this.writerIndex += var3;
      return this;
   }

   public ByteBuf writeBytes(ByteBuffer var1) {
      int var2 = var1.remaining();
      this.ensureWritable0(var2);
      this.setBytes(this.writerIndex, (ByteBuffer)var1);
      this.writerIndex += var2;
      return this;
   }

   public int writeBytes(InputStream var1, int var2) throws IOException {
      this.ensureWritable(var2);
      int var3 = this.setBytes(this.writerIndex, var1, var2);
      if (var3 > 0) {
         this.writerIndex += var3;
      }

      return var3;
   }

   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      this.ensureWritable(var2);
      int var3 = this.setBytes(this.writerIndex, var1, var2);
      if (var3 > 0) {
         this.writerIndex += var3;
      }

      return var3;
   }

   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      this.ensureWritable(var4);
      int var5 = this.setBytes(this.writerIndex, var1, var2, var4);
      if (var5 > 0) {
         this.writerIndex += var5;
      }

      return var5;
   }

   public ByteBuf writeZero(int var1) {
      if (var1 == 0) {
         return this;
      } else {
         this.ensureWritable(var1);
         int var2 = this.writerIndex;
         this.checkIndex0(var2, var1);
         int var3 = var1 >>> 3;
         int var4 = var1 & 7;

         int var5;
         for(var5 = var3; var5 > 0; --var5) {
            this._setLong(var2, 0L);
            var2 += 8;
         }

         if (var4 == 4) {
            this._setInt(var2, 0);
            var2 += 4;
         } else if (var4 < 4) {
            for(var5 = var4; var5 > 0; --var5) {
               this._setByte(var2, 0);
               ++var2;
            }
         } else {
            this._setInt(var2, 0);
            var2 += 4;

            for(var5 = var4 - 4; var5 > 0; --var5) {
               this._setByte(var2, 0);
               ++var2;
            }
         }

         this.writerIndex = var2;
         return this;
      }
   }

   public int writeCharSequence(CharSequence var1, Charset var2) {
      int var3 = this.setCharSequence0(this.writerIndex, var1, var2, true);
      this.writerIndex += var3;
      return var3;
   }

   public ByteBuf copy() {
      return this.copy(this.readerIndex, this.readableBytes());
   }

   public ByteBuf duplicate() {
      this.ensureAccessible();
      return new UnpooledDuplicatedByteBuf(this);
   }

   public ByteBuf retainedDuplicate() {
      return this.duplicate().retain();
   }

   public ByteBuf slice() {
      return this.slice(this.readerIndex, this.readableBytes());
   }

   public ByteBuf retainedSlice() {
      return this.slice().retain();
   }

   public ByteBuf slice(int var1, int var2) {
      this.ensureAccessible();
      return new UnpooledSlicedByteBuf(this, var1, var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.slice(var1, var2).retain();
   }

   public ByteBuffer nioBuffer() {
      return this.nioBuffer(this.readerIndex, this.readableBytes());
   }

   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(this.readerIndex, this.readableBytes());
   }

   public String toString(Charset var1) {
      return this.toString(this.readerIndex, this.readableBytes(), var1);
   }

   public String toString(int var1, int var2, Charset var3) {
      return ByteBufUtil.decodeString(this, var1, var2, var3);
   }

   public int indexOf(int var1, int var2, byte var3) {
      return ByteBufUtil.indexOf(this, var1, var2, var3);
   }

   public int bytesBefore(byte var1) {
      return this.bytesBefore(this.readerIndex(), this.readableBytes(), var1);
   }

   public int bytesBefore(int var1, byte var2) {
      this.checkReadableBytes(var1);
      return this.bytesBefore(this.readerIndex(), var1, var2);
   }

   public int bytesBefore(int var1, int var2, byte var3) {
      int var4 = this.indexOf(var1, var1 + var2, var3);
      return var4 < 0 ? -1 : var4 - var1;
   }

   public int forEachByte(ByteProcessor var1) {
      this.ensureAccessible();

      try {
         return this.forEachByteAsc0(this.readerIndex, this.writerIndex, var1);
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
         return -1;
      }
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      this.checkIndex(var1, var2);

      try {
         return this.forEachByteAsc0(var1, var1 + var2, var3);
      } catch (Exception var5) {
         PlatformDependent.throwException(var5);
         return -1;
      }
   }

   private int forEachByteAsc0(int var1, int var2, ByteProcessor var3) throws Exception {
      while(var1 < var2) {
         if (!var3.process(this._getByte(var1))) {
            return var1;
         }

         ++var1;
      }

      return -1;
   }

   public int forEachByteDesc(ByteProcessor var1) {
      this.ensureAccessible();

      try {
         return this.forEachByteDesc0(this.writerIndex - 1, this.readerIndex, var1);
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
         return -1;
      }
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      this.checkIndex(var1, var2);

      try {
         return this.forEachByteDesc0(var1 + var2 - 1, var1, var3);
      } catch (Exception var5) {
         PlatformDependent.throwException(var5);
         return -1;
      }
   }

   private int forEachByteDesc0(int var1, int var2, ByteProcessor var3) throws Exception {
      while(var1 >= var2) {
         if (!var3.process(this._getByte(var1))) {
            return var1;
         }

         --var1;
      }

      return -1;
   }

   public int hashCode() {
      return ByteBufUtil.hashCode(this);
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof ByteBuf && ByteBufUtil.equals(this, (ByteBuf)var1);
   }

   public int compareTo(ByteBuf var1) {
      return ByteBufUtil.compare(this, var1);
   }

   public String toString() {
      if (this.refCnt() == 0) {
         return StringUtil.simpleClassName((Object)this) + "(freed)";
      } else {
         StringBuilder var1 = (new StringBuilder()).append(StringUtil.simpleClassName((Object)this)).append("(ridx: ").append(this.readerIndex).append(", widx: ").append(this.writerIndex).append(", cap: ").append(this.capacity());
         if (this.maxCapacity != 2147483647) {
            var1.append('/').append(this.maxCapacity);
         }

         ByteBuf var2 = this.unwrap();
         if (var2 != null) {
            var1.append(", unwrapped: ").append(var2);
         }

         var1.append(')');
         return var1.toString();
      }
   }

   protected final void checkIndex(int var1) {
      this.checkIndex(var1, 1);
   }

   protected final void checkIndex(int var1, int var2) {
      this.ensureAccessible();
      this.checkIndex0(var1, var2);
   }

   final void checkIndex0(int var1, int var2) {
      if (MathUtil.isOutOfBounds(var1, var2, this.capacity())) {
         throw new IndexOutOfBoundsException(String.format("index: %d, length: %d (expected: range(0, %d))", var1, var2, this.capacity()));
      }
   }

   protected final void checkSrcIndex(int var1, int var2, int var3, int var4) {
      this.checkIndex(var1, var2);
      if (MathUtil.isOutOfBounds(var3, var2, var4)) {
         throw new IndexOutOfBoundsException(String.format("srcIndex: %d, length: %d (expected: range(0, %d))", var3, var2, var4));
      }
   }

   protected final void checkDstIndex(int var1, int var2, int var3, int var4) {
      this.checkIndex(var1, var2);
      if (MathUtil.isOutOfBounds(var3, var2, var4)) {
         throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", var3, var2, var4));
      }
   }

   protected final void checkReadableBytes(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("minimumReadableBytes: " + var1 + " (expected: >= 0)");
      } else {
         this.checkReadableBytes0(var1);
      }
   }

   protected final void checkNewCapacity(int var1) {
      this.ensureAccessible();
      if (var1 < 0 || var1 > this.maxCapacity()) {
         throw new IllegalArgumentException("newCapacity: " + var1 + " (expected: 0-" + this.maxCapacity() + ')');
      }
   }

   private void checkReadableBytes0(int var1) {
      this.ensureAccessible();
      if (this.readerIndex > this.writerIndex - var1) {
         throw new IndexOutOfBoundsException(String.format("readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s", this.readerIndex, var1, this.writerIndex, this));
      }
   }

   protected final void ensureAccessible() {
      if (checkAccessible && this.refCnt() == 0) {
         throw new IllegalReferenceCountException(0);
      }
   }

   final void setIndex0(int var1, int var2) {
      this.readerIndex = var1;
      this.writerIndex = var2;
   }

   final void discardMarks() {
      this.markedReaderIndex = this.markedWriterIndex = 0;
   }

   static {
      if (logger.isDebugEnabled()) {
         logger.debug("-D{}: {}", "io.netty.buffer.bytebuf.checkAccessible", checkAccessible);
      }

      leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ByteBuf.class);
   }
}
