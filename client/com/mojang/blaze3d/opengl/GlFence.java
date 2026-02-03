package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuFence;

public class GlFence implements GpuFence {
   private long handle = GlStateManager._glFenceSync(37143, 0);

   public GlFence() {
      super();
   }

   public void close() {
      if (this.handle != 0L) {
         GlStateManager._glDeleteSync(this.handle);
         this.handle = 0L;
      }

   }

   public boolean awaitCompletion(final long timeoutMs) {
      if (this.handle == 0L) {
         return true;
      } else {
         int result = GlStateManager._glClientWaitSync(this.handle, 0, timeoutMs);
         if (result == 37147) {
            return false;
         } else if (result == 37149) {
            throw new IllegalStateException("Failed to complete GPU fence: " + GlStateManager._getError());
         } else {
            return true;
         }
      }
   }
}
