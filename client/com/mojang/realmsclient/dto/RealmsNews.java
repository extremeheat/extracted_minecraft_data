package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class RealmsNews extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   public String newsLink;

   public RealmsNews() {
      super();
   }

   public static RealmsNews parse(String var0) {
      RealmsNews var1 = new RealmsNews();

      try {
         JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
         var1.newsLink = JsonUtils.getStringOr("newsLink", var2, (String)null);
      } catch (Exception var3) {
         LOGGER.error("Could not parse RealmsNews: {}", var3.getMessage());
      }

      return var1;
   }
}
