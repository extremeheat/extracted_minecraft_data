package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class WorldDownload extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String downloadLink;
   public String resourcePackUrl;
   public String resourcePackHash;

   public WorldDownload() {
      super();
   }

   public static WorldDownload parse(String var0) {
      JsonObject var1 = LenientJsonParser.parse(var0).getAsJsonObject();
      WorldDownload var2 = new WorldDownload();

      try {
         var2.downloadLink = JsonUtils.getStringOr("downloadLink", var1, "");
         var2.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", var1, "");
         var2.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", var1, "");
      } catch (Exception var4) {
         LOGGER.error("Could not parse WorldDownload: {}", var4.getMessage());
      }

      return var2;
   }
}
