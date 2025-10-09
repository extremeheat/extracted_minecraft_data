package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

public class RealmsServer extends ValueObject implements ReflectionBasedSerialization {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_VALUE = -1;
   public static final Component WORLD_CLOSED_COMPONENT = Component.translatable("mco.play.button.realm.closed");
   @SerializedName("id")
   public long id = -1L;
   @Nullable
   @SerializedName("remoteSubscriptionId")
   public String remoteSubscriptionId;
   @Nullable
   @SerializedName("name")
   public String name;
   @SerializedName("motd")
   public String motd = "";
   @SerializedName("state")
   public State state;
   @Nullable
   @SerializedName("owner")
   public String owner;
   @SerializedName("ownerUUID")
   @JsonAdapter(UUIDTypeAdapter.class)
   public UUID ownerUUID;
   @SerializedName("players")
   public List<PlayerInfo> players;
   @SerializedName("slots")
   private List<RealmsSlot> slotList;
   @Exclude
   public Map<Integer, RealmsSlot> slots;
   @SerializedName("expired")
   public boolean expired;
   @SerializedName("expiredTrial")
   public boolean expiredTrial;
   @SerializedName("daysLeft")
   public int daysLeft;
   @SerializedName("worldType")
   public WorldType worldType;
   @SerializedName("isHardcore")
   public boolean isHardcore;
   @SerializedName("gameMode")
   public int gameMode;
   @SerializedName("activeSlot")
   public int activeSlot;
   @Nullable
   @SerializedName("minigameName")
   public String minigameName;
   @SerializedName("minigameId")
   public int minigameId;
   @Nullable
   @SerializedName("minigameImage")
   public String minigameImage;
   @SerializedName("parentWorldId")
   public long parentRealmId;
   @Nullable
   @SerializedName("parentWorldName")
   public String parentWorldName;
   @SerializedName("activeVersion")
   public String activeVersion;
   @SerializedName("compatibility")
   public Compatibility compatibility;
   @Nullable
   @SerializedName("regionSelectionPreference")
   public RegionSelectionPreferenceDto regionSelectionPreference;

   public RealmsServer() {
      super();
      this.state = RealmsServer.State.CLOSED;
      this.ownerUUID = Util.NIL_UUID;
      this.players = Lists.newArrayList();
      this.slotList = createEmptySlots();
      this.slots = new HashMap();
      this.expiredTrial = false;
      this.worldType = RealmsServer.WorldType.NORMAL;
      this.isHardcore = false;
      this.gameMode = -1;
      this.activeSlot = -1;
      this.minigameId = -1;
      this.parentRealmId = -1L;
      this.activeVersion = "";
      this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
   }

