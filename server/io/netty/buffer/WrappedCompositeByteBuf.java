package io.netty.buffer;

import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

class WrappedCompositeByteBuf extends CompositeByteBuf {
   private final CompositeByteBuf wrapped;

   WrappedCompositeByteBuf(CompositeByteBuf var1) {
      super(var1.alloc());
      this.wrapped = var1;
   }

   public boolean release() {
      return this.wrapped.release();
   }

   public boolean release(int var1) {
      return this.wrapped.release(var1);
   }

   public final int maxCapacity() {
      return this.wrapped.maxCapacity();
   }

   public final int readerIndex() {
      return this.wrapped.readerIndex();
   }

   public final int writerIndex() {
      return this.wrapped.writerIndex();
   }

   public final boolean isReadable() {
      return this.wrapped.isReadable();
   }

   public final boolean isReadable(int var1) {
      return this.wrapped.isReadable(var1);
   }

   public final boolean isWritable() {
      return this.wrapped.isWritable();
   }

   public final boolean isWritable(int var1) {
      return this.wrapped.isWritable(var1);
   }

   public final int readableBytes() {
      return this.wrapped.readableBytes();
   }

   public final int writableBytes() {
      return this.wrapped.writableBytes();
   }

   public final int maxWritableBytes() {
      return this.wrapped.maxWritableBytes();
   }

   public int ensureWritable(int var1, boolean var2) {
      return this.wrapped.ensureWritable(var1, var2);
   }

   public ByteBuf order(ByteOrder var1) {
      return this.wrapped.order(var1);
   }

   public boolean getBoolean(int var1) {
      return this.wrapped.getBoolean(var1);
   }

   public short getUnsignedByte(int var1) {
      return this.wrapped.getUnsignedByte(var1);
   }

   public short getShort(int var1) {
      return this.wrapped.getShort(var1);
   }

   public short getShortLE(int var1) {
      return this.wrapped.getShortLE(var1);
   }

   public int getUnsignedShort(int var1) {
      return this.wrapped.getUnsignedShort(var1);
   }

   public int getUnsignedShortLE(int var1) {
      return this.wrapped.getUnsignedShortLE(var1);
   }

   public int getUnsignedMedium(int var1) {
      return this.wrapped.getUnsignedMedium(var1);
   }

   public int getUnsignedMediumLE(int var1) {
      return this.wrapped.getUnsignedMediumLE(var1);
   }

   public int getMedium(int var1) {
      return this.wrapped.getMedium(var1);
   }

   public int getMediumLE(int var1) {
      return this.wrapped.getMediumLE(var1);
   }

   public int getInt(int var1) {
      return this.wrapped.getInt(var1);
   }

   public int getIntLE(int var1) {
      return this.wrapped.getIntLE(var1);
   }

   public long getUnsignedInt(int var1) {
      return this.wrapped.getUnsignedInt(var1);
   }

   public long getUnsignedIntLE(int var1) {
      return this.wrapped.getUnsignedIntLE(var1);
   }

   public long getLong(int var1) {
      return this.wrapped.getLong(var1);
   }

   public long getLongLE(int var1) {
      return this.wrapped.getLongLE(var1);
   }

   public char getChar(int var1) {
      return this.wrapped.getChar(var1);
   }

   public float getFloat(int var1) {
      return this.wrapped.getFloat(var1);
   }

