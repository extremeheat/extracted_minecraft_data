package com.mojang.renderpearl.api.pipeline;

public enum UniformType {
   COMBINED_IMAGE_SAMPLER,
   UNIFORM_BUFFER,
   TEXEL_BUFFER;

   private UniformType() {
   }

   // $FF: synthetic method
   private static UniformType[] $values() {
      return new UniformType[]{COMBINED_IMAGE_SAMPLER, UNIFORM_BUFFER, TEXEL_BUFFER};
   }
}
