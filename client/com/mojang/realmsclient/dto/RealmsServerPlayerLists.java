package com.mojang.realmsclient.dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.world.item.component.ResolvableProfile;
import org.slf4j.Logger;

public record RealmsServerPlayerLists(Map<Long, List<ResolvableProfile>> servers) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public RealmsServerPlayerLists(Map<Long, List<ResolvableProfile>> var1) {
      super();
      this.servers = var1;
   }

   public static RealmsServerPlayerLists parse(String var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();

      try {
         JsonObject var2 = GsonHelper.parse(var0);
         if (GsonHelper.isArrayNode(var2, "lists")) {
            for(JsonElement var5 : var2.getAsJsonArray("lists")) {
               JsonObject var7 = var5.getAsJsonObject();
               String var8 = JsonUtils.getStringOr("playerList", var7, (String)null);
               Object var6;
               if (var8 != null) {
                  JsonElement var9 = LenientJsonParser.parse(var8);
                  if (var9.isJsonArray()) {
                     var6 = parsePlayers(var9.getAsJsonArray());
                  } else {
                     var6 = Lists.newArrayList();
                  }
               } else {
                  var6 = Lists.newArrayList();
               }

               var1.put(JsonUtils.getLongOr("serverId", var7, -1L), var6);
            }
         }
      } catch (Exception var10) {
         LOGGER.error("Could not parse RealmsServerPlayerLists", var10);
      }

      return new RealmsServerPlayerLists(var1.build());
   }

   private static List<ResolvableProfile> parsePlayers(JsonArray var0) {
      ArrayList var1 = new ArrayList(var0.size());

      for(JsonElement var3 : var0) {
         if (var3.isJsonObject()) {
            UUID var4 = JsonUtils.getUuidOr("playerId", var3.getAsJsonObject(), (UUID)null);
            if (var4 != null && !Minecraft.getInstance().isLocalPlayer(var4)) {
               var1.add(ResolvableProfile.createUnresolved(var4));
            }
         }
      }

      return var1;
   }

   public List<ResolvableProfile> getProfileResultsFor(long var1) {
      List var3 = (List)this.servers.get(var1);
      return var3 != null ? var3 : List.of();
   }
}
