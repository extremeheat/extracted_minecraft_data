package com.mojang.blaze3d.opengl;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public abstract class BufferStorage {
   public BufferStorage() {
      super();
   }

   public static BufferStorage create(GLCapabilities var0, Set<String> var1) {
      if (var0.GL_ARB_buffer_storage && GlDevice.USE_GL_ARB_buffer_storage) {
         var1.add("GL_ARB_buffer_storage");
         return new Immutable();
      } else {
         return new Mutable();
      }
   }

   public abstract GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, int var4);

   public abstract GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, ByteBuffer var4);

   public abstract GlBuffer.GlMappedView mapBuffer(DirectStateAccess var1, GlBuffer var2, int var3, int var4, int var5);

   static class Mutable extends BufferStorage {
      Mutable() {
         super();
      }

      public GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, int var4) {
         int var5 = var1.createBuffer();
         var1.bufferData(var5, (long)var4, var3);
         return new GlBuffer(var2, var1, var3, var4, var5, (ByteBuffer)null);
      }

      public GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, ByteBuffer var4) {
         int var5 = var1.createBuffer();
         int var6 = var4.remaining();
         var1.bufferData(var5, var4, var3);
         return new GlBuffer(var2, var1, var3, var6, var5, (ByteBuffer)null);
      }

      public GlBuffer.GlMappedView mapBuffer(DirectStateAccess var1, GlBuffer var2, int var3, int var4, int var5) {
         GlStateManager.clearGlErrors();
         ByteBuffer var6 = var1.mapBufferRange(var2.handle, var3, var4, var5, var2.usage());
         if (var6 == null) {
            throw new IllegalStateException("Can't map buffer, opengl error " + GlStateManager._getError());
         } else {
            return new GlBuffer.GlMappedView(() -> var1.unmapBuffer(var2.handle, var2.usage()), var2, var6);
         }
      }
   }

   static class Immutable extends BufferStorage {
      Immutable() {
         super();
      }

      public GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, int var4) {
         int var5 = var1.createBuffer();
         var1.bufferStorage(var5, (long)var4, var3);
         ByteBuffer var6 = this.tryMapBufferPersistent(var1, var3, var5, var4);
         return new GlBuffer(var2, var1, var3, var4, var5, var6);
      }

      public GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, ByteBuffer var4) {
         int var5 = var1.createBuffer();
         int var6 = var4.remaining();
         var1.bufferStorage(var5, var4, var3);
         ByteBuffer var7 = this.tryMapBufferPersistent(var1, var3, var5, var6);
         return new GlBuffer(var2, var1, var3, var6, var5, var7);
      }

      @Nullable
      private ByteBuffer tryMapBufferPersistent(DirectStateAccess var1, int var2, int var3, int var4) {
         int var6 = 0;
         if ((var2 & 1) != 0) {
            var6 |= 1;
         }

         if ((var2 & 2) != 0) {
            var6 |= 18;
         }

         ByteBuffer var5;
         if (var6 != 0) {
            GlStateManager.clearGlErrors();
            var5 = var1.mapBufferRange(var3, 0, var4, var6 | 64, var2);
            if (var5 == null) {
               throw new IllegalStateException("Can't persistently map buffer, opengl error " + GlStateManager._getError());
            }
         } else {
            var5 = null;
         }

         return var5;
      }

      public GlBuffer.GlMappedView mapBuffer(DirectStateAccess var1, GlBuffer var2, int var3, int var4, int var5) {
         if (var2.persistentBuffer == null) {
            throw new IllegalStateException("Somehow trying to map an unmappable buffer");
         } else {
            return new GlBuffer.GlMappedView(() -> {
               if ((var5 & 2) != 0) {
                  var1.flushMappedBufferRange(var2.handle, var3, var4, var2.usage());
               }

            }, var2, MemoryUtil.memSlice(var2.persistentBuffer, var3, var4));
         }
      }
   }
}
