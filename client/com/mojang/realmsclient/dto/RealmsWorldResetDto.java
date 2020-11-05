package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;

public class RealmsWorldResetDto extends ValueObject implements ReflectionBasedSerialization {
   @SerializedName("seed")
   private final String seed;
   @SerializedName("worldTemplateId")
   private final long worldTemplateId;
   @SerializedName("levelType")
   private final int levelType;
   @SerializedName("generateStructures")
   private final boolean generateStructures;

   public RealmsWorldResetDto(String var1, long var2, int var4, boolean var5) {
      super();
      this.seed = var1;
      this.worldTemplateId = var2;
      this.levelType = var4;
      this.generateStructures = var5;
   }
}
