package com.mojang.realmsclient.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServer extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long id;
   public String remoteSubscriptionId;
   public String name;
   public String motd;
   public RealmsServer.State state;
   public String owner;
   public String ownerUUID;
   public List<PlayerInfo> players;
   public Map<Integer, RealmsWorldOptions> slots;
   public boolean expired;
   public boolean expiredTrial;
   public int daysLeft;
   public RealmsServer.WorldType worldType;
   public int activeSlot;
   public String minigameName;
   public int minigameId;
   public String minigameImage;
   public RealmsServerPing serverPing = new RealmsServerPing();

   public RealmsServer() {
      super();
   }

   public String getDescription() {
      return this.motd;
   }

   public String getName() {
      return this.name;
   }

   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setDescription(String var1) {
      this.motd = var1;
   }

   public void updateServerPing(RealmsServerPlayerList var1) {
      ArrayList var2 = Lists.newArrayList();
      int var3 = 0;
      Iterator var4 = var1.players.iterator();

      while(true) {
         String var5;
         do {
            if (!var4.hasNext()) {
               this.serverPing.nrOfPlayers = String.valueOf(var3);
               this.serverPing.playerList = Joiner.on('\n').join(var2);
               return;
            }

            var5 = (String)var4.next();
         } while(var5.equals(Minecraft.getInstance().getUser().getUuid()));

         String var6 = "";

         try {
            var6 = RealmsUtil.uuidToName(var5);
         } catch (Exception var8) {
            LOGGER.error("Could not get name for {}", var5, var8);
            continue;
         }

         var2.add(var6);
         ++var3;
      }
   }

   public static RealmsServer parse(JsonObject var0) {
      RealmsServer var1 = new RealmsServer();

      try {
         var1.id = JsonUtils.getLongOr("id", var0, -1L);
         var1.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", var0, (String)null);
         var1.name = JsonUtils.getStringOr("name", var0, (String)null);
         var1.motd = JsonUtils.getStringOr("motd", var0, (String)null);
         var1.state = getState(JsonUtils.getStringOr("state", var0, RealmsServer.State.CLOSED.name()));
         var1.owner = JsonUtils.getStringOr("owner", var0, (String)null);
         if (var0.get("players") != null && var0.get("players").isJsonArray()) {
            var1.players = parseInvited(var0.get("players").getAsJsonArray());
            sortInvited(var1);
         } else {
            var1.players = Lists.newArrayList();
         }

         var1.daysLeft = JsonUtils.getIntOr("daysLeft", var0, 0);
         var1.expired = JsonUtils.getBooleanOr("expired", var0, false);
         var1.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", var0, false);
         var1.worldType = getWorldType(JsonUtils.getStringOr("worldType", var0, RealmsServer.WorldType.NORMAL.name()));
         var1.ownerUUID = JsonUtils.getStringOr("ownerUUID", var0, "");
         if (var0.get("slots") != null && var0.get("slots").isJsonArray()) {
            var1.slots = parseSlots(var0.get("slots").getAsJsonArray());
         } else {
            var1.slots = createEmptySlots();
         }

         var1.minigameName = JsonUtils.getStringOr("minigameName", var0, (String)null);
         var1.activeSlot = JsonUtils.getIntOr("activeSlot", var0, -1);
         var1.minigameId = JsonUtils.getIntOr("minigameId", var0, -1);
         var1.minigameImage = JsonUtils.getStringOr("minigameImage", var0, (String)null);
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServer: {}", var3.getMessage());
      }

      return var1;
   }

   private static void sortInvited(RealmsServer var0) {
      var0.players.sort((var0x, var1) -> {
         return ComparisonChain.start().compareFalseFirst(var1.getAccepted(), var0x.getAccepted()).compare(var0x.getName().toLowerCase(Locale.ROOT), var1.getName().toLowerCase(Locale.ROOT)).result();
      });
   }

   private static List<PlayerInfo> parseInvited(JsonArray var0) {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         JsonElement var3 = (JsonElement)var2.next();

         try {
            JsonObject var4 = var3.getAsJsonObject();
            PlayerInfo var5 = new PlayerInfo();
            var5.setName(JsonUtils.getStringOr("name", var4, (String)null));
            var5.setUuid(JsonUtils.getStringOr("uuid", var4, (String)null));
            var5.setOperator(JsonUtils.getBooleanOr("operator", var4, false));
            var5.setAccepted(JsonUtils.getBooleanOr("accepted", var4, false));
            var5.setOnline(JsonUtils.getBooleanOr("online", var4, false));
            var1.add(var5);
         } catch (Exception var6) {
         }
      }

      return var1;
   }

   private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray var0) {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         JsonElement var3 = (JsonElement)var2.next();

         try {
            JsonObject var5 = var3.getAsJsonObject();
            JsonParser var6 = new JsonParser();
            JsonElement var7 = var6.parse(var5.get("options").getAsString());
            RealmsWorldOptions var4;
            if (var7 == null) {
               var4 = RealmsWorldOptions.createDefaults();
            } else {
               var4 = RealmsWorldOptions.parse(var7.getAsJsonObject());
            }

            int var8 = JsonUtils.getIntOr("slotId", var5, -1);
            var1.put(var8, var4);
         } catch (Exception var9) {
         }
      }

      for(int var10 = 1; var10 <= 3; ++var10) {
         if (!var1.containsKey(var10)) {
            var1.put(var10, RealmsWorldOptions.createEmptyDefaults());
         }
      }

      return var1;
   }

   private static Map<Integer, RealmsWorldOptions> createEmptySlots() {
      HashMap var0 = Maps.newHashMap();
      var0.put(1, RealmsWorldOptions.createEmptyDefaults());
      var0.put(2, RealmsWorldOptions.createEmptyDefaults());
      var0.put(3, RealmsWorldOptions.createEmptyDefaults());
      return var0;
   }

   public static RealmsServer parse(String var0) {
      try {
         return parse((new JsonParser()).parse(var0).getAsJsonObject());
      } catch (Exception var2) {
         LOGGER.error("Could not parse McoServer: {}", var2.getMessage());
         return new RealmsServer();
      }
   }

   private static RealmsServer.State getState(String var0) {
      try {
         return RealmsServer.State.valueOf(var0);
      } catch (Exception var2) {
         return RealmsServer.State.CLOSED;
      }
   }

   private static RealmsServer.WorldType getWorldType(String var0) {
      try {
         return RealmsServer.WorldType.valueOf(var0);
      } catch (Exception var2) {
         return RealmsServer.WorldType.NORMAL;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.name, this.motd, this.state, this.owner, this.expired});
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         RealmsServer var2 = (RealmsServer)var1;
         return (new EqualsBuilder()).append(this.id, var2.id).append(this.name, var2.name).append(this.motd, var2.motd).append(this.state, var2.state).append(this.owner, var2.owner).append(this.expired, var2.expired).append(this.worldType, this.worldType).isEquals();
      }
   }

   public RealmsServer clone() {
      RealmsServer var1 = new RealmsServer();
      var1.id = this.id;
      var1.remoteSubscriptionId = this.remoteSubscriptionId;
      var1.name = this.name;
      var1.motd = this.motd;
      var1.state = this.state;
      var1.owner = this.owner;
      var1.players = this.players;
      var1.slots = this.cloneSlots(this.slots);
      var1.expired = this.expired;
      var1.expiredTrial = this.expiredTrial;
      var1.daysLeft = this.daysLeft;
      var1.serverPing = new RealmsServerPing();
      var1.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
      var1.serverPing.playerList = this.serverPing.playerList;
      var1.worldType = this.worldType;
      var1.ownerUUID = this.ownerUUID;
      var1.minigameName = this.minigameName;
      var1.activeSlot = this.activeSlot;
      var1.minigameId = this.minigameId;
      var1.minigameImage = this.minigameImage;
      return var1;
   }

   public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var2.put(var4.getKey(), ((RealmsWorldOptions)var4.getValue()).clone());
      }

      return var2;
   }

   public String getWorldName(int var1) {
      return this.name + " (" + ((RealmsWorldOptions)this.slots.get(var1)).getSlotName(var1) + ")";
   }

   public ServerData toServerData(String var1) {
      return new ServerData(this.name, var1, false);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }

   public static enum WorldType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;

      private WorldType() {
      }
   }

   public static enum State {
      CLOSED,
      OPEN,
      UNINITIALIZED;

      private State() {
      }
   }

   public static class McoServerComparator implements Comparator<RealmsServer> {
      private final String refOwner;

      public McoServerComparator(String var1) {
         super();
         this.refOwner = var1;
      }

      public int compare(RealmsServer var1, RealmsServer var2) {
         return ComparisonChain.start().compareTrueFirst(var1.state == RealmsServer.State.UNINITIALIZED, var2.state == RealmsServer.State.UNINITIALIZED).compareTrueFirst(var1.expiredTrial, var2.expiredTrial).compareTrueFirst(var1.owner.equals(this.refOwner), var2.owner.equals(this.refOwner)).compareFalseFirst(var1.expired, var2.expired).compareTrueFirst(var1.state == RealmsServer.State.OPEN, var2.state == RealmsServer.State.OPEN).compare(var1.id, var2.id).result();
      }

      // $FF: synthetic method
      public int compare(Object var1, Object var2) {
         return this.compare((RealmsServer)var1, (RealmsServer)var2);
      }
   }
}