   public String getDescription() {
      return this.motd;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setDescription(String var1) {
      this.motd = var1;
   }

   public static RealmsServer parse(GuardedSerializer var0, String var1) {
      try {
         RealmsServer var2 = (RealmsServer)var0.fromJson(var1, RealmsServer.class);
         if (var2 == null) {
            LOGGER.error("Could not parse McoServer: {}", var1);
            return new RealmsServer();
         } else {
            finalize(var2);
            return var2;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServer", var3);
         return new RealmsServer();
      }
   }

   public static void finalize(RealmsServer var0) {
      if (var0.players == null) {
         var0.players = Lists.newArrayList();
      }

      if (var0.slotList == null) {
         var0.slotList = createEmptySlots();
      }

      if (var0.slots == null) {
         var0.slots = new HashMap();
      }

      if (var0.worldType == null) {
         var0.worldType = RealmsServer.WorldType.NORMAL;
      }

      if (var0.activeVersion == null) {
         var0.activeVersion = "";
      }

      if (var0.compatibility == null) {
         var0.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
      }

      if (var0.regionSelectionPreference == null) {
         var0.regionSelectionPreference = RegionSelectionPreferenceDto.DEFAULT;
      }

      sortInvited(var0);
      finalizeSlots(var0);
   }

   private static void sortInvited(RealmsServer var0) {
      var0.players.sort((var0x, var1) -> ComparisonChain.start().compareFalseFirst(var1.accepted, var0x.accepted).compare(var0x.name.toLowerCase(Locale.ROOT), var1.name.toLowerCase(Locale.ROOT)).result());
   }

   private static void finalizeSlots(RealmsServer var0) {
      var0.slotList.forEach((var1x) -> var0.slots.put(var1x.slotId, var1x));

      for(int var1 = 1; var1 <= 3; ++var1) {
         if (!var0.slots.containsKey(var1)) {
            var0.slots.put(var1, RealmsSlot.defaults(var1));
         }
      }

   }

   private static List<RealmsSlot> createEmptySlots() {
      ArrayList var0 = new ArrayList();
      var0.add(RealmsSlot.defaults(1));
      var0.add(RealmsSlot.defaults(2));
      var0.add(RealmsSlot.defaults(3));
      return var0;
   }

   public boolean isCompatible() {
      return this.compatibility.isCompatible();
   }

   public boolean needsUpgrade() {
      return this.compatibility.needsUpgrade();
   }

   public boolean needsDowngrade() {
      return this.compatibility.needsDowngrade();
   }

   public boolean shouldPlayButtonBeActive() {
      boolean var1 = !this.expired && this.state == RealmsServer.State.OPEN;
      return var1 && (this.isCompatible() || this.needsUpgrade() || this.isSelfOwnedServer());
   }

   private boolean isSelfOwnedServer() {
      return Minecraft.getInstance().isLocalPlayer(this.ownerUUID);
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

   public RealmsServer copy() {
      RealmsServer var1 = new RealmsServer();
      var1.id = this.id;
      var1.remoteSubscriptionId = this.remoteSubscriptionId;
      var1.name = this.name;
      var1.motd = this.motd;
      var1.state = this.state;
      var1.owner = this.owner;
      var1.players = this.players;
      var1.slotList = this.slotList.stream().map(RealmsSlot::copy).toList();
      var1.slots = this.cloneSlots(this.slots);
      var1.expired = this.expired;
      var1.expiredTrial = this.expiredTrial;
      var1.daysLeft = this.daysLeft;
      var1.worldType = this.worldType;
      var1.isHardcore = this.isHardcore;
      var1.gameMode = this.gameMode;
      var1.ownerUUID = this.ownerUUID;
      var1.minigameName = this.minigameName;
      var1.activeSlot = this.activeSlot;
      var1.minigameId = this.minigameId;
      var1.minigameImage = this.minigameImage;
      var1.parentWorldName = this.parentWorldName;
      var1.parentRealmId = this.parentRealmId;
      var1.activeVersion = this.activeVersion;
      var1.compatibility = this.compatibility;
      var1.regionSelectionPreference = this.regionSelectionPreference != null ? this.regionSelectionPreference.copy() : null;
      return var1;
   }

   public Map<Integer, RealmsSlot> cloneSlots(Map<Integer, RealmsSlot> var1) {
      HashMap var2 = Maps.newHashMap();

      for(Map.Entry var4 : var1.entrySet()) {
         var2.put((Integer)var4.getKey(), new RealmsSlot((Integer)var4.getKey(), ((RealmsSlot)var4.getValue()).options.copy(), ((RealmsSlot)var4.getValue()).settings));
      }

      return var2;
   }

   public boolean isSnapshotRealm() {
      return this.parentRealmId != -1L;
   }

   public boolean isMinigameActive() {
      return this.worldType == RealmsServer.WorldType.MINIGAME;
   }

   public String getWorldName(int var1) {
      if (this.name == null) {
         return ((RealmsSlot)this.slots.get(var1)).options.getSlotName(var1);
      } else {
         String var10000 = this.name;
         return var10000 + " (" + ((RealmsSlot)this.slots.get(var1)).options.getSlotName(var1) + ")";
      }
   }

   public ServerData toServerData(String var1) {
      return new ServerData((String)Objects.requireNonNullElse(this.name, "unknown server"), var1, ServerData.Type.REALM);
   }

   public static class McoServerComparator implements Comparator<RealmsServer> {
      private final String refOwner;

      public McoServerComparator(String var1) {
         super();
         this.refOwner = var1;
      }

      public int compare(RealmsServer var1, RealmsServer var2) {
         return ComparisonChain.start().compareTrueFirst(var1.isSnapshotRealm(), var2.isSnapshotRealm()).compareTrueFirst(var1.state == RealmsServer.State.UNINITIALIZED, var2.state == RealmsServer.State.UNINITIALIZED).compareTrueFirst(var1.expiredTrial, var2.expiredTrial).compareTrueFirst(Objects.equals(var1.owner, this.refOwner), Objects.equals(var2.owner, this.refOwner)).compareFalseFirst(var1.expired, var2.expired).compareTrueFirst(var1.state == RealmsServer.State.OPEN, var2.state == RealmsServer.State.OPEN).compare(var1.id, var2.id).result();
      }

      // $FF: synthetic method
      public int compare(final Object var1, final Object var2) {
         return this.compare((RealmsServer)var1, (RealmsServer)var2);
      }
   }

   public static enum State {
      CLOSED,
      OPEN,
      UNINITIALIZED;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{CLOSED, OPEN, UNINITIALIZED};
      }
   }

   public static enum WorldType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;

      private WorldType() {
      }

      // $FF: synthetic method
      private static WorldType[] $values() {
         return new WorldType[]{NORMAL, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
      }
   }

   public static enum Compatibility {
      UNVERIFIABLE,
      INCOMPATIBLE,
      RELEASE_TYPE_INCOMPATIBLE,
      NEEDS_DOWNGRADE,
      NEEDS_UPGRADE,
      COMPATIBLE;

      private Compatibility() {
      }

      public boolean isCompatible() {
         return this == COMPATIBLE;
      }

      public boolean needsUpgrade() {
         return this == NEEDS_UPGRADE;
      }

      public boolean needsDowngrade() {
         return this == NEEDS_DOWNGRADE;
      }

      // $FF: synthetic method
      private static Compatibility[] $values() {
         return new Compatibility[]{UNVERIFIABLE, INCOMPATIBLE, RELEASE_TYPE_INCOMPATIBLE, NEEDS_DOWNGRADE, NEEDS_UPGRADE, COMPATIBLE};
      }
   }
}
