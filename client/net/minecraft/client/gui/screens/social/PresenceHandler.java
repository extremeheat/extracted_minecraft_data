package net.minecraft.client.gui.screens.social;

import com.mojang.authlib.services.FriendsService;
import com.mojang.authlib.services.response.PresenceResponse;
import com.mojang.authlib.services.response.PresenceStatus;
import com.mojang.authlib.services.response.PresenceStatusDto;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PresenceSharing;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PresenceHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Duration BACKUP_POLL_INTERVAL = Duration.ofMinutes(5L);
   private static final Duration MIN_PRESENCE_POST_INTERVAL = Duration.ofSeconds(5L);
   private final Minecraft minecraft;
   private final FriendsService friendsService;
   private PresenceResponse latestPresence = new PresenceResponse(new ArrayList());
   private Instant lastPresencePost;
   private @Nullable PresenceStatus lastPostedStatus;

   public PresenceHandler(final Minecraft minecraft, final FriendsService friendsService) {
      super();
      this.lastPresencePost = Instant.EPOCH;
      this.minecraft = minecraft;
      this.friendsService = friendsService;
   }

   private void updatePresence(final PresenceStatus publicPresenceStatus) {
      this.lastPresencePost = Instant.now();
      this.lastPostedStatus = publicPresenceStatus;
      CompletableFuture.runAsync(() -> {
         PresenceResponse newPresence = this.postPresence(publicPresenceStatus);
         if (newPresence != null) {
            this.minecraft.execute(() -> this.setLatestPresence(newPresence));
         }
      }, Util.nonCriticalIoPool());
   }

   public void updatePresenceWith(final PresenceStatusDto presenceStatusDto) {
      this.minecraft.execute(() -> {
         List<PresenceStatusDto> presence = new ArrayList(this.latestPresence.presence());

         for(PresenceStatusDto presenceStatus : presence) {
            if (presenceStatus.profileId().equals(presenceStatusDto.profileId())) {
               if (presenceStatusDto.lastUpdated().isAfter(presenceStatus.lastUpdated())) {
                  presence.set(presence.indexOf(presenceStatus), presenceStatusDto);
                  this.setLatestPresence(new PresenceResponse(presence));
               }

               return;
            }
         }

         presence.add(presenceStatusDto);
         this.setLatestPresence(new PresenceResponse(presence));
      });
   }

   private void setLatestPresence(final PresenceResponse newPresence) {
      boolean refreshPresence = !Objects.equals(this.latestPresence, newPresence);
      this.latestPresence = newPresence;
      if (refreshPresence) {
         Screen var4 = this.minecraft.gui.screen();
         if (var4 instanceof PresenceAwareScreen) {
            PresenceAwareScreen presenceAwareScreen = (PresenceAwareScreen)var4;
            presenceAwareScreen.applyPresenceUpdate(this.latestPresence);
         }
      }

   }

   private @Nullable PresenceResponse postPresence(final PresenceStatus status) {
      try {
         return this.friendsService.presence(status.name());
      } catch (Exception e) {
         LOGGER.warn("Failed to post presence {}", status, e);
         return null;
      }
   }

   private boolean isPresenceSharingDisabled() {
      PlayerSocialManager socialManager = this.minecraft.getPlayerSocialManager();
      return !socialManager.isFriendListEnabled() || socialManager.getFriends().isEmpty();
   }

   public void tick() {
      if (!this.isPresenceSharingDisabled()) {
         Duration sinceLastPresencePost = Duration.between(this.lastPresencePost, Instant.now());
         if (sinceLastPresencePost.compareTo(MIN_PRESENCE_POST_INTERVAL) >= 0) {
            PresenceStatus publicPresenceStatus = this.getPublicPresenceStatus();
            boolean ownPresenceChanged = publicPresenceStatus != this.lastPostedStatus;
            Duration pollInterval = (Duration)this.friendsService.getPresencePollInterval().orElse(BACKUP_POLL_INTERVAL);
            boolean backupPollDue = sinceLastPresencePost.compareTo(pollInterval) >= 0;
            if (ownPresenceChanged || backupPollDue) {
               this.updatePresence(publicPresenceStatus);
            }

         }
      }
   }

   public void sendOfflinePresence() {
      if (!this.isPresenceSharingDisabled()) {
         Util.ioPool().execute(() -> this.postPresence(PresenceStatus.OFFLINE));
      }
   }

   public PresenceResponse getLatestPresence() {
      return this.latestPresence;
   }

   public PresenceStatus getPublicPresenceStatus() {
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
      if (singleplayerServer != null) {
         return singleplayerServer.getMultiplayerScope() == MinecraftServer.MultiplayerScope.LAN ? PresenceStatus.PLAYING_HOSTED_SERVER : PresenceStatus.PLAYING_OFFLINE;
      } else {
         ServerData server = this.minecraft.getCurrentServer();
         if (server != null) {
            return server.isRealm() ? PresenceStatus.PLAYING_REALMS : PresenceStatus.PLAYING_SERVER;
         } else {
            return PresenceStatus.ONLINE;
         }
      }
   }
}
