package com.mojang.renderpearl.api.textures;

public enum AddressMode {
   REPEAT,
   CLAMP_TO_EDGE;

   private AddressMode() {
   }

   // $FF: synthetic method
   private static AddressMode[] $values() {
      return new AddressMode[]{REPEAT, CLAMP_TO_EDGE};
   }
}
