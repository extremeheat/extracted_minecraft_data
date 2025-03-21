package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum PolygonMode {
   FILL,
   WIREFRAME;

   private PolygonMode() {
   }

   // $FF: synthetic method
   private static PolygonMode[] $values() {
      return new PolygonMode[]{FILL, WIREFRAME};
   }
}