   public double getDouble(int var1) {
      return this.wrapped.getDouble(var1);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      return this.wrapped.setShortLE(var1, var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      return this.wrapped.setMediumLE(var1, var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      return this.wrapped.setIntLE(var1, var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      return this.wrapped.setLongLE(var1, var2);
   }

   public byte readByte() {
      return this.wrapped.readByte();
   }

   public boolean readBoolean() {
      return this.wrapped.readBoolean();
   }

   public short readUnsignedByte() {
      return this.wrapped.readUnsignedByte();
   }

   public short readShort() {
      return this.wrapped.readShort();
   }

   public short readShortLE() {
      return this.wrapped.readShortLE();
   }

   public int readUnsignedShort() {
      return this.wrapped.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      return this.wrapped.readUnsignedShortLE();
   }

   public int readMedium() {
      return this.wrapped.readMedium();
   }

   public int readMediumLE() {
      return this.wrapped.readMediumLE();
   }

   public int readUnsignedMedium() {
      return this.wrapped.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      return this.wrapped.readUnsignedMediumLE();
   }

   public int readInt() {
      return this.wrapped.readInt();
   }

   public int readIntLE() {
      return this.wrapped.readIntLE();
   }

   public long readUnsignedInt() {
      return this.wrapped.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      return this.wrapped.readUnsignedIntLE();
   }

   public long readLong() {
      return this.wrapped.readLong();
   }

   public long readLongLE() {
      return this.wrapped.readLongLE();
   }

   public char readChar() {
      return this.wrapped.readChar();
   }

   public float readFloat() {
      return this.wrapped.readFloat();
   }

   public double readDouble() {
      return this.wrapped.readDouble();
   }

   public ByteBuf readBytes(int var1) {
      return this.wrapped.readBytes(var1);
   }

   public ByteBuf slice() {
      return this.wrapped.slice();
   }

   public ByteBuf retainedSlice() {
      return this.wrapped.retainedSlice();
   }

   public ByteBuf slice(int var1, int var2) {
      return this.wrapped.slice(var1, var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.wrapped.retainedSlice(var1, var2);
   }

   public ByteBuffer nioBuffer() {
      return this.wrapped.nioBuffer();
   }

   public String toString(Charset var1) {
      return this.wrapped.toString(var1);
   }

   public String toString(int var1, int var2, Charset var3) {
      return this.wrapped.toString(var1, var2, var3);
   }

   public int indexOf(int var1, int var2, byte var3) {
      return this.wrapped.indexOf(var1, var2, var3);
   }

   public int bytesBefore(byte var1) {
      return this.wrapped.bytesBefore(var1);
   }

   public int bytesBefore(int var1, byte var2) {
      return this.wrapped.bytesBefore(var1, var2);
   }

   public int bytesBefore(int var1, int var2, byte var3) {
      return this.wrapped.bytesBefore(var1, var2, var3);
   }

   public int forEachByte(ByteProcessor var1) {
      return this.wrapped.forEachByte(var1);
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.wrapped.forEachByte(var1, var2, var3);
   }

   public int forEachByteDesc(ByteProcessor var1) {
      return this.wrapped.forEachByteDesc(var1);
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.wrapped.forEachByteDesc(var1, var2, var3);
   }

   public final int hashCode() {
      return this.wrapped.hashCode();
   }

   public final boolean equals(Object var1) {
      return this.wrapped.equals(var1);
   }

   public final int compareTo(ByteBuf var1) {
      return this.wrapped.compareTo(var1);
   }

   public final int refCnt() {
      return this.wrapped.refCnt();
   }

   public ByteBuf duplicate() {
      return this.wrapped.duplicate();
   }

   public ByteBuf retainedDuplicate() {
      return this.wrapped.retainedDuplicate();
   }

   public ByteBuf readSlice(int var1) {
      return this.wrapped.readSlice(var1);
   }

   public ByteBuf readRetainedSlice(int var1) {
      return this.wrapped.readRetainedSlice(var1);
   }

   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      return this.wrapped.readBytes((GatheringByteChannel)var1, var2);
   }

   public ByteBuf writeShortLE(int var1) {
      return this.wrapped.writeShortLE(var1);
   }

   public ByteBuf writeMediumLE(int var1) {
      return this.wrapped.writeMediumLE(var1);
   }

   public ByteBuf writeIntLE(int var1) {
      return this.wrapped.writeIntLE(var1);
   }

   public ByteBuf writeLongLE(long var1) {
      return this.wrapped.writeLongLE(var1);
   }

   public int writeBytes(InputStream var1, int var2) throws IOException {
      return this.wrapped.writeBytes((InputStream)var1, var2);
   }

   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      return this.wrapped.writeBytes((ScatteringByteChannel)var1, var2);
   }

   public ByteBuf copy() {
      return this.wrapped.copy();
   }

   public CompositeByteBuf addComponent(ByteBuf var1) {
      this.wrapped.addComponent(var1);
      return this;
   }

   public CompositeByteBuf addComponents(ByteBuf... var1) {
      this.wrapped.addComponents(var1);
      return this;
   }

   public CompositeByteBuf addComponents(Iterable<ByteBuf> var1) {
      this.wrapped.addComponents(var1);
      return this;
   }

   public CompositeByteBuf addComponent(int var1, ByteBuf var2) {
      this.wrapped.addComponent(var1, var2);
      return this;
   }

   public CompositeByteBuf addComponents(int var1, ByteBuf... var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   public CompositeByteBuf addComponents(int var1, Iterable<ByteBuf> var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   public CompositeByteBuf addComponent(boolean var1, ByteBuf var2) {
      this.wrapped.addComponent(var1, var2);
      return this;
   }

   public CompositeByteBuf addComponents(boolean var1, ByteBuf... var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   public CompositeByteBuf addComponents(boolean var1, Iterable<ByteBuf> var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   public CompositeByteBuf addComponent(boolean var1, int var2, ByteBuf var3) {
      this.wrapped.addComponent(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf removeComponent(int var1) {
      this.wrapped.removeComponent(var1);
      return this;
   }

   public CompositeByteBuf removeComponents(int var1, int var2) {
      this.wrapped.removeComponents(var1, var2);
      return this;
   }

   public Iterator<ByteBuf> iterator() {
      return this.wrapped.iterator();
   }

   public List<ByteBuf> decompose(int var1, int var2) {
      return this.wrapped.decompose(var1, var2);
   }

   public final boolean isDirect() {
      return this.wrapped.isDirect();
   }

   public final boolean hasArray() {
      return this.wrapped.hasArray();
   }

   public final byte[] array() {
      return this.wrapped.array();
   }

   public final int arrayOffset() {
      return this.wrapped.arrayOffset();
   }

   public final boolean hasMemoryAddress() {
      return this.wrapped.hasMemoryAddress();
   }

   public final long memoryAddress() {
      return this.wrapped.memoryAddress();
   }

   public final int capacity() {
      return this.wrapped.capacity();
   }

   public CompositeByteBuf capacity(int var1) {
      this.wrapped.capacity(var1);
      return this;
   }

   public final ByteBufAllocator alloc() {
      return this.wrapped.alloc();
   }

   public final ByteOrder order() {
      return this.wrapped.order();
   }

   public final int numComponents() {
      return this.wrapped.numComponents();
   }

   public final int maxNumComponents() {
      return this.wrapped.maxNumComponents();
   }

   public final int toComponentIndex(int var1) {
      return this.wrapped.toComponentIndex(var1);
   }

   public final int toByteIndex(int var1) {
      return this.wrapped.toByteIndex(var1);
   }

   public byte getByte(int var1) {
      return this.wrapped.getByte(var1);
   }

   protected final byte _getByte(int var1) {
      return this.wrapped._getByte(var1);
   }

   protected final short _getShort(int var1) {
      return this.wrapped._getShort(var1);
   }

   protected final short _getShortLE(int var1) {
      return this.wrapped._getShortLE(var1);
   }

   protected final int _getUnsignedMedium(int var1) {
      return this.wrapped._getUnsignedMedium(var1);
   }

   protected final int _getUnsignedMediumLE(int var1) {
      return this.wrapped._getUnsignedMediumLE(var1);
   }

   protected final int _getInt(int var1) {
      return this.wrapped._getInt(var1);
   }

   protected final int _getIntLE(int var1) {
      return this.wrapped._getIntLE(var1);
   }

   protected final long _getLong(int var1) {
      return this.wrapped._getLong(var1);
   }

   protected final long _getLongLE(int var1) {
      return this.wrapped._getLongLE(var1);
   }

   public CompositeByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.wrapped.getBytes(var1, var2, var3, var4);
      return this;
   }

   public CompositeByteBuf getBytes(int var1, ByteBuffer var2) {
      this.wrapped.getBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.wrapped.getBytes(var1, var2, var3, var4);
      return this;
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.wrapped.getBytes(var1, var2, var3);
   }

   public CompositeByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.wrapped.getBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf setByte(int var1, int var2) {
      this.wrapped.setByte(var1, var2);
      return this;
   }

   protected final void _setByte(int var1, int var2) {
      this.wrapped._setByte(var1, var2);
   }

   public CompositeByteBuf setShort(int var1, int var2) {
      this.wrapped.setShort(var1, var2);
      return this;
   }

   protected final void _setShort(int var1, int var2) {
      this.wrapped._setShort(var1, var2);
   }

   protected final void _setShortLE(int var1, int var2) {
      this.wrapped._setShortLE(var1, var2);
   }

   public CompositeByteBuf setMedium(int var1, int var2) {
      this.wrapped.setMedium(var1, var2);
      return this;
   }

   protected final void _setMedium(int var1, int var2) {
      this.wrapped._setMedium(var1, var2);
   }

   protected final void _setMediumLE(int var1, int var2) {
      this.wrapped._setMediumLE(var1, var2);
   }

   public CompositeByteBuf setInt(int var1, int var2) {
      this.wrapped.setInt(var1, var2);
      return this;
   }

   protected final void _setInt(int var1, int var2) {
      this.wrapped._setInt(var1, var2);
   }

   protected final void _setIntLE(int var1, int var2) {
      this.wrapped._setIntLE(var1, var2);
   }

   public CompositeByteBuf setLong(int var1, long var2) {
      this.wrapped.setLong(var1, var2);
      return this;
   }

   protected final void _setLong(int var1, long var2) {
      this.wrapped._setLong(var1, var2);
   }

   protected final void _setLongLE(int var1, long var2) {
      this.wrapped._setLongLE(var1, var2);
   }

   public CompositeByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.wrapped.setBytes(var1, var2, var3, var4);
      return this;
   }

   public CompositeByteBuf setBytes(int var1, ByteBuffer var2) {
      this.wrapped.setBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.wrapped.setBytes(var1, var2, var3, var4);
      return this;
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return this.wrapped.setBytes(var1, var2, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      return this.wrapped.setBytes(var1, var2, var3);
   }

   public ByteBuf copy(int var1, int var2) {
      return this.wrapped.copy(var1, var2);
   }

   public final ByteBuf component(int var1) {
      return this.wrapped.component(var1);
   }

   public final ByteBuf componentAtOffset(int var1) {
      return this.wrapped.componentAtOffset(var1);
   }

   public final ByteBuf internalComponent(int var1) {
      return this.wrapped.internalComponent(var1);
   }

   public final ByteBuf internalComponentAtOffset(int var1) {
      return this.wrapped.internalComponentAtOffset(var1);
   }

   public int nioBufferCount() {
      return this.wrapped.nioBufferCount();
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.wrapped.internalNioBuffer(var1, var2);
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.wrapped.nioBuffer(var1, var2);
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.wrapped.nioBuffers(var1, var2);
   }

   public CompositeByteBuf consolidate() {
      this.wrapped.consolidate();
      return this;
   }

   public CompositeByteBuf consolidate(int var1, int var2) {
      this.wrapped.consolidate(var1, var2);
      return this;
   }

   public CompositeByteBuf discardReadComponents() {
      this.wrapped.discardReadComponents();
      return this;
   }

   public CompositeByteBuf discardReadBytes() {
      this.wrapped.discardReadBytes();
      return this;
   }

   public final String toString() {
      return this.wrapped.toString();
   }

   public final CompositeByteBuf readerIndex(int var1) {
      this.wrapped.readerIndex(var1);
      return this;
   }

   public final CompositeByteBuf writerIndex(int var1) {
      this.wrapped.writerIndex(var1);
      return this;
   }

   public final CompositeByteBuf setIndex(int var1, int var2) {
      this.wrapped.setIndex(var1, var2);
      return this;
   }

   public final CompositeByteBuf clear() {
      this.wrapped.clear();
      return this;
   }

   public final CompositeByteBuf markReaderIndex() {
      this.wrapped.markReaderIndex();
      return this;
   }

   public final CompositeByteBuf resetReaderIndex() {
      this.wrapped.resetReaderIndex();
      return this;
   }

   public final CompositeByteBuf markWriterIndex() {
      this.wrapped.markWriterIndex();
      return this;
   }

   public final CompositeByteBuf resetWriterIndex() {
      this.wrapped.resetWriterIndex();
      return this;
   }

   public CompositeByteBuf ensureWritable(int var1) {
      this.wrapped.ensureWritable(var1);
      return this;
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2) {
      this.wrapped.getBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      this.wrapped.getBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf getBytes(int var1, byte[] var2) {
      this.wrapped.getBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf setBoolean(int var1, boolean var2) {
      this.wrapped.setBoolean(var1, var2);
      return this;
   }

   public CompositeByteBuf setChar(int var1, int var2) {
      this.wrapped.setChar(var1, var2);
      return this;
   }

   public CompositeByteBuf setFloat(int var1, float var2) {
      this.wrapped.setFloat(var1, var2);
      return this;
   }

   public CompositeByteBuf setDouble(int var1, double var2) {
      this.wrapped.setDouble(var1, var2);
      return this;
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2) {
      this.wrapped.setBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      this.wrapped.setBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf setBytes(int var1, byte[] var2) {
      this.wrapped.setBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf setZero(int var1, int var2) {
      this.wrapped.setZero(var1, var2);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf var1) {
      this.wrapped.readBytes(var1);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf var1, int var2) {
      this.wrapped.readBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      this.wrapped.readBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf readBytes(byte[] var1) {
      this.wrapped.readBytes(var1);
      return this;
   }

   public CompositeByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.wrapped.readBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuffer var1) {
      this.wrapped.readBytes(var1);
      return this;
   }

   public CompositeByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.wrapped.readBytes(var1, var2);
      return this;
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.wrapped.getBytes(var1, var2, var3, var5);
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.wrapped.setBytes(var1, var2, var3, var5);
   }

   public boolean isReadOnly() {
      return this.wrapped.isReadOnly();
   }

   public ByteBuf asReadOnly() {
      return this.wrapped.asReadOnly();
   }

   protected SwappedByteBuf newSwappedByteBuf() {
      return this.wrapped.newSwappedByteBuf();
   }

   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      return this.wrapped.getCharSequence(var1, var2, var3);
   }

   public CharSequence readCharSequence(int var1, Charset var2) {
      return this.wrapped.readCharSequence(var1, var2);
   }

   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      return this.wrapped.setCharSequence(var1, var2, var3);
   }

   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.wrapped.readBytes(var1, var2, var4);
   }

   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.wrapped.writeBytes(var1, var2, var4);
   }

   public int writeCharSequence(CharSequence var1, Charset var2) {
      return this.wrapped.writeCharSequence(var1, var2);
   }

   public CompositeByteBuf skipBytes(int var1) {
      this.wrapped.skipBytes(var1);
      return this;
   }

   public CompositeByteBuf writeBoolean(boolean var1) {
      this.wrapped.writeBoolean(var1);
      return this;
   }

   public CompositeByteBuf writeByte(int var1) {
      this.wrapped.writeByte(var1);
      return this;
   }

   public CompositeByteBuf writeShort(int var1) {
      this.wrapped.writeShort(var1);
      return this;
   }

   public CompositeByteBuf writeMedium(int var1) {
      this.wrapped.writeMedium(var1);
      return this;
   }

   public CompositeByteBuf writeInt(int var1) {
      this.wrapped.writeInt(var1);
      return this;
   }

   public CompositeByteBuf writeLong(long var1) {
      this.wrapped.writeLong(var1);
      return this;
   }

   public CompositeByteBuf writeChar(int var1) {
      this.wrapped.writeChar(var1);
      return this;
   }

   public CompositeByteBuf writeFloat(float var1) {
      this.wrapped.writeFloat(var1);
      return this;
   }

   public CompositeByteBuf writeDouble(double var1) {
      this.wrapped.writeDouble(var1);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf var1) {
      this.wrapped.writeBytes(var1);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf var1, int var2) {
      this.wrapped.writeBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      this.wrapped.writeBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf writeBytes(byte[] var1) {
      this.wrapped.writeBytes(var1);
      return this;
   }

   public CompositeByteBuf writeBytes(byte[] var1, int var2, int var3) {
      this.wrapped.writeBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuffer var1) {
      this.wrapped.writeBytes(var1);
      return this;
   }

   public CompositeByteBuf writeZero(int var1) {
      this.wrapped.writeZero(var1);
      return this;
   }

   public CompositeByteBuf retain(int var1) {
      this.wrapped.retain(var1);
      return this;
   }

   public CompositeByteBuf retain() {
      this.wrapped.retain();
      return this;
   }

   public CompositeByteBuf touch() {
      this.wrapped.touch();
      return this;
   }

   public CompositeByteBuf touch(Object var1) {
      this.wrapped.touch(var1);
      return this;
   }

   public ByteBuffer[] nioBuffers() {
      return this.wrapped.nioBuffers();
   }

   public CompositeByteBuf discardSomeReadBytes() {
      this.wrapped.discardSomeReadBytes();
      return this;
   }

   public final void deallocate() {
      this.wrapped.deallocate();
   }

   public final ByteBuf unwrap() {
      return this.wrapped;
   }
}
