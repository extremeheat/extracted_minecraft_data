package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class PreferHeapByteBufAllocator implements ByteBufAllocator {
   private final ByteBufAllocator allocator;

   public PreferHeapByteBufAllocator(ByteBufAllocator var1) {
      super();
      this.allocator = (ByteBufAllocator)ObjectUtil.checkNotNull(var1, "allocator");
   }

   public ByteBuf buffer() {
      return this.allocator.heapBuffer();
   }

   public ByteBuf buffer(int var1) {
      return this.allocator.heapBuffer(var1);
   }

   public ByteBuf buffer(int var1, int var2) {
      return this.allocator.heapBuffer(var1, var2);
   }

   public ByteBuf ioBuffer() {
      return this.allocator.heapBuffer();
   }

   public ByteBuf ioBuffer(int var1) {
      return this.allocator.heapBuffer(var1);
   }

   public ByteBuf ioBuffer(int var1, int var2) {
      return this.allocator.heapBuffer(var1, var2);
   }

   public ByteBuf heapBuffer() {
      return this.allocator.heapBuffer();
   }

   public ByteBuf heapBuffer(int var1) {
      return this.allocator.heapBuffer(var1);
   }

   public ByteBuf heapBuffer(int var1, int var2) {
      return this.allocator.heapBuffer(var1, var2);
   }

   public ByteBuf directBuffer() {
      return this.allocator.directBuffer();
   }

   public ByteBuf directBuffer(int var1) {
      return this.allocator.directBuffer(var1);
   }

   public ByteBuf directBuffer(int var1, int var2) {
      return this.allocator.directBuffer(var1, var2);
   }

   public CompositeByteBuf compositeBuffer() {
      return this.allocator.compositeHeapBuffer();
   }

   public CompositeByteBuf compositeBuffer(int var1) {
      return this.allocator.compositeHeapBuffer(var1);
   }

   public CompositeByteBuf compositeHeapBuffer() {
      return this.allocator.compositeHeapBuffer();
   }

   public CompositeByteBuf compositeHeapBuffer(int var1) {
      return this.allocator.compositeHeapBuffer(var1);
   }

   public CompositeByteBuf compositeDirectBuffer() {
      return this.allocator.compositeDirectBuffer();
   }

   public CompositeByteBuf compositeDirectBuffer(int var1) {
      return this.allocator.compositeDirectBuffer(var1);
   }

   public boolean isDirectBufferPooled() {
      return this.allocator.isDirectBufferPooled();
   }

   public int calculateNewCapacity(int var1, int var2) {
      return this.allocator.calculateNewCapacity(var1, var2);
   }
}
