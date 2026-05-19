package net.minecraft.client.gui.screens.social;

import com.mojang.authlib.yggdrasil.FriendsService;
import com.mojang.authlib.yggdrasil.request.JoinInfoUpdate;
import com.mojang.authlib.yggdrasil.response.PresenceResponse;
import com.mojang.authlib.yggdrasil.response.PresenceStatus;
import com.mojang.authlib.yggdrasil.response.PresenceStatusDto;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PresenceSharing;
import net.minecraft.client.gui.components.toasts.FriendToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;

public class PresenceHandler {
   private static final Duration PRESENCE_UPDATE_INTERVAL = Duration.ofSeconds(10L);
   private static final Duration MAX_PRESENCE_UPDATE_INTERVAL = Duration.ofSeconds(60L);
   private final Set<UUID> invitedPlayersBatch = ConcurrentHashMap.newKeySet();
   private final Set<UUID> locallyDismissedInvitePmids = ConcurrentHashMap.newKeySet();
   private final Set<UUID> seenInvites = ConcurrentHashMap.newKeySet();
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
      JoinInfoUpdate joinInfo = this.getJoinInfoUpdate(publicPresenceStatus);
      CompletableFuture.runAsync(() -> {
         PresenceResponse newPresence = this.friendsService.presence(publicPresenceStatus.name(), joinInfo);
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

            this.clearStaleDismissedInvites();
            this.latestPresence.presence().forEach((presence) -> {
               PresenceStatusDto.JoinInfo friendJoinInfo = presence.joinInfo();
               if (friendJoinInfo != null && friendJoinInfo.invited()) {
                  PlayerSkin friendSkin = this.minecraft.playerSkinRenderCache().getOrDefault(ResolvableProfile.createUnresolved(presence.profileId())).playerSkin();
                  this.minecraft.getPlayerSocialManager().getFriends().stream().filter((playerData) -> playerData.id().equals(presence.profileId()) && !this.seenInvites.contains(playerData.id())).findAny().ifPresent((playerData) -> {
                     this.seenInvites.add(playerData.id());
                     FriendToast.showInviteFromFriend(this.minecraft, playerData.name(), friendSkin);
                  });
               }

            });
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

   public void invitePlayer(final UUID id) {
      if (this.invitedPlayersBatch.add(id)) {
         this.tryUpdatePresence();
         CompletableFuture.delayedExecutor(1L, TimeUnit.MINUTES, this.minecraft).execute(() -> this.expireHostInvite(id));
      }
   }

   public Set<UUID> getInvitedPlayersBatch() {
      return this.invitedPlayersBatch;
   }

   public boolean clearInviteForPmid(final UUID pmid) {
      UUID profileId = this.getProfileIdFromPmid(pmid);
      if (profileId != null && this.invitedPlayersBatch.remove(profileId)) {
         this.tryUpdatePresence();
         return true;
      } else {
         return false;
      }
   }

   public void clearInvites() {
      this.invitedPlayersBatch.clear();
   }

   public void dismissInviteForPmid(final UUID pmid) {
      this.locallyDismissedInvitePmids.add(pmid);
      this.seenInvites.remove(this.minecraft.getPlayerSocialManager().getPresenceHandler().getProfileIdFromPmid(pmid));
      this.tryUpdatePresence();
   }

   public boolean hasDismissedInvite(final PresenceStatusDto presence) {
      return this.locallyDismissedInvitePmids.contains(presence.pmid());
   }

   public boolean isInvitedPmid(final UUID pmid) {
      UUID profileId = this.getProfileIdFromPmid(pmid);
      return profileId != null && this.invitedPlayersBatch.contains(profileId);
   }

   public @Nullable UUID getProfileIdFromPmid(final UUID pmid) {
      for(PresenceStatusDto presence : this.latestPresence.presence()) {
         if (pmid.equals(presence.pmid())) {
            return presence.profileId();
         }
      }

      return null;
   }

   private void clearStaleDismissedInvites() {
      this.locallyDismissedInvitePmids.removeIf((pmid) -> this.latestPresence.presence().stream().noneMatch((presence) -> pmid.equals(presence.pmid()) && presence.joinInfo() != null && presence.joinInfo().invited()));
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
      if (singleplayerServer != null) {
         PresenceStatus var10000;
         switch (singleplayerServer.getMultiplayerScope()) {
            case OFF:
            case LAN:
               var10000 = PresenceStatus.PLAYING_OFFLINE;
               break;
            case ONLINE:
               var10000 = PresenceStatus.PLAYING_HOSTED_SERVER;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      } else {
         return PresenceStatus.ONLINE;
      }
   }

   private @Nullable JoinInfoUpdate getJoinInfoUpdate(final PresenceStatus publicPresenceStatus) {
      JoinInfoUpdate var10000;
      switch ((PresenceSharing)this.minecraft.options.sharePresence().get()) {
         case NONE:
            var10000 = null;
            return var10000;
         case LIMITED:
            switch (this.getPresenceStatus()) {
               case PLAYING_HOSTED_SERVER:
                  var10000 = new JoinInfoUpdate((String)null, Set.copyOf(this.invitedPlayersBatch));
                  return var10000;
               case ONLINE:
               case PLAYING_OFFLINE:
               case OFFLINE:
               case PLAYING_REALMS:
               case PLAYING_SERVER:
                  var10000 = null;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case ALL:
            switch (publicPresenceStatus) {
               case PLAYING_HOSTED_SERVER:
                  var10000 = new JoinInfoUpdate((String)null, Set.copyOf(this.invitedPlayersBatch));
                  return var10000;
               case ONLINE:
               case PLAYING_OFFLINE:
               case OFFLINE:
               case PLAYING_REALMS:
               case PLAYING_SERVER:
                  var10000 = null;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         default:
            throw new MatchException((String)null, (Throwable)null);
      }
   }

   private void expireHostInvite(final UUID profileId) {
      if (this.invitedPlayersBatch.remove(profileId)) {
         this.tryUpdatePresence();
         this.minecraft.getPlayerSocialManager().getFriends().stream().filter((playerData) -> playerData.id().equals(profileId)).findAny().ifPresent((friend) -> {
            PlayerSkin friendSkin = this.minecraft.playerSkinRenderCache().getOrDefault(ResolvableProfile.createUnresolved(friend.id())).playerSkin();
            FriendToast.showHostInviteExpired(this.minecraft, friend.name(), friendSkin);
         });
      }
   }
}
