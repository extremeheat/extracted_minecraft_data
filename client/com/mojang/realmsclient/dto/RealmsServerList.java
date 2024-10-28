package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;

public class RealmsServerList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List<RealmsServer> servers;

   public RealmsServerList() {
      super();
   }

   public static RealmsServerList parse(String var0) {
      RealmsServerList var1 = new RealmsServerList();
      var1.servers = new ArrayList();

      try {
         JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
         if (var2.get("servers").isJsonArray()) {
            JsonArray var3 = var2.get("servers").getAsJsonArray();
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               JsonElement var5 = (JsonElement)var4.next();
               var1.servers.add(RealmsServer.parse(var5.getAsJsonObject()));
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse McoServerList: {}", var6.getMessage());
      }

      return var1;
   }
}
