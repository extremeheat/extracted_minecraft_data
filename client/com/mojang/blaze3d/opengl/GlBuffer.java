package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class GlBuffer extends GpuBuffer {
   protected static final MemoryPool MEMORY_POOl = TracyClient.createMemoryPool("GPU Buffers");
   protected boolean closed;
   protected final @Nullable Supplier<String> label;
   private final DirectStateAccess dsa;
   protected final int handle;
   protected final boolean canPersistentMap;
   protected final int mappingFlags;
   protected int mappingRefCount = 0;
   protected @Nullable ByteBuffer mappedBuffer;

   protected GlBuffer(final @Nullable Supplier<String> label, final DirectStateAccess dsa, final @GpuBuffer.Usage int usage, final long size, final int handle, final boolean canPersistentMap) {
      super(usage, size);
      this.label = label;
      this.dsa = dsa;
      this.handle = handle;
      int clampedSize = (int)Math.min(size, 2147483647L);
      MEMORY_POOl.malloc((long)handle, clampedSize);
      int mappingFlags = 0;
      if ((usage & 1) != 0) {
         mappingFlags |= 1;
      }

      if ((usage & 2) != 0) {
         mappingFlags |= 50;
      }

      if (canPersistentMap) {
         mappingFlags |= 64;
      }

      this.mappingFlags = mappingFlags;
      this.canPersistentMap = canPersistentMap;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.mappingRefCount != 0) {
            throw new IllegalStateException("Attempt to close a mapped buffer");
         } else {
            GlStateManager._glDeleteBuffers(this.handle);
            MEMORY_POOl.free((long)this.handle);
         }
      }
   }

   public GpuBufferSlice.MappedView map(final long offset, final long length, final boolean read, final boolean write) {
      if (this.isClosed()) {
         throw new IllegalStateException("Buffer already closed");
      } else if (!read && !write) {
         throw new IllegalArgumentException("At least read or write must be true");
      } else if (read && (this.usage() & 1) == 0) {
         throw new IllegalStateException("Buffer is not readable");
      } else if (write && (this.usage() & 2) == 0) {
         throw new IllegalStateException("Buffer is not writable");
      } else if (offset + length > this.size()) {
         throw new IllegalArgumentException("Cannot map more data than this buffer can hold (attempting to map " + length + " bytes at offset " + offset + " from " + this.size() + " size buffer)");
      } else if (offset <= 2147483647L && length <= 2147483647L) {
         if (offset >= 0L && length >= 0L) {
            ++this.mappingRefCount;
            if (this.mappedBuffer == null) {
               GlStateManager.clearGlErrors();
               this.mappedBuffer = this.dsa.mapBufferRange(this.handle, 0L, this.size(), this.mappingFlags, this.usage());
               if (this.mappedBuffer == null) {
                  throw new IllegalStateException("Failed to map buffer");
               }
            }

            return new GpuBufferSlice.MappedView(this.slice(offset, length), MemoryUtil.memSlice(this.mappedBuffer, (int)offset, (int)length), new Runnable() {
               private boolean closed;

               {
                  Objects.requireNonNull(GlBuffer.this);
                  this.closed = false;
               }

               public void run() {
                  if (!this.closed) {
                     this.closed = true;
                     GlBuffer.this.dsa.flushMappedBufferRange(GlBuffer.this.handle, GlBuffer.this.slice().offset(), GlBuffer.this.slice().length(), GlBuffer.this.usage());
                     GlBuffer.this.unmap();
                  }
               }
            });
         } else {
            throw new IllegalArgumentException("Offset or length must be positive integer values");
         }
      } else {
         throw new IllegalArgumentException("Mapping buffers larger than 2GB is not supported");
      }
   }

   private void unmap() {
      --this.mappingRefCount;
      if (this.mappingRefCount == 0) {
         this.dsa.unmapBuffer(this.handle, this.usage());
         this.mappedBuffer = null;
      }

   }

   protected void checkCanBeUsed() {
      if (!this.canPersistentMap) {
         if (this.mappingRefCount != 0) {
            throw new IllegalStateException("Attempt to use buffer while mapped without persistent mapping capability");
         }
      }
   }
}
