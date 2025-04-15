package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public interface GpuFence extends AutoCloseable {
   void close();

   boolean awaitCompletion(long var1);
}
