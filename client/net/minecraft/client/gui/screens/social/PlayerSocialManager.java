package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PlayerSocialManager {
   private final Minecraft minecraft;
   private final Set<UUID> hiddenPlayers = Sets.newHashSet();
   private final UserApiService service;
   private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();
   private boolean onlineMode;
   private CompletableFuture<?> pendingBlockListRefresh = CompletableFuture.completedFuture(null);

   public PlayerSocialManager(Minecraft var1, UserApiService var2) {
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

   public void startOnlineMode() {
      this.onlineMode = true;
      this.pendingBlockListRefresh = this.pendingBlockListRefresh.thenRunAsync(this.service::refreshBlockList, Util.ioPool());
   }

   public void stopOnlineMode() {
      this.onlineMode = false;
   }

   public boolean isBlocked(UUID var1) {
      if (!this.onlineMode) {
         return false;
      } else {
         this.pendingBlockListRefresh.join();
         return this.service.isBlockedPlayer(var1);
      }
   }

   public Set<UUID> getHiddenPlayers() {
      return this.hiddenPlayers;
   }

   public UUID getDiscoveredUUID(String var1) {
      return this.discoveredNamesToUUID.getOrDefault(var1, Util.NIL_UUID);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void addPlayer(PlayerInfo var1) {
      GameProfile var2 = var1.getProfile();
      if (var2.isComplete()) {
         this.discoveredNamesToUUID.put(var2.getName(), var2.getId());
      }

      Screen var3 = this.minecraft.screen;
      if (var3 instanceof SocialInteractionsScreen var4) {
         var4.onAddPlayer(var1);
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void removePlayer(UUID var1) {
      Screen var2 = this.minecraft.screen;
      if (var2 instanceof SocialInteractionsScreen var3) {
         var3.onRemovePlayer(var1);
      }
   }
}
