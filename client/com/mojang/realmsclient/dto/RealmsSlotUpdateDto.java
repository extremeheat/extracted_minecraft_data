package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

public record RealmsSlotUpdateDto(int slotId, int spawnProtection, boolean forceGameMode, int difficulty, int gameMode, String slotName, String version, RealmsServer.Compatibility compatibility, long templateId, @Nullable String templateImage, boolean hardcore) implements ReflectionBasedSerialization {
   public RealmsSlotUpdateDto(final int slotId, final RealmsWorldOptions options, final boolean hardcore) {
      this(slotId, options.spawnProtection, options.forceGameMode, options.difficulty, options.gameMode, options.getSlotName(slotId), options.version, options.compatibility, options.templateId, options.templateImage, hardcore);
   }

   public RealmsSlotUpdateDto {
      super();
   }

   @SerializedName("slotId")
   public int slotId() {
      return this.slotId;
   }

   @SerializedName("spawnProtection")
   public int spawnProtection() {
      return this.spawnProtection;
   }

   @SerializedName("forceGameMode")
   public boolean forceGameMode() {
      return this.forceGameMode;
   }

   @SerializedName("difficulty")
   public int difficulty() {
      return this.difficulty;
   }

   @SerializedName("gameMode")
   public int gameMode() {
      return this.gameMode;
   }

   @SerializedName("slotName")
   public String slotName() {
      return this.slotName;
   }

   @SerializedName("version")
   public String version() {
      return this.version;
   }

   @SerializedName("compatibility")
   public RealmsServer.Compatibility compatibility() {
      return this.compatibility;
   }

   @SerializedName("worldTemplateId")
   public long templateId() {
      return this.templateId;
   }

   @SerializedName("worldTemplateImage")
   public @Nullable String templateImage() {
      return this.templateImage;
   }

   @SerializedName("hardcore")
   public boolean hardcore() {
      return this.hardcore;
   }
}
