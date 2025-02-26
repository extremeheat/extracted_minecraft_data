package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;

public record GlRenderPipeline(RenderPipeline info, GlProgram program) implements CompiledRenderPipeline {
   public GlRenderPipeline(RenderPipeline var1, GlProgram var2) {
      super();
      this.info = var1;
      this.program = var2;
   }

   public boolean containsUniform(String var1) {
      return this.program.getUniform(var1) != null;
   }

   public boolean isValid() {
      return this.program != GlProgram.INVALID_PROGRAM;
   }
}
