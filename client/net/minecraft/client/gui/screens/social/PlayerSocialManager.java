package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PlayerSocialManager {
   private final Minecraft minecraft;
   private final Set<UUID> hiddenPlayers = Sets.newHashSet();
   private final SocialInteractionsService service;
   private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();

   public PlayerSocialManager(Minecraft var1, SocialInteractionsService var2) {
      super();
      this.minecraft = var1;
      this.service = var2;
   }

   public void hidePlayer(UUID var1) {
      this.hiddenPlayers.add(var1);
   }

   public void showPlayer(UUID var1) {
      this.hiddenPlayers.remove(var1);
   }

   public boolean shouldHideMessageFrom(UUID var1) {
      return this.isHidden(var1) || this.isBlocked(var1);
   }

   public boolean isHidden(UUID var1) {
      return this.hiddenPlayers.contains(var1);
   }

   public boolean isBlocked(UUID var1) {
      return this.service.isBlockedPlayer(var1);
   }

   public Set<UUID> getHiddenPlayers() {
      return this.hiddenPlayers;
   }

   public UUID getDiscoveredUUID(String var1) {
      return (UUID)this.discoveredNamesToUUID.getOrDefault(var1, Util.NIL_UUID);
   }

   public void addPlayer(PlayerInfo var1) {
      GameProfile var2 = var1.getProfile();
      if (var2.isComplete()) {
         this.discoveredNamesToUUID.put(var2.getName(), var2.getId());
      }

      Screen var3 = this.minecraft.screen;
      if (var3 instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen var4 = (SocialInteractionsScreen)var3;
         var4.onAddPlayer(var1);
      }

   }

   public void removePlayer(UUID var1) {
      Screen var2 = this.minecraft.screen;
      if (var2 instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen var3 = (SocialInteractionsScreen)var2;
         var3.onRemovePlayer(var1);
      }

   }
}
