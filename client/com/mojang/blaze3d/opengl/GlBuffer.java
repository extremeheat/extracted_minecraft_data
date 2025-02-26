package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class GlBuffer extends GpuBuffer {
   protected static final MemoryPool MEMORY_POOl = TracyClient.createMemoryPool("GPU Buffers");
   protected boolean closed;
   protected boolean initialized = false;
   @Nullable
   protected final Supplier<String> label;
   protected final int handle;

   protected GlBuffer(GlDebugLabel var1, @Nullable Supplier<String> var2, BufferType var3, BufferUsage var4, int var5, int var6) {
      super(var3, var4, var5);
      this.label = var2;
      this.handle = var6;
      if (var4.isReadable()) {
         GlStateManager._glBindBuffer(GlConst.toGl(var3), var6);
         GlStateManager._glBufferData(GlConst.toGl(var3), (long)var5, GlConst.toGl(var4));
         MEMORY_POOl.malloc((long)var6, var5);
         this.initialized = true;
         var1.applyLabel(this);
      }

   }

   protected void ensureBufferExists() {
      if (!this.initialized) {
         GlStateManager._glBindBuffer(GlConst.toGl(this.type()), this.handle);
         GlStateManager._glBindBuffer(GlConst.toGl(this.type()), 0);
      }

   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         GlStateManager._glDeleteBuffers(this.handle);
         if (this.initialized) {
            MEMORY_POOl.free((long)this.handle);
         }

      }
   }

   public static class ReadView implements GpuBuffer.ReadView {
      private final int target;
      private final ByteBuffer data;

      protected ReadView(int var1, ByteBuffer var2) {
         super();
         this.target = var1;
         this.data = var2;
      }

      public ByteBuffer data() {
         return this.data;
      }

      public void close() {
         GlStateManager._glUnmapBuffer(this.target);
      }
   }
}
