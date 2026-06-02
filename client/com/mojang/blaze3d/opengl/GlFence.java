package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuFence;

public class GlFence implements GpuFence {
   private final GlCommandEncoder encoder;
   private final long submitIndex;
   private boolean closedOrCompleted;

   GlFence(final GlCommandEncoder encoder) {
      super();
      this.encoder = encoder;
      this.submitIndex = encoder.currentSubmitIndex();
   }

   public void close() {
      this.closedOrCompleted = true;
   }

   public boolean awaitCompletion(final long timeoutNS) {
      if (this.closedOrCompleted) {
         return true;
      } else {
         this.closedOrCompleted = this.encoder.awaitSubmit(this.submitIndex, timeoutNS);
         return this.closedOrCompleted;
      }
   }
}
