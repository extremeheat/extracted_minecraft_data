package com.mojang.renderpearl.api.commands;

public interface GpuFence extends AutoCloseable {
   void close();

   boolean awaitCompletion(final long timeoutNS);
}
