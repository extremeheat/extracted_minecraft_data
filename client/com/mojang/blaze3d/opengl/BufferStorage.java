package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GLCapabilities;

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

   private static class Mutable extends BufferStorage {
      private Mutable() {
         super();
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final long size) {
         int buffer = dsa.createBuffer();
         dsa.bufferData(buffer, size, usage);
         return new GlBuffer(label, dsa, usage, size, buffer, false);
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final ByteBuffer data) {
         int buffer = dsa.createBuffer();
         int size = data.remaining();
         dsa.bufferData(buffer, data, usage);
         return new GlBuffer(label, dsa, usage, (long)size, buffer, false);
      }
   }

   private static class Immutable extends BufferStorage {
      private Immutable() {
         super();
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final long size) {
         int buffer = dsa.createBuffer();
         dsa.bufferStorage(buffer, size, usage);
         return new GlBuffer(label, dsa, usage, size, buffer, true);
      }

      public GlBuffer createBuffer(final DirectStateAccess dsa, final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final ByteBuffer data) {
         int buffer = dsa.createBuffer();
         int size = data.remaining();
         dsa.bufferStorage(buffer, data, usage);
         return new GlBuffer(label, dsa, usage, (long)size, buffer, true);
      }
   }
}
