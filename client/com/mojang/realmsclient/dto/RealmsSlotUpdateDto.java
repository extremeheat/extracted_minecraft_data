package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

public record RealmsSlotUpdateDto(int slotId, int spawnProtection, boolean forceGameMode, int difficulty, int gameMode, String slotName, String version, RealmsServer.Compatibility compatibility, long templateId, @Nullable String templateImage, boolean hardcore) implements ReflectionBasedSerialization {
   public RealmsSlotUpdateDto(int var1, RealmsWorldOptions var2, boolean var3) {
      this(var1, var2.spawnProtection, var2.forceGameMode, var2.difficulty, var2.gameMode, var2.getSlotName(var1), var2.version, var2.compatibility, var2.templateId, var2.templateImage, var3);
   }

   public RealmsSlotUpdateDto(int var1, int var2, boolean var3, int var4, int var5, String var6, String var7, RealmsServer.Compatibility var8, long var9, @Nullable String var11, boolean var12) {
      super();
      this.slotId = var1;
      this.spawnProtection = var2;
      this.forceGameMode = var3;
      this.difficulty = var4;
      this.gameMode = var5;
      this.slotName = var6;
      this.version = var7;
      this.compatibility = var8;
      this.templateId = var9;
      this.templateImage = var11;
      this.hardcore = var12;
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

   @Nullable
   @SerializedName("worldTemplateImage")
   public String templateImage() {
      return this.templateImage;
   }

   @SerializedName("hardcore")
   public boolean hardcore() {
      return this.hardcore;
   }
}
