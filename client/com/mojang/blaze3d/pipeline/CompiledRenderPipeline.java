package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public interface CompiledRenderPipeline {
   boolean containsUniform(String var1);

   boolean isValid();
}
