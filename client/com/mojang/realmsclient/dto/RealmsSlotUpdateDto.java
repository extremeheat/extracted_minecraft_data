package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

public final class RealmsSlotUpdateDto implements ReflectionBasedSerialization {
   @SerializedName("slotId")
   public final int slotId;
   @SerializedName("spawnProtection")
   private final int spawnProtection;
   @SerializedName("forceGameMode")
   private final boolean forceGameMode;
   @SerializedName("difficulty")
   private final int difficulty;
   @SerializedName("gameMode")
   private final int gameMode;
   @SerializedName("slotName")
   private final String slotName;
   @SerializedName("version")
   private final String version;
   @SerializedName("compatibility")
   private final RealmsServer.Compatibility compatibility;
   @SerializedName("worldTemplateId")
   private final long templateId;
   @Nullable
   @SerializedName("worldTemplateImage")
   private final String templateImage;
   @SerializedName("hardcore")
   private final boolean hardcore;

   public RealmsSlotUpdateDto(int var1, RealmsWorldOptions var2, boolean var3) {
      super();
      this.slotId = var1;
      this.spawnProtection = var2.spawnProtection;
      this.forceGameMode = var2.forceGameMode;
      this.difficulty = var2.difficulty;
      this.gameMode = var2.gameMode;
      this.slotName = var2.getSlotName(var1);
      this.version = var2.version;
      this.compatibility = var2.compatibility;
      this.templateId = var2.templateId;
      this.templateImage = var2.templateImage;
      this.hardcore = var3;
   }
}
