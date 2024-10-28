package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class RealmsServerAddress extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   public String address;
   @Nullable
   public String resourcePackUrl;
   @Nullable
   public String resourcePackHash;

   public RealmsServerAddress() {
      super();
   }

   public static RealmsServerAddress parse(String var0) {
      RealmsServerAddress var1 = new RealmsServerAddress();

      try {
         JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
         var1.address = JsonUtils.getStringOr("address", var2, (String)null);
         var1.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", var2, (String)null);
         var1.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", var2, (String)null);
      } catch (Exception var3) {
         LOGGER.error("Could not parse RealmsServerAddress: {}", var3.getMessage());
      }

      return var1;
   }
}
