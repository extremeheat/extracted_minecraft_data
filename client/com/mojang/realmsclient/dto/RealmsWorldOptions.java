package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;

public class RealmsWorldOptions extends ValueObject implements ReflectionBasedSerialization {
   @SerializedName("spawnProtection")
   public int spawnProtection = 0;
   @SerializedName("forceGameMode")
   public boolean forceGameMode = false;
   @SerializedName("difficulty")
   public int difficulty = 2;
   @SerializedName("gameMode")
   public int gameMode = 0;
   @SerializedName("slotName")
   private String slotName = "";
   @SerializedName("version")
   public String version = "";
   @SerializedName("compatibility")
   public RealmsServer.Compatibility compatibility;
   @SerializedName("worldTemplateId")
   public long templateId;
   @Nullable
   @SerializedName("worldTemplateImage")
   public String templateImage;
   public boolean empty;

   private RealmsWorldOptions() {
      super();
      this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
      this.templateId = -1L;
      this.templateImage = null;
   }

   public RealmsWorldOptions(int var1, int var2, int var3, boolean var4, String var5, String var6, RealmsServer.Compatibility var7) {
      super();
      this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
      this.templateId = -1L;
      this.templateImage = null;
      this.spawnProtection = var1;
      this.difficulty = var2;
      this.gameMode = var3;
      this.forceGameMode = var4;
      this.slotName = var5;
      this.version = var6;
      this.compatibility = var7;
   }

   public static RealmsWorldOptions createDefaults() {
      return new RealmsWorldOptions();
   }

   public static RealmsWorldOptions createDefaultsWith(GameType var0, Difficulty var1, boolean var2, String var3, String var4) {
      RealmsWorldOptions var5 = createDefaults();
      var5.difficulty = var1.getId();
      var5.gameMode = var0.getId();
      var5.slotName = var4;
      var5.version = var3;
      return var5;
   }

   public static RealmsWorldOptions createFromSettings(LevelSettings var0, String var1) {
      return createDefaultsWith(var0.gameType(), var0.difficulty(), var0.hardcore(), var1, var0.levelName());
   }

   public static RealmsWorldOptions createEmptyDefaults() {
      RealmsWorldOptions var0 = createDefaults();
      var0.setEmpty(true);
      return var0;
   }

   public void setEmpty(boolean var1) {
      this.empty = var1;
   }

   public static RealmsWorldOptions parse(GuardedSerializer var0, String var1) {
      RealmsWorldOptions var2 = (RealmsWorldOptions)var0.fromJson(var1, RealmsWorldOptions.class);
      if (var2 == null) {
         return createDefaults();
      } else {
         finalize(var2);
         return var2;
      }
   }

   private static void finalize(RealmsWorldOptions var0) {
      if (var0.slotName == null) {
         var0.slotName = "";
      }

      if (var0.version == null) {
         var0.version = "";
      }

      if (var0.compatibility == null) {
         var0.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
      }

   }

   public String getSlotName(int var1) {
      if (StringUtil.isBlank(this.slotName)) {
         return this.empty ? I18n.get("mco.configure.world.slot.empty") : this.getDefaultSlotName(var1);
      } else {
         return this.slotName;
      }
   }

   public String getDefaultSlotName(int var1) {
      return I18n.get("mco.configure.world.slot", var1);
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(this.spawnProtection, this.difficulty, this.gameMode, this.forceGameMode, this.slotName, this.version, this.compatibility);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }
}
