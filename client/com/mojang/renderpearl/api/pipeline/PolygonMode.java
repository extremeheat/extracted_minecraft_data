package com.mojang.renderpearl.api.pipeline;

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
