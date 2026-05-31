package net.minecraft.client.gui.screens.social;

import com.mojang.authlib.yggdrasil.FriendsService;
import com.mojang.authlib.yggdrasil.response.PresenceResponse;
import com.mojang.authlib.yggdrasil.response.PresenceStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PresenceSharing;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.util.Util;

public class PresenceHandler {
   private static final Duration PRESENCE_UPDATE_INTERVAL = Duration.ofSeconds(10L);
   private static final Duration MAX_PRESENCE_UPDATE_INTERVAL = Duration.ofSeconds(60L);
   private final Minecraft minecraft;
   private final FriendsService friendsService;
   private PresenceResponse latestPresence = new PresenceResponse(new ArrayList());
   private Instant lastPresencePost = Instant.now();
   private boolean updatePresence;

   public PresenceHandler(final Minecraft minecraft, final FriendsService friendsService) {
      super();
      this.minecraft = minecraft;
      this.friendsService = friendsService;
      this.updatePresence = true;
   }

   private void updatePresence() {
      this.updatePresence = false;
      this.lastPresencePost = Instant.now();
      PresenceStatus publicPresenceStatus = this.getPublicPresenceStatus();
      CompletableFuture.runAsync(() -> {
         PresenceResponse newPresence = this.friendsService.presence(publicPresenceStatus.name());
         this.minecraft.execute(() -> {
            boolean refreshList = this.latestPresence != newPresence;
            this.latestPresence = newPresence;
            if (refreshList) {
               Screen patt0$temp = this.minecraft.gui.screen();
               if (patt0$temp instanceof FriendsOverlayScreen) {
                  FriendsOverlayScreen friendsOverlayScreen = (FriendsOverlayScreen)patt0$temp;
                  friendsOverlayScreen.refreshLists();
               }
            }

         });
      }, Util.backgroundExecutor());
   }

   private boolean shouldRefreshPresence() {
      if (this.minecraft.getPlayerSocialManager().isFriendListEnabled() && !this.minecraft.getPlayerSocialManager().getFriends().isEmpty()) {
         Duration sinceLastPresence = Duration.between(this.lastPresencePost, Instant.now());
         return this.updatePresence && sinceLastPresence.compareTo(PRESENCE_UPDATE_INTERVAL) >= 0 || sinceLastPresence.compareTo(MAX_PRESENCE_UPDATE_INTERVAL) >= 0;
      } else {
         return false;
      }
   }

   public void tick() {
      if (this.shouldRefreshPresence()) {
         this.updatePresence();
      }

   }

   public void tryUpdatePresence() {
      this.updatePresence = true;
   }

   public PresenceResponse getLatestPresence() {
      return this.latestPresence;
   }

   private PresenceStatus getPublicPresenceStatus() {
      PresenceStatus var10000;
      switch ((PresenceSharing)this.minecraft.options.sharePresence().get()) {
         case NONE -> var10000 = PresenceStatus.OFFLINE;
         case LIMITED -> var10000 = PresenceStatus.ONLINE;
         case ALL -> var10000 = this.getPresenceStatus();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private PresenceStatus getPresenceStatus() {
      IntegratedServer singleplayerServer = this.minecraft.getSingleplayerServer();
      return singleplayerServer != null ? PresenceStatus.PLAYING_OFFLINE : PresenceStatus.ONLINE;
   }
}
