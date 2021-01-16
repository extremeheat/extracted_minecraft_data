package io.netty.buffer;

import io.netty.util.Recycler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledDirectByteBuf extends PooledByteBuf<ByteBuffer> {
   private static final Recycler<PooledDirectByteBuf> RECYCLER = new Recycler<PooledDirectByteBuf>() {
      protected PooledDirectByteBuf newObject(Recycler.Handle<PooledDirectByteBuf> var1) {
         return new PooledDirectByteBuf(var1, 0);
      }
   };

   static PooledDirectByteBuf newInstance(int var0) {
      PooledDirectByteBuf var1 = (PooledDirectByteBuf)RECYCLER.get();
      var1.reuse(var0);
      return var1;
   }

   private PooledDirectByteBuf(Recycler.Handle<PooledDirectByteBuf> var1, int var2) {
      super(var1, var2);
   }

   protected ByteBuffer newInternalNioBuffer(ByteBuffer var1) {
      return var1.duplicate();
   }

   public boolean isDirect() {
      return true;
   }

   protected byte _getByte(int var1) {
      return ((ByteBuffer)this.memory).get(this.idx(var1));
   }

   protected short _getShort(int var1) {
      return ((ByteBuffer)this.memory).getShort(this.idx(var1));
   }

   protected short _getShortLE(int var1) {
      return ByteBufUtil.swapShort(this._getShort(var1));
   }

   protected int _getUnsignedMedium(int var1) {
      var1 = this.idx(var1);
      return (((ByteBuffer)this.memory).get(var1) & 255) << 16 | (((ByteBuffer)this.memory).get(var1 + 1) & 255) << 8 | ((ByteBuffer)this.memory).get(var1 + 2) & 255;
   }

   protected int _getUnsignedMediumLE(int var1) {
      var1 = this.idx(var1);
      return ((ByteBuffer)this.memory).get(var1) & 255 | (((ByteBuffer)this.memory).get(var1 + 1) & 255) << 8 | (((ByteBuffer)this.memory).get(var1 + 2) & 255) << 16;
   }

   protected int _getInt(int var1) {
      return ((ByteBuffer)this.memory).getInt(this.idx(var1));
   }

   protected int _getIntLE(int var1) {
      return ByteBufUtil.swapInt(this._getInt(var1));
   }

   protected long _getLong(int var1) {
      return ((ByteBuffer)this.memory).getLong(this.idx(var1));
   }

   protected long _getLongLE(int var1) {
      return ByteBufUtil.swapLong(this._getLong(var1));
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var2.hasArray()) {
         this.getBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else if (var2.nioBufferCount() > 0) {
         ByteBuffer[] var5 = var2.nioBuffers(var3, var4);
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ByteBuffer var8 = var5[var7];
            int var9 = var8.remaining();
            this.getBytes(var1, var8);
            var1 += var9;
         }
      } else {
         var2.setBytes(var3, (ByteBuf)this, var1, var4);
      }

      return this;
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.getBytes(var1, var2, var3, var4, false);
      return this;
   }

   private void getBytes(int var1, byte[] var2, int var3, int var4, boolean var5) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      ByteBuffer var6;
      if (var5) {
         var6 = this.internalNioBuffer();
      } else {
         var6 = ((ByteBuffer)this.memory).duplicate();
      }

      var1 = this.idx(var1);
      var6.clear().position(var1).limit(var1 + var4);
      var6.get(var2, var3, var4);
   }

   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.checkReadableBytes(var3);
      this.getBytes(this.readerIndex, var1, var2, var3, true);
      this.readerIndex += var3;
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.getBytes(var1, var2, false);
      return this;
   }

   private void getBytes(int var1, ByteBuffer var2, boolean var3) {
      this.checkIndex(var1, var2.remaining());
      ByteBuffer var4;
      if (var3) {
         var4 = this.internalNioBuffer();
      } else {
         var4 = ((ByteBuffer)this.memory).duplicate();
      }

      var1 = this.idx(var1);
      var4.clear().position(var1).limit(var1 + var2.remaining());
      var2.put(var4);
   }

   public ByteBuf readBytes(ByteBuffer var1) {
      int var2 = var1.remaining();
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, var1, true);
      this.readerIndex += var2;
      return this;
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.getBytes(var1, var2, var3, false);
      return this;
   }

   private void getBytes(int var1, OutputStream var2, int var3, boolean var4) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 != 0) {
         ByteBufUtil.readBytes(this.alloc(), var4 ? this.internalNioBuffer() : ((ByteBuffer)this.memory).duplicate(), this.idx(var1), var3, var2);
      }
   }

   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, var1, var2, true);
      this.readerIndex += var2;
      return this;
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.getBytes(var1, var2, var3, false);
   }

   private int getBytes(int var1, GatheringByteChannel var2, int var3, boolean var4) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return 0;
      } else {
         ByteBuffer var5;
         if (var4) {
            var5 = this.internalNioBuffer();
         } else {
            var5 = ((ByteBuffer)this.memory).duplicate();
         }

         var1 = this.idx(var1);
         var5.clear().position(var1).limit(var1 + var3);
         return var2.write(var5);
      }
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.getBytes(var1, var2, var3, var5, false);
   }

   private int getBytes(int var1, FileChannel var2, long var3, int var5, boolean var6) throws IOException {
      this.checkIndex(var1, var5);
      if (var5 == 0) {
         return 0;
      } else {
         ByteBuffer var7 = var6 ? this.internalNioBuffer() : ((ByteBuffer)this.memory).duplicate();
         var1 = this.idx(var1);
         var7.clear().position(var1).limit(var1 + var5);
         return var2.write(var7, var3);
      }
   }

   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      int var3 = this.getBytes(this.readerIndex, var1, var2, true);
      this.readerIndex += var3;
      return var3;
   }

   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      this.checkReadableBytes(var4);
      int var5 = this.getBytes(this.readerIndex, var1, var2, var4, true);
      this.readerIndex += var5;
      return var5;
   }

   protected void _setByte(int var1, int var2) {
      ((ByteBuffer)this.memory).put(this.idx(var1), (byte)var2);
   }

   protected void _setShort(int var1, int var2) {
      ((ByteBuffer)this.memory).putShort(this.idx(var1), (short)var2);
   }

   protected void _setShortLE(int var1, int var2) {
      this._setShort(var1, ByteBufUtil.swapShort((short)var2));
   }

   protected void _setMedium(int var1, int var2) {
      var1 = this.idx(var1);
      ((ByteBuffer)this.memory).put(var1, (byte)(var2 >>> 16));
      ((ByteBuffer)this.memory).put(var1 + 1, (byte)(var2 >>> 8));
      ((ByteBuffer)this.memory).put(var1 + 2, (byte)var2);
   }

   protected void _setMediumLE(int var1, int var2) {
      var1 = this.idx(var1);
      ((ByteBuffer)this.memory).put(var1, (byte)var2);
      ((ByteBuffer)this.memory).put(var1 + 1, (byte)(var2 >>> 8));
      ((ByteBuffer)this.memory).put(var1 + 2, (byte)(var2 >>> 16));
   }

   protected void _setInt(int var1, int var2) {
      ((ByteBuffer)this.memory).putInt(this.idx(var1), var2);
   }

   protected void _setIntLE(int var1, int var2) {
      this._setInt(var1, ByteBufUtil.swapInt(var2));
   }

   protected void _setLong(int var1, long var2) {
      ((ByteBuffer)this.memory).putLong(this.idx(var1), var2);
   }

   protected void _setLongLE(int var1, long var2) {
      this._setLong(var1, ByteBufUtil.swapLong(var2));
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var2.hasArray()) {
         this.setBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else if (var2.nioBufferCount() > 0) {
         ByteBuffer[] var5 = var2.nioBuffers(var3, var4);
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ByteBuffer var8 = var5[var7];
            int var9 = var8.remaining();
            this.setBytes(var1, var8);
            var1 += var9;
         }
      } else {
         var2.getBytes(var3, (ByteBuf)this, var1, var4);
      }

      return this;
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      ByteBuffer var5 = this.internalNioBuffer();
      var1 = this.idx(var1);
      var5.clear().position(var1).limit(var1 + var4);
      var5.put(var2, var3, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.checkIndex(var1, var2.remaining());
      ByteBuffer var3 = this.internalNioBuffer();
      if (var2 == var3) {
         var2 = var2.duplicate();
      }

      var1 = this.idx(var1);
      var3.clear().position(var1).limit(var1 + var2.remaining());
      var3.put(var2);
      return this;
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      byte[] var4 = new byte[var3];
      int var5 = var2.read(var4);
      if (var5 <= 0) {
         return var5;
      } else {
         ByteBuffer var6 = this.internalNioBuffer();
         var6.clear().position(this.idx(var1));
         var6.put(var4, 0, var5);
         return var5;
      }
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      ByteBuffer var4 = this.internalNioBuffer();
      var1 = this.idx(var1);
      var4.clear().position(var1).limit(var1 + var3);

      try {
         return var2.read(var4);
      } catch (ClosedChannelException var6) {
         return -1;
      }
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex(var1, var5);
      ByteBuffer var6 = this.internalNioBuffer();
      var1 = this.idx(var1);
      var6.clear().position(var1).limit(var1 + var5);

      try {
         return var2.read(var6, var3);
      } catch (ClosedChannelException var8) {
         return -1;
      }
   }

   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.alloc().directBuffer(var2, this.maxCapacity());
      var3.writeBytes((ByteBuf)this, var1, var2);
      return var3;
   }

   public int nioBufferCount() {
      return 1;
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      var1 = this.idx(var1);
      return ((ByteBuffer)((ByteBuffer)this.memory).duplicate().position(var1).limit(var1 + var2)).slice();
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return new ByteBuffer[]{this.nioBuffer(var1, var2)};
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      var1 = this.idx(var1);
      return (ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var2);
   }

   public boolean hasArray() {
      return false;
   }

   public byte[] array() {
      throw new UnsupportedOperationException("direct buffer");
   }

   public int arrayOffset() {
      throw new UnsupportedOperationException("direct buffer");
   }

   public boolean hasMemoryAddress() {
      return false;
   }

   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   // $FF: synthetic method
   PooledDirectByteBuf(Recycler.Handle var1, int var2, Object var3) {
      this(var1, var2);
   }
}
