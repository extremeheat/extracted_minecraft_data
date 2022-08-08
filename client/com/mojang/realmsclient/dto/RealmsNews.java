package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import org.slf4j.Logger;

public class RealmsNews extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String newsLink;

   public RealmsNews() {
      super();
   }

   public static RealmsNews parse(String var0) {
      RealmsNews var1 = new RealmsNews();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(var0).getAsJsonObject();
         var1.newsLink = JsonUtils.getStringOr("newsLink", var3, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsNews: {}", var4.getMessage());
      }

      return var1;
   }
}
