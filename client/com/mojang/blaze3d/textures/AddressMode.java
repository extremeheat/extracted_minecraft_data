package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
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
