package com.mojang.blaze3d.textures;

public enum AddressMode {
   REPEAT(10497),
   CLAMP_TO_EDGE(33071);

   final int id;

   private AddressMode(final int var3) {
      this.id = var3;
   }

   // $FF: synthetic method
   private static AddressMode[] $values() {
      return new AddressMode[]{REPEAT, CLAMP_TO_EDGE};
   }
}
