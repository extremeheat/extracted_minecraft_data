package com.mojang.blaze3d.textures;

public enum TextureFormat {
   RGBA8(4),
   RED8(1),
   DEPTH32(4);

   private final int pixelSize;

   private TextureFormat(final int var3) {
      this.pixelSize = var3;
   }

   public int pixelSize() {
      return this.pixelSize;
   }

   // $FF: synthetic method
   private static TextureFormat[] $values() {
      return new TextureFormat[]{RGBA8, RED8, DEPTH32};
   }
}
