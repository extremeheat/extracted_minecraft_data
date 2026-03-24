package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Set;

public record RealmsWorldResetDto(String seed, long worldTemplateId, int levelType, boolean generateStructures, Set<String> experiments) implements ReflectionBasedSerialization {
   public RealmsWorldResetDto {
      super();
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
