package com.mojang.blaze3d.buffers;

public interface GpuFence extends AutoCloseable {
   void close();

   boolean awaitCompletion(final long timeoutMs);
}
