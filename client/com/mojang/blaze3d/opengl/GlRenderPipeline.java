package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;

public record GlRenderPipeline(RenderPipeline info, GlProgram program) implements CompiledRenderPipeline {
   public GlRenderPipeline {
      super();
   }

   public boolean isValid() {
      return this.program != GlProgram.INVALID_PROGRAM;
   }
}
