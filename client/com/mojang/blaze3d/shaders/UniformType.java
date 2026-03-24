package com.mojang.blaze3d.shaders;

public enum UniformType {
   UNIFORM_BUFFER("ubo"),
   TEXEL_BUFFER("utb");

   final String name;

   private UniformType(final String name) {
      this.name = name;
   }

   // $FF: synthetic method
   private static UniformType[] $values() {
      return new UniformType[]{UNIFORM_BUFFER, TEXEL_BUFFER};
   }
}
