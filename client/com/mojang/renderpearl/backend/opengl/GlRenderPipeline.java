package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

public final class GlRenderPipeline implements CompiledRenderPipeline {
   private final GlDevice device;
   private final RenderPipeline info;
   private final GlProgram program;
   private boolean closed = false;

   GlRenderPipeline(final GlDevice device, final RenderPipeline info, final GlProgram program) {
      super();
      this.device = device;
      this.info = info;
      this.program = program;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.program.close();
         this.device.markAmdShaderCompilerAngry();
      }
   }

   public RenderPipeline info() {
      return this.info;
   }

   public GlProgram program() {
      return this.program;
   }
}
