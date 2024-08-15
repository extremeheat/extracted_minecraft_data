package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.StringUtil;

public class RealmsWorldOptions extends ValueObject {
   public final boolean pvp;
   public final boolean spawnMonsters;
   public final int spawnProtection;
   public final boolean commandBlocks;
   public final boolean forceGameMode;
   public final int difficulty;
   public final int gameMode;
   private final String slotName;
   public final String version;
   public final RealmsServer.Compatibility compatibility;
   public long templateId;
   @Nullable
   public String templateImage;
   public boolean empty;
   private static final boolean DEFAULT_FORCE_GAME_MODE = false;
   private static final boolean DEFAULT_PVP = true;
   private static final boolean DEFAULT_SPAWN_MONSTERS = true;
   private static final int DEFAULT_SPAWN_PROTECTION = 0;
   private static final boolean DEFAULT_COMMAND_BLOCKS = false;
   private static final int DEFAULT_DIFFICULTY = 2;
   private static final int DEFAULT_GAME_MODE = 0;
   private static final String DEFAULT_SLOT_NAME = "";
   private static final String DEFAULT_VERSION = "";
   private static final RealmsServer.Compatibility DEFAULT_COMPATIBILITY = RealmsServer.Compatibility.UNVERIFIABLE;
   private static final long DEFAULT_TEMPLATE_ID = -1L;
   private static final String DEFAULT_TEMPLATE_IMAGE = null;

   public RealmsWorldOptions(
      boolean var1, boolean var2, int var3, boolean var4, int var5, int var6, boolean var7, String var8, String var9, RealmsServer.Compatibility var10
   ) {
      super();
      this.pvp = var1;
      this.spawnMonsters = var2;
      this.spawnProtection = var3;
      this.commandBlocks = var4;
      this.difficulty = var5;
      this.gameMode = var6;
      this.forceGameMode = var7;
      this.slotName = var8;
      this.version = var9;
      this.compatibility = var10;
   }

   public static RealmsWorldOptions createDefaults() {
      return new RealmsWorldOptions(true, true, 0, false, 2, 0, false, "", "", DEFAULT_COMPATIBILITY);
   }

   public static RealmsWorldOptions createEmptyDefaults() {
      RealmsWorldOptions var0 = createDefaults();
      var0.setEmpty(true);
      return var0;
   }

   public void setEmpty(boolean var1) {
      this.empty = var1;
   }

   public static RealmsWorldOptions parse(JsonObject var0) {
      RealmsWorldOptions var1 = new RealmsWorldOptions(
         JsonUtils.getBooleanOr("pvp", var0, true),
         JsonUtils.getBooleanOr("spawnMonsters", var0, true),
         JsonUtils.getIntOr("spawnProtection", var0, 0),
         JsonUtils.getBooleanOr("commandBlocks", var0, false),
         JsonUtils.getIntOr("difficulty", var0, 2),
         JsonUtils.getIntOr("gameMode", var0, 0),
         JsonUtils.getBooleanOr("forceGameMode", var0, false),
         JsonUtils.getRequiredStringOr("slotName", var0, ""),
         JsonUtils.getRequiredStringOr("version", var0, ""),
         RealmsServer.getCompatibility(JsonUtils.getRequiredStringOr("compatibility", var0, RealmsServer.Compatibility.UNVERIFIABLE.name()))
      );
      var1.templateId = JsonUtils.getLongOr("worldTemplateId", var0, -1L);
      var1.templateImage = JsonUtils.getStringOr("worldTemplateImage", var0, DEFAULT_TEMPLATE_IMAGE);
      return var1;
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

   public String toJson() {
      JsonObject var1 = new JsonObject();
      if (!this.pvp) {
         var1.addProperty("pvp", this.pvp);
      }

      if (!this.spawnMonsters) {
         var1.addProperty("spawnMonsters", this.spawnMonsters);
      }

      if (this.spawnProtection != 0) {
         var1.addProperty("spawnProtection", this.spawnProtection);
      }

      if (this.commandBlocks) {
         var1.addProperty("commandBlocks", this.commandBlocks);
      }

      if (this.difficulty != 2) {
         var1.addProperty("difficulty", this.difficulty);
      }

      if (this.gameMode != 0) {
         var1.addProperty("gameMode", this.gameMode);
      }

      if (this.forceGameMode) {
         var1.addProperty("forceGameMode", this.forceGameMode);
      }

      if (!Objects.equals(this.slotName, "")) {
         var1.addProperty("slotName", this.slotName);
      }

      if (!Objects.equals(this.version, "")) {
         var1.addProperty("version", this.version);
      }

      if (this.compatibility != DEFAULT_COMPATIBILITY) {
         var1.addProperty("compatibility", this.compatibility.name());
      }

      return var1.toString();
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(
         this.pvp,
         this.spawnMonsters,
         this.spawnProtection,
         this.commandBlocks,
         this.difficulty,
         this.gameMode,
         this.forceGameMode,
         this.slotName,
         this.version,
         this.compatibility
      );
   }
}
