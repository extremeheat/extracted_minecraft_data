package com.mojang.realmsclient.dto;

public class RealmsWorldResetDto extends ValueObject {
   private final String seed;
   private final long worldTemplateId;
   private final int levelType;
   private final boolean generateStructures;

   public RealmsWorldResetDto(String var1, long var2, int var4, boolean var5) {
      this.seed = var1;
      this.worldTemplateId = var2;
      this.levelType = var4;
      this.generateStructures = var5;
   }
}
