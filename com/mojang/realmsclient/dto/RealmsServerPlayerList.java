package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final JsonParser jsonParser = new JsonParser();
   public long serverId;
   public List players;

   public static RealmsServerPlayerList parse(JsonObject var0) {
      RealmsServerPlayerList var1 = new RealmsServerPlayerList();

      try {
         var1.serverId = JsonUtils.getLongOr("serverId", var0, -1L);
         String var2 = JsonUtils.getStringOr("playerList", var0, (String)null);
         if (var2 != null) {
            JsonElement var3 = jsonParser.parse(var2);
            if (var3.isJsonArray()) {
               var1.players = parsePlayers(var3.getAsJsonArray());
            } else {
               var1.players = Lists.newArrayList();
            }
         } else {
            var1.players = Lists.newArrayList();
         }
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsServerPlayerList: " + var4.getMessage());
      }

      return var1;
   }

   private static List parsePlayers(JsonArray var0) {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         JsonElement var3 = (JsonElement)var2.next();

         try {
            var1.add(var3.getAsString());
         } catch (Exception var5) {
         }
      }

      return var1;
   }
}
