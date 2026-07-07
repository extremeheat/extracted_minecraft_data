package com.mojang.renderpearl.api.pipeline;

public enum UniformType {
   UNIFORM_BUFFER,
   TEXEL_BUFFER;

   private UniformType() {
   }

   // $FF: synthetic method
   private static UniformType[] $values() {
      return new UniformType[]{UNIFORM_BUFFER, TEXEL_BUFFER};
   }
}
