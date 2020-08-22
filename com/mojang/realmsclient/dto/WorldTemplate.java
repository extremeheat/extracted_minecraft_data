package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplate extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String id;
   public String name;
   public String version;
   public String author;
   public String link;
   public String image;
   public String trailer;
   public String recommendedPlayers;
   public WorldTemplate.WorldTemplateType type;

   public static WorldTemplate parse(JsonObject var0) {
      WorldTemplate var1 = new WorldTemplate();

      try {
         var1.id = JsonUtils.getStringOr("id", var0, "");
         var1.name = JsonUtils.getStringOr("name", var0, "");
         var1.version = JsonUtils.getStringOr("version", var0, "");
         var1.author = JsonUtils.getStringOr("author", var0, "");
         var1.link = JsonUtils.getStringOr("link", var0, "");
         var1.image = JsonUtils.getStringOr("image", var0, (String)null);
         var1.trailer = JsonUtils.getStringOr("trailer", var0, "");
         var1.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", var0, "");
         var1.type = WorldTemplate.WorldTemplateType.valueOf(JsonUtils.getStringOr("type", var0, WorldTemplate.WorldTemplateType.WORLD_TEMPLATE.name()));
      } catch (Exception var3) {
         LOGGER.error("Could not parse WorldTemplate: " + var3.getMessage());
      }

      return var1;
   }

   public static enum WorldTemplateType {
      WORLD_TEMPLATE,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }
}
