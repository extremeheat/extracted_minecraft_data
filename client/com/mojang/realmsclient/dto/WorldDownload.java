package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record WorldDownload(String downloadLink, String resourcePackUrl, String resourcePackHash) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public WorldDownload(String var1, String var2, String var3) {
      super();
      this.downloadLink = var1;
      this.resourcePackUrl = var2;
      this.resourcePackHash = var3;
   }

   public static WorldDownload parse(String var0) {
      JsonObject var1 = LenientJsonParser.parse(var0).getAsJsonObject();

      try {
         return new WorldDownload(JsonUtils.getStringOr("downloadLink", var1, ""), JsonUtils.getStringOr("resourcePackUrl", var1, ""), JsonUtils.getStringOr("resourcePackHash", var1, ""));
      } catch (Exception var3) {
         LOGGER.error("Could not parse WorldDownload", var3);
         return new WorldDownload("", "", "");
      }
   }
}
