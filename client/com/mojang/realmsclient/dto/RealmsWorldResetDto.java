package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Set;

public record RealmsWorldResetDto(String seed, long worldTemplateId, int levelType, boolean generateStructures, Set<String> experiments) implements ReflectionBasedSerialization {
   public RealmsWorldResetDto(String var1, long var2, int var4, boolean var5, Set<String> var6) {
      super();
      this.seed = var1;
      this.worldTemplateId = var2;
      this.levelType = var4;
      this.generateStructures = var5;
      this.experiments = var6;
   }

   @SerializedName("seed")
   public String seed() {
      return this.seed;
   }

   @SerializedName("worldTemplateId")
   public long worldTemplateId() {
      return this.worldTemplateId;
   }

   @SerializedName("levelType")
   public int levelType() {
      return this.levelType;
   }

   @SerializedName("generateStructures")
   public boolean generateStructures() {
      return this.generateStructures;
   }

   @SerializedName("experiments")
   public Set<String> experiments() {
      return this.experiments;
   }
}
