package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;

public class RealmsServerPlayerList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public long serverId;
   public List<UUID> players;

   public RealmsServerPlayerList() {
      super();
   }

   public static RealmsServerPlayerList parse(JsonObject var0) {
      RealmsServerPlayerList var1 = new RealmsServerPlayerList();

      try {
         var1.serverId = JsonUtils.getLongOr("serverId", var0, -1L);
         String var2 = JsonUtils.getStringOr("playerList", var0, null);
         if (var2 != null) {
            JsonElement var3 = JsonParser.parseString(var2);
            if (var3.isJsonArray()) {
               var1.players = parsePlayers(var3.getAsJsonArray());
            } else {
               var1.players = Lists.newArrayList();
            }
         } else {
            var1.players = Lists.newArrayList();
         }
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsServerPlayerList: {}", var4.getMessage());
      }

      return var1;
   }

   private static List<UUID> parsePlayers(JsonArray var0) {
      ArrayList var1 = new ArrayList(var0.size());

      for(JsonElement var3 : var0) {
         if (var3.isJsonObject()) {
            UUID var4 = JsonUtils.getUuidOr("playerId", var3.getAsJsonObject(), null);
            if (var4 != null) {
               var1.add(var4);
            }
         }
      }

      return var1;
   }
}
