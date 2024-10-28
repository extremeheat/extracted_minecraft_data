package com.mojang.blaze3d.shaders;

public enum FogShape {
   SPHERE(0),
   CYLINDER(1);

   private final int index;

   private FogShape(final int var3) {
      this.index = var3;
   }

   public int getIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static FogShape[] $values() {
      return new FogShape[]{SPHERE, CYLINDER};
   }
}
