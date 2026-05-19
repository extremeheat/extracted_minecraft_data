package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public abstract class GlBuffer extends GpuBuffer {
   protected static final MemoryPool MEMORY_POOL = TracyClient.createMemoryPool("GPU Buffers");
   private final int handle;
   protected final boolean canPersistentMap;
   protected int mappingRefCount = 0;

   protected GlBuffer(final @GpuBuffer.Usage int usage, final long size, final int handle, final boolean canPersistentMap) {
      super(usage, size);
      this.handle = handle;
      this.canPersistentMap = canPersistentMap;
   }

   public int handle() {
      return this.handle;
   }

   protected void checkCanBeUsed() {
      if (!this.canPersistentMap) {
         if (this.mappingRefCount != 0) {
            throw new IllegalStateException("Attempt to use buffer while mapped without persistent mapping capability");
         }
      }
   }

   public static class Direct extends GlBuffer {
      private boolean closed;
      private final DirectStateAccess dsa;
      protected final int mappingFlags;
      protected @Nullable ByteBuffer mappedBuffer;

      protected Direct(final DirectStateAccess dsa, final @GpuBuffer.Usage int usage, final long size, final int handle, final boolean canPersistentMap) {
         this.dsa = dsa;
         int clampedSize = (int)Math.min(size, 2147483647L);
         MEMORY_POOL.malloc((long)handle, clampedSize);
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
         super(usage, size, handle, canPersistentMap);
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
               GlStateManager._glDeleteBuffers(this.handle());
               MEMORY_POOL.free((long)this.handle());
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
                  this.mappedBuffer = this.dsa.mapBufferRange(this.handle(), 0L, this.size(), this.mappingFlags, this.usage());
                  if (this.mappedBuffer == null) {
                     throw new IllegalStateException("Failed to map buffer");
                  }
               }

               return new GpuBufferSlice.MappedView(this.slice(offset, length), MemoryUtil.memSlice(this.mappedBuffer, (int)offset, (int)length), new Runnable() {
                  private boolean closed;

                  {
                     Objects.requireNonNull(Direct.this);
                     this.closed = false;
                  }

                  public void run() {
                     if (!this.closed) {
                        this.closed = true;
                        if ((Direct.this.mappingFlags & 16) != 0) {
                           Direct.this.dsa.flushMappedBufferRange(Direct.this.handle(), Direct.this.slice().offset(), Direct.this.slice().length(), Direct.this.usage());
                        }

                        Direct.this.unmap();
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
            this.dsa.unmapBuffer(this.handle(), this.usage());
            this.mappedBuffer = null;
         }

      }
   }
}
