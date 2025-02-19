package com.mojang.blaze3d.textures;

public enum TextureFormat {
   RGBA8(32856, 6408, 5121, 4),
   RED8(33321, 6403, 5121, 1),
   DEPTH32(33191, 6402, 5126, 4);

   private final int internalId;
   private final int externalId;
   private final int type;
   private final int pixelSize;

   private TextureFormat(final int var3, final int var4, final int var5, final int var6) {
      this.internalId = var3;
      this.externalId = var4;
      this.type = var5;
      this.pixelSize = var6;
   }

   int glInternalFormatId() {
      return this.internalId;
   }

   int glExternalFormatId() {
      return this.externalId;
   }

   int glType() {
      return this.type;
   }

   public int pixelSize() {
      return this.pixelSize;
   }

   // $FF: synthetic method
   private static TextureFormat[] $values() {
      return new TextureFormat[]{RGBA8, RED8, DEPTH32};
   }
}
