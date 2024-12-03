package com.mojang.realmsclient.dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class RealmsServerPlayerLists extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public Map<Long, List<ProfileResult>> servers = Map.of();

   public RealmsServerPlayerLists() {
      super();
   }

   public static RealmsServerPlayerLists parse(String var0) {
      RealmsServerPlayerLists var1 = new RealmsServerPlayerLists();
      ImmutableMap.Builder var2 = ImmutableMap.builder();

      try {
         JsonObject var3 = GsonHelper.parse(var0);
         if (GsonHelper.isArrayNode(var3, "lists")) {
            for(JsonElement var6 : var3.getAsJsonArray("lists")) {
               JsonObject var8 = var6.getAsJsonObject();
               String var9 = JsonUtils.getStringOr("playerList", var8, (String)null);
               Object var7;
               if (var9 != null) {
                  JsonElement var10 = JsonParser.parseString(var9);
                  if (var10.isJsonArray()) {
                     var7 = parsePlayers(var10.getAsJsonArray());
                  } else {
                     var7 = Lists.newArrayList();
                  }
               } else {
                  var7 = Lists.newArrayList();
               }

               var2.put(JsonUtils.getLongOr("serverId", var8, -1L), var7);
            }
         }
      } catch (Exception var11) {
         LOGGER.error("Could not parse RealmsServerPlayerLists: {}", var11.getMessage());
      }

      var1.servers = var2.build();
      return var1;
   }

   private static List<ProfileResult> parsePlayers(JsonArray var0) {
      ArrayList var1 = new ArrayList(var0.size());
      MinecraftSessionService var2 = Minecraft.getInstance().getMinecraftSessionService();

      for(JsonElement var4 : var0) {
         if (var4.isJsonObject()) {
            UUID var5 = JsonUtils.getUuidOr("playerId", var4.getAsJsonObject(), (UUID)null);
            if (var5 != null && !Minecraft.getInstance().isLocalPlayer(var5)) {
               try {
                  ProfileResult var6 = var2.fetchProfile(var5, false);
                  if (var6 != null) {
                     var1.add(var6);
                  }
               } catch (Exception var7) {
                  LOGGER.error("Could not get name for {}", var5, var7);
               }
            }
         }
      }

      return var1;
   }

   public List<ProfileResult> getProfileResultsFor(long var1) {
      List var3 = (List)this.servers.get(var1);
      return var3 != null ? var3 : List.of();
   }
}
