package com.mojang.renderpearl.api.textures;

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
