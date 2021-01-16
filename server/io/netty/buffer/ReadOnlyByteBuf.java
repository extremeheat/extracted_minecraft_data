package io.netty.buffer;

import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

/** @deprecated */
@Deprecated
public class ReadOnlyByteBuf extends AbstractDerivedByteBuf {
   private final ByteBuf buffer;

   public ReadOnlyByteBuf(ByteBuf var1) {
      super(var1.maxCapacity());
      if (!(var1 instanceof ReadOnlyByteBuf) && !(var1 instanceof DuplicatedByteBuf)) {
         this.buffer = var1;
      } else {
         this.buffer = var1.unwrap();
      }

      this.setIndex(var1.readerIndex(), var1.writerIndex());
   }

   public boolean isReadOnly() {
      return true;
   }

   public boolean isWritable() {
      return false;
   }

   public boolean isWritable(int var1) {
      return false;
   }

   public int ensureWritable(int var1, boolean var2) {
      return 1;
   }

   public ByteBuf ensureWritable(int var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf unwrap() {
      return this.buffer;
   }

   public ByteBufAllocator alloc() {
      return this.unwrap().alloc();
   }

   /** @deprecated */
   @Deprecated
   public ByteOrder order() {
      return this.unwrap().order();
   }

   public boolean isDirect() {
      return this.unwrap().isDirect();
   }

   public boolean hasArray() {
      return false;
   }

   public byte[] array() {
      throw new ReadOnlyBufferException();
   }

   public int arrayOffset() {
      throw new ReadOnlyBufferException();
   }

   public boolean hasMemoryAddress() {
      return this.unwrap().hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.unwrap().memoryAddress();
   }

   public ByteBuf discardReadBytes() {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setByte(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setByte(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setShort(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setShort(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setShortLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setShortLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setMedium(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setMedium(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setMediumLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setIntLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setIntLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setLongLE(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   protected void _setLongLE(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   public int setBytes(int var1, InputStream var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) {
      throw new ReadOnlyBufferException();
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.unwrap().getBytes(var1, var2, var3);
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.unwrap().getBytes(var1, var2, var3, var5);
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.unwrap().getBytes(var1, var2, var3);
      return this;
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.unwrap().getBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.unwrap().getBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.unwrap().getBytes(var1, var2);
      return this;
   }

   public ByteBuf duplicate() {
      return new ReadOnlyByteBuf(this);
   }

   public ByteBuf copy(int var1, int var2) {
      return this.unwrap().copy(var1, var2);
   }

   public ByteBuf slice(int var1, int var2) {
      return Unpooled.unmodifiableBuffer(this.unwrap().slice(var1, var2));
   }

   public byte getByte(int var1) {
      return this.unwrap().getByte(var1);
   }

   protected byte _getByte(int var1) {
      return this.unwrap().getByte(var1);
   }

   public short getShort(int var1) {
      return this.unwrap().getShort(var1);
   }

   protected short _getShort(int var1) {
      return this.unwrap().getShort(var1);
   }

   public short getShortLE(int var1) {
      return this.unwrap().getShortLE(var1);
   }

   protected short _getShortLE(int var1) {
      return this.unwrap().getShortLE(var1);
   }

   public int getUnsignedMedium(int var1) {
      return this.unwrap().getUnsignedMedium(var1);
   }

   protected int _getUnsignedMedium(int var1) {
      return this.unwrap().getUnsignedMedium(var1);
   }

   public int getUnsignedMediumLE(int var1) {
      return this.unwrap().getUnsignedMediumLE(var1);
   }

   protected int _getUnsignedMediumLE(int var1) {
      return this.unwrap().getUnsignedMediumLE(var1);
   }

   public int getInt(int var1) {
      return this.unwrap().getInt(var1);
   }

   protected int _getInt(int var1) {
      return this.unwrap().getInt(var1);
   }

   public int getIntLE(int var1) {
      return this.unwrap().getIntLE(var1);
   }

   protected int _getIntLE(int var1) {
      return this.unwrap().getIntLE(var1);
   }

   public long getLong(int var1) {
      return this.unwrap().getLong(var1);
   }

   protected long _getLong(int var1) {
      return this.unwrap().getLong(var1);
   }

   public long getLongLE(int var1) {
      return this.unwrap().getLongLE(var1);
   }

   protected long _getLongLE(int var1) {
      return this.unwrap().getLongLE(var1);
   }

   public int nioBufferCount() {
      return this.unwrap().nioBufferCount();
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.unwrap().nioBuffer(var1, var2).asReadOnlyBuffer();
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.unwrap().nioBuffers(var1, var2);
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.unwrap().forEachByte(var1, var2, var3);
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.unwrap().forEachByteDesc(var1, var2, var3);
   }

   public int capacity() {
      return this.unwrap().capacity();
   }

   public ByteBuf capacity(int var1) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf asReadOnly() {
      return this;
   }
}
