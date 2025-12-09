package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record WorldTemplate(String id, String name, String version, String author, String link, @Nullable String image, String trailer, String recommendedPlayers, WorldTemplateType type) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public WorldTemplate(String var1, String var2, String var3, String var4, String var5, @Nullable String var6, String var7, String var8, WorldTemplateType var9) {
      super();
      this.id = var1;
      this.name = var2;
      this.version = var3;
      this.author = var4;
      this.link = var5;
      this.image = var6;
      this.trailer = var7;
      this.recommendedPlayers = var8;
      this.type = var9;
   }

   public static @Nullable WorldTemplate parse(JsonObject var0) {
      try {
         String var1 = JsonUtils.getStringOr("type", var0, (String)null);
         return new WorldTemplate(JsonUtils.getStringOr("id", var0, ""), JsonUtils.getStringOr("name", var0, ""), JsonUtils.getStringOr("version", var0, ""), JsonUtils.getStringOr("author", var0, ""), JsonUtils.getStringOr("link", var0, ""), JsonUtils.getStringOr("image", var0, (String)null), JsonUtils.getStringOr("trailer", var0, ""), JsonUtils.getStringOr("recommendedPlayers", var0, ""), var1 == null ? WorldTemplate.WorldTemplateType.WORLD_TEMPLATE : WorldTemplate.WorldTemplateType.valueOf(var1));
      } catch (Exception var2) {
         LOGGER.error("Could not parse WorldTemplate", var2);
         return null;
      }
   }

   public static enum WorldTemplateType {
      WORLD_TEMPLATE,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;

      private WorldTemplateType() {
      }

      // $FF: synthetic method
      private static WorldTemplateType[] $values() {
         return new WorldTemplateType[]{WORLD_TEMPLATE, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
      }
   }
}
