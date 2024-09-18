package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.platform.GlStateManager;

public class GpuFence implements AutoCloseable {
   private long handle = GlStateManager._glFenceSync(37143, 0);

   public GpuFence() {
      super();
   }

   @Override
   public void close() {
      if (this.handle != 0L) {
         GlStateManager._glDeleteSync(this.handle);
         this.handle = 0L;
      }
   }

   public boolean awaitCompletion(long var1) {
      if (this.handle == 0L) {
         return true;
      } else {
         int var3 = GlStateManager._glClientWaitSync(this.handle, 0, var1);
         if (var3 == 37147) {
            return false;
         } else if (var3 == 37149) {
            throw new IllegalStateException("Failed to complete gpu fence");
         } else {
            return true;
         }
      }
   }
}
