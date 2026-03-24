package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public abstract class BufferStorage {
   public BufferStorage() {
      super();
   }

   public static BufferStorage create(final GLCapabilities capabilities, final Set<String> enabledExtensions) {
      if (capabilities.GL_ARB_buffer_storage && GlDevice.USE_GL_ARB_buffer_storage) {
         enabledExtensions.add("GL_ARB_buffer_storage");
         return new Immutable();
      } else {
         return new Mutable();
      }
   }

   public abstract GlBuffer createBuffer(DirectStateAccess dsa, @Nullable Supplier<String> label, @GpuBuffer.Usage int usage, long size);

   public abstract GlBuffer createBuffer(DirectStateAccess dsa, @Nullable Supplier<String> label, @GpuBuffer.Usage int usage, ByteBuffer data);

   public abstract GlBuffer.GlMappedView mapBuffer(DirectStateAccess dsa, GlBuffer buffer, long offset, long length, int flags);

   private static class Mutable extends BufferStorage {
      private Mutable() {
         super();
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final long size) {
         int buffer = dsa.createBuffer();
         dsa.bufferData(buffer, size, usage);
         return new GlBuffer(label, dsa, usage, size, buffer, (ByteBuffer)null);
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final ByteBuffer data) {
         int buffer = dsa.createBuffer();
         int size = data.remaining();
         dsa.bufferData(buffer, data, usage);
         return new GlBuffer(label, dsa, usage, (long)size, buffer, (ByteBuffer)null);
      }

      public GlBuffer.GlMappedView mapBuffer(final DirectStateAccess dsa, final GlBuffer buffer, final long offset, final long length, final int flags) {
         GlStateManager.clearGlErrors();
         ByteBuffer byteBuffer = dsa.mapBufferRange(buffer.handle, offset, length, flags, buffer.usage());
         if (byteBuffer == null) {
            throw new IllegalStateException("Can't map buffer, opengl error " + GlStateManager._getError());
         } else {
            return new GlBuffer.GlMappedView(() -> dsa.unmapBuffer(buffer.handle, buffer.usage()), buffer, byteBuffer);
         }
      }
   }

   private static class Immutable extends BufferStorage {
      private Immutable() {
         super();
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final long size) {
         int buffer = dsa.createBuffer();
         dsa.bufferStorage(buffer, size, usage);
         ByteBuffer persistentBuffer = this.tryMapBufferPersistent(dsa, usage, buffer, size);
         return new GlBuffer(label, dsa, usage, size, buffer, persistentBuffer);
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final ByteBuffer data) {
         int buffer = dsa.createBuffer();
         int size = data.remaining();
         dsa.bufferStorage(buffer, data, usage);
         ByteBuffer persistentBuffer = this.tryMapBufferPersistent(dsa, usage, buffer, (long)size);
         return new GlBuffer(label, dsa, usage, (long)size, buffer, persistentBuffer);
      }

      private @Nullable ByteBuffer tryMapBufferPersistent(final DirectStateAccess dsa, final @GpuBuffer.Usage int usage, final int buffer, final long size) {
         int mapFlags = 0;
         if ((usage & 1) != 0) {
            mapFlags |= 1;
         }

         if ((usage & 2) != 0) {
            mapFlags |= 18;
         }

         ByteBuffer persistentBuffer;
         if (mapFlags != 0) {
            GlStateManager.clearGlErrors();
            persistentBuffer = dsa.mapBufferRange(buffer, 0L, size, mapFlags | 64, usage);
            if (persistentBuffer == null) {
               throw new IllegalStateException("Can't persistently map buffer, opengl error " + GlStateManager._getError());
            }
         } else {
            persistentBuffer = null;
         }

         return persistentBuffer;
      }

      public GlBuffer.GlMappedView mapBuffer(final DirectStateAccess dsa, final GlBuffer buffer, final long offset, final long length, final int flags) {
         if (buffer.persistentBuffer == null) {
            throw new IllegalStateException("Somehow trying to map an unmappable buffer");
         } else if (offset <= 2147483647L && length <= 2147483647L) {
            if (offset >= 0L && length >= 0L) {
               return new GlBuffer.GlMappedView(() -> {
                  if ((flags & 2) != 0) {
                     dsa.flushMappedBufferRange(buffer.handle, offset, length, buffer.usage());
                  }

               }, buffer, MemoryUtil.memSlice(buffer.persistentBuffer, (int)offset, (int)length));
            } else {
               throw new IllegalArgumentException("Offset or length must be positive integer values");
            }
         } else {
            throw new IllegalArgumentException("Mapping buffers larger than 2GB is not supported");
         }
      }
   }
}
