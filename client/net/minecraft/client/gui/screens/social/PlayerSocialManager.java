package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.Util;

public class PlayerSocialManager {
   private final Minecraft minecraft;
   private final Set<UUID> hiddenPlayers = Sets.newHashSet();
   private final UserApiService service;
   private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();
   private boolean onlineMode;
   private CompletableFuture<?> pendingBlockListRefresh = CompletableFuture.completedFuture((Object)null);

   public PlayerSocialManager(final Minecraft minecraft, final UserApiService service) {
      super();
      this.minecraft = minecraft;
      this.service = service;
   }

   public void hidePlayer(final UUID id) {
      this.hiddenPlayers.add(id);
   }

   public void showPlayer(final UUID id) {
      this.hiddenPlayers.remove(id);
   }

   public boolean shouldHideMessageFrom(final UUID id) {
      return this.isHidden(id) || this.isBlocked(id);
   }

   public boolean isHidden(final UUID id) {
      return this.hiddenPlayers.contains(id);
   }

   public void startOnlineMode() {
      this.onlineMode = true;
      CompletableFuture var10001 = this.pendingBlockListRefresh;
      UserApiService var10002 = this.service;
      Objects.requireNonNull(var10002);
      this.pendingBlockListRefresh = var10001.thenRunAsync(var10002::refreshBlockList, Util.ioPool());
   }

   public void stopOnlineMode() {
      this.onlineMode = false;
   }

   public boolean isBlocked(final UUID id) {
      if (!this.onlineMode) {
         return false;
      } else {
         this.pendingBlockListRefresh.join();
         return this.service.isBlockedPlayer(id);
      }
   }

   public Set<UUID> getHiddenPlayers() {
      return this.hiddenPlayers;
   }

   public UUID getDiscoveredUUID(final String name) {
      return (UUID)this.discoveredNamesToUUID.getOrDefault(name, Util.NIL_UUID);
   }

   public void addPlayer(final PlayerInfo info) {
      GameProfile gameProfile = info.getProfile();
      this.discoveredNamesToUUID.put(gameProfile.name(), gameProfile.id());
      Screen var4 = this.minecraft.gui.screen();
      if (var4 instanceof SocialInteractionsScreen screen) {
         screen.onAddPlayer(info);
      }

   }

   public void removePlayer(final UUID id) {
      Screen var3 = this.minecraft.gui.screen();
      if (var3 instanceof SocialInteractionsScreen screen) {
         screen.onRemovePlayer(id);
      }

   }
}
