package com.mojang.blaze3d.pipeline;

public interface CompiledRenderPipeline extends AutoCloseable {
   RenderPipeline info();

   boolean isClosed();

   void close();
}
