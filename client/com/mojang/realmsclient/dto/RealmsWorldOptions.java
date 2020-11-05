package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import net.minecraft.client.resources.language.I18n;

public class RealmsWorldOptions extends ValueObject {
   public Boolean pvp;
   public Boolean spawnAnimals;
   public Boolean spawnMonsters;
   public Boolean spawnNPCs;
   public Integer spawnProtection;
   public Boolean commandBlocks;
   public Boolean forceGameMode;
   public Integer difficulty;
   public Integer gameMode;
   public String slotName;
   public long templateId;
   public String templateImage;
   public boolean adventureMap;
   public boolean empty;
   private static final String DEFAULT_TEMPLATE_IMAGE = null;

   public RealmsWorldOptions(Boolean var1, Boolean var2, Boolean var3, Boolean var4, Integer var5, Boolean var6, Integer var7, Integer var8, Boolean var9, String var10) {
      super();
      this.pvp = var1;
      this.spawnAnimals = var2;
      this.spawnMonsters = var3;
      this.spawnNPCs = var4;
      this.spawnProtection = var5;
      this.commandBlocks = var6;
      this.difficulty = var7;
      this.gameMode = var8;
      this.forceGameMode = var9;
      this.slotName = var10;
   }

   public static RealmsWorldOptions createDefaults() {
      return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
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
      RealmsWorldOptions var1 = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", var0, true), JsonUtils.getBooleanOr("spawnAnimals", var0, true), JsonUtils.getBooleanOr("spawnMonsters", var0, true), JsonUtils.getBooleanOr("spawnNPCs", var0, true), JsonUtils.getIntOr("spawnProtection", var0, 0), JsonUtils.getBooleanOr("commandBlocks", var0, false), JsonUtils.getIntOr("difficulty", var0, 2), JsonUtils.getIntOr("gameMode", var0, 0), JsonUtils.getBooleanOr("forceGameMode", var0, false), JsonUtils.getStringOr("slotName", var0, ""));
      var1.templateId = JsonUtils.getLongOr("worldTemplateId", var0, -1L);
      var1.templateImage = JsonUtils.getStringOr("worldTemplateImage", var0, DEFAULT_TEMPLATE_IMAGE);
      var1.adventureMap = JsonUtils.getBooleanOr("adventureMap", var0, false);
      return var1;
   }

   public String getSlotName(int var1) {
      if (this.slotName != null && !this.slotName.isEmpty()) {
         return this.slotName;
      } else {
         return this.empty ? I18n.get("mco.configure.world.slot.empty") : this.getDefaultSlotName(var1);
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

      if (!this.spawnAnimals) {
         var1.addProperty("spawnAnimals", this.spawnAnimals);
      }

      if (!this.spawnMonsters) {
         var1.addProperty("spawnMonsters", this.spawnMonsters);
      }

      if (!this.spawnNPCs) {
         var1.addProperty("spawnNPCs", this.spawnNPCs);
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

      return var1.toString();
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }
}
