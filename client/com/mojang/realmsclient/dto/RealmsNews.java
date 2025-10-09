package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record RealmsNews(@Nullable String newsLink) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public RealmsNews(@Nullable String var1) {
      super();
      this.newsLink = var1;
   }

   public static RealmsNews parse(String var0) {
      String var1 = null;

      try {
         JsonObject var2 = LenientJsonParser.parse(var0).getAsJsonObject();
         var1 = JsonUtils.getStringOr("newsLink", var2, (String)null);
      } catch (Exception var3) {
         LOGGER.error("Could not parse RealmsNews", var3);
      }

      return new RealmsNews(var1);
   }
}
