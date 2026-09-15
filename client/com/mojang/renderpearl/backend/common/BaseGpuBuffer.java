package com.mojang.renderpearl.backend.common;

import com.mojang.renderpearl.api.buffers.GpuBuffer;

public abstract class BaseGpuBuffer implements GpuBuffer {
   private final @GpuBuffer.Usage int usage;
   private final long size;

   public BaseGpuBuffer(final @GpuBuffer.Usage int usage, final long size) {
      super();
      this.size = size;
      this.usage = usage;
   }

   public long size() {
      return this.size;
   }

   public @GpuBuffer.Usage int usage() {
      return this.usage;
   }

   public void checkCanBeUsed() {
   }
}
