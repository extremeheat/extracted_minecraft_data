package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class GlBuffer extends GpuBuffer {
   protected static final MemoryPool MEMORY_POOl = TracyClient.createMemoryPool("GPU Buffers");
   protected boolean closed;
   protected final @Nullable Supplier<String> label;
   private final DirectStateAccess dsa;
   protected final int handle;
   protected @Nullable ByteBuffer persistentBuffer;

   protected GlBuffer(@Nullable Supplier<String> var1, DirectStateAccess var2, @GpuBuffer.Usage int var3, long var4, int var6, @Nullable ByteBuffer var7) {
      super(var3, var4);
      this.label = var1;
      this.dsa = var2;
      this.handle = var6;
      this.persistentBuffer = var7;
      int var8 = (int)Math.min(var4, 2147483647L);
      MEMORY_POOl.malloc((long)var6, var8);
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.persistentBuffer != null) {
            this.dsa.unmapBuffer(this.handle, this.usage());
            this.persistentBuffer = null;
         }

         GlStateManager._glDeleteBuffers(this.handle);
         MEMORY_POOl.free((long)this.handle);
      }
   }

   public static class GlMappedView implements GpuBuffer.MappedView {
      private final Runnable unmap;
      private final GlBuffer buffer;
      private final ByteBuffer data;
      private boolean closed;

      protected GlMappedView(Runnable var1, GlBuffer var2, ByteBuffer var3) {
         super();
         this.unmap = var1;
         this.buffer = var2;
         this.data = var3;
      }

      public ByteBuffer data() {
         return this.data;
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            this.unmap.run();
         }
      }
   }
}
