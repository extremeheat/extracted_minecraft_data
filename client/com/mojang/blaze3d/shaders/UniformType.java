package com.mojang.blaze3d.shaders;

public enum UniformType {
   UNIFORM_BUFFER("ubo"),
   TEXEL_BUFFER("utb");

   final String name;

   private UniformType(final String var3) {
      this.name = var3;
   }

   // $FF: synthetic method
   private static UniformType[] $values() {
      return new UniformType[]{UNIFORM_BUFFER, TEXEL_BUFFER};
   }
}
