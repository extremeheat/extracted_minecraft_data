package com.mojang.blaze3d.vertex;

import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class ByteBufferBuilder implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);
   private static final int MAX_GROWTH_SIZE = 2097152;
   private static final int BUFFER_FREED_GENERATION = -1;
   long pointer;
   private int capacity;
   private int writeOffset;
   private int nextResultOffset;
   private int resultCount;
   private int generation;

   public ByteBufferBuilder(int var1) {
      super();
      this.capacity = var1;
      this.pointer = ALLOCATOR.malloc((long)var1);
      if (this.pointer == 0L) {
         throw new OutOfMemoryError("Failed to allocate " + var1 + " bytes");
      }
   }

   public long reserve(int var1) {
      int var2 = this.writeOffset;
      int var3 = var2 + var1;
      this.ensureCapacity(var3);
      this.writeOffset = var3;
      return this.pointer + (long)var2;
   }

   private void ensureCapacity(int var1) {
      if (var1 > this.capacity) {
         int var2 = Math.min(this.capacity, 2097152);
         int var3 = Math.max(this.capacity + var2, var1);
         this.resize(var3);
      }

   }

   private void resize(int var1) {
      this.pointer = ALLOCATOR.realloc(this.pointer, (long)var1);
      LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", this.capacity, var1);
      if (this.pointer == 0L) {
         throw new OutOfMemoryError("Failed to resize buffer from " + this.capacity + " bytes to " + var1 + " bytes");
      } else {
         this.capacity = var1;
      }
   }

   @Nullable
   public Result build() {
      this.checkOpen();
      int var1 = this.nextResultOffset;
      int var2 = this.writeOffset - var1;
      if (var2 == 0) {
         return null;
      } else {
         this.nextResultOffset = this.writeOffset;
         ++this.resultCount;
         return new Result(var1, var2, this.generation);
      }
   }

   public void clear() {
      if (this.resultCount > 0) {
         LOGGER.warn("Clearing BufferBuilder with unused batches");
      }

      this.discard();
   }

   public void discard() {
      this.checkOpen();
      if (this.resultCount > 0) {
         this.discardResults();
         this.resultCount = 0;
      }

   }

   boolean isValid(int var1) {
      return var1 == this.generation;
   }

   void freeResult() {
      if (--this.resultCount <= 0) {
         this.discardResults();
      }

   }

   private void discardResults() {
      int var1 = this.writeOffset - this.nextResultOffset;
      if (var1 > 0) {
         MemoryUtil.memCopy(this.pointer + (long)this.nextResultOffset, this.pointer, (long)var1);
      }

      this.writeOffset = var1;
      this.nextResultOffset = 0;
      ++this.generation;
   }

   public void close() {
      if (this.pointer != 0L) {
         ALLOCATOR.free(this.pointer);
         this.pointer = 0L;
         this.generation = -1;
      }

   }

   private void checkOpen() {
      if (this.pointer == 0L) {
         throw new IllegalStateException("Buffer has been freed");
      }
   }

   public class Result implements AutoCloseable {
      private final int offset;
      private final int capacity;
      private final int generation;
      private boolean closed;

      Result(final int var2, final int var3, final int var4) {
         super();
         this.offset = var2;
         this.capacity = var3;
         this.generation = var4;
      }

      public ByteBuffer byteBuffer() {
         if (!ByteBufferBuilder.this.isValid(this.generation)) {
            throw new IllegalStateException("Buffer is no longer valid");
         } else {
            return MemoryUtil.memByteBuffer(ByteBufferBuilder.this.pointer + (long)this.offset, this.capacity);
         }
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            if (ByteBufferBuilder.this.isValid(this.generation)) {
               ByteBufferBuilder.this.freeResult();
            }

         }
      }
   }
}
