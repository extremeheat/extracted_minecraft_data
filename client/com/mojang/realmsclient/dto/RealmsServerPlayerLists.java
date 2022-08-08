package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;

public class RealmsServerPlayerLists extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List<RealmsServerPlayerList> servers;

   public RealmsServerPlayerLists() {
      super();
   }

   public static RealmsServerPlayerLists parse(String var0) {
      RealmsServerPlayerLists var1 = new RealmsServerPlayerLists();
      var1.servers = Lists.newArrayList();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(var0).getAsJsonObject();
         if (var3.get("lists").isJsonArray()) {
            JsonArray var4 = var3.get("lists").getAsJsonArray();
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               var1.servers.add(RealmsServerPlayerList.parse(((JsonElement)var5.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse RealmsServerPlayerLists: {}", var6.getMessage());
      }

      return var1;
   }
}
