package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum FilterMode {
   NEAREST,
   LINEAR;

   private FilterMode() {
   }

   // $FF: synthetic method
   private static FilterMode[] $values() {
      return new FilterMode[]{NEAREST, LINEAR};
   }
}
