package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import org.slf4j.Logger;

public class RealmsServerAddress extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String address;
   public String resourcePackUrl;
   public String resourcePackHash;

   public RealmsServerAddress() {
      super();
   }

   public static RealmsServerAddress parse(String var0) {
      JsonParser var1 = new JsonParser();
      RealmsServerAddress var2 = new RealmsServerAddress();

      try {
         JsonObject var3 = var1.parse(var0).getAsJsonObject();
         var2.address = JsonUtils.getStringOr("address", var3, (String)null);
         var2.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", var3, (String)null);
         var2.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", var3, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsServerAddress: {}", var4.getMessage());
      }

      return var2;
   }
}
