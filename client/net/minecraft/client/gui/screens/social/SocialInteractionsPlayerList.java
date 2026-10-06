package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.friends.FriendsListFilter;
import net.minecraft.client.gui.screens.friends.FriendsListOrder;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import org.jspecify.annotations.Nullable;

public class SocialInteractionsPlayerList {
   private final Minecraft minecraft;
   private final SocialInteractionsScreen interactionsScreen;
   private final List<PlayerEntry> players = Lists.newArrayList();
   private final FriendsListFilter filter;
   private final Comparator<PlayerEntry> comparator;
   private @Nullable PlayerInteractionsTab interactionsTab;

   public SocialInteractionsPlayerList(final Minecraft minecraft, final SocialInteractionsScreen interactionsScreen, final FriendsListFilter filter, final FriendsListOrder order) {
      super();
      this.minecraft = minecraft;
      this.interactionsScreen = interactionsScreen;
      this.filter = filter;
      this.comparator = Comparator.comparingInt(PlayerEntry::prioritySortOrder).thenComparing((e) -> !isValidPlayerName(e.playerName())).thenComparing(order);
   }

   void setActiveTab(final @Nullable PlayerInteractionsTab tab) {
      this.interactionsTab = tab;
   }

   public void updatePlayerList(final Collection<UUID> playersToAdd, final boolean addOfflineEntries) {
      Map<UUID, PlayerEntry> newEntries = new HashMap();
      this.addOnlinePlayers(playersToAdd, newEntries);
      if (addOfflineEntries) {
         this.addSeenPlayers(newEntries);
      }

      this.updatePlayersFromChatLog(newEntries, addOfflineEntries);
      this.updateFiltersAndScroll(newEntries.values());
   }

   private void addOnlinePlayers(final Collection<UUID> playersToAdd, final Map<UUID, PlayerEntry> output) {
      ClientPacketListener connection = this.minecraft.player.connection;

      for(UUID id : playersToAdd) {
         if (SharedConstants.DEBUG_SOCIAL_INTERACTIONS || !this.minecraft.isLocalPlayer(id)) {
            PlayerInfo playerInfo = connection.getPlayerInfo(id);
            if (playerInfo != null) {
               PlayerEntry player = this.makePlayerEntry(id, playerInfo);
               output.put(id, player);
            }
         }
      }

   }

   private void addSeenPlayers(final Map<UUID, PlayerEntry> newEntries) {
      Map<UUID, PlayerInfo> seenPlayers = this.minecraft.player.connection.getSeenPlayers();

      for(Map.Entry<UUID, PlayerInfo> entry : seenPlayers.entrySet()) {
         newEntries.computeIfAbsent((UUID)entry.getKey(), (uuid) -> {
            PlayerEntry player = this.makePlayerEntry(uuid, (PlayerInfo)entry.getValue());
            player.setRemoved(true);
            return player;
         });
      }

   }

   private PlayerEntry makePlayerEntry(final UUID id, final PlayerInfo playerInfo) {
      PlayerSocialManager.Visibility visibility = this.minecraft.getPlayerSocialManager().getVisibility(id);
      Minecraft var10002 = this.minecraft;
      SocialInteractionsScreen var10003 = this.interactionsScreen;
      String var10005 = playerInfo.getProfile().name();
      Objects.requireNonNull(playerInfo);
      return new PlayerEntry(var10002, var10003, id, var10005, visibility, playerInfo::getSkin, playerInfo.hasVerifiableChat());
   }

   private void updatePlayersFromChatLog(final Map<UUID, PlayerEntry> entries, final boolean addOfflineEntries) {
      Map<UUID, GameProfile> gameProfiles = collectProfilesFromChatLog(this.minecraft.getReportingContext().chatLog());
      gameProfiles.forEach((id, gameProfile) -> {
         if (SharedConstants.DEBUG_SOCIAL_INTERACTIONS || !this.minecraft.isLocalPlayer(id)) {
            PlayerEntry entry;
            if (addOfflineEntries) {
               entry = (PlayerEntry)entries.computeIfAbsent(id, (var3) -> {
                  PlayerSocialManager.Visibility visibility = this.minecraft.getPlayerSocialManager().getVisibility(id);
                  PlayerEntry player = new PlayerEntry(this.minecraft, this.interactionsScreen, gameProfile.id(), gameProfile.name(), visibility, this.minecraft.getSkinManager().createLookup(gameProfile, true), true);
                  player.setRemoved(true);
                  return player;
               });
            } else {
               entry = (PlayerEntry)entries.get(id);
               if (entry == null) {
                  return;
               }
            }

            entry.setHasRecentMessages(true);
         }
      });
   }

   public static Map<UUID, GameProfile> collectProfilesFromChatLog(final ChatLog chatLog) {
      Map<UUID, GameProfile> gameProfiles = new Object2ObjectLinkedOpenHashMap();

      for(int id = chatLog.end(); id >= chatLog.start(); --id) {
         LoggedChatEvent event = chatLog.lookup(id);
         if (event instanceof LoggedChatMessage.Player message) {
            if (message.message().hasSignature()) {
               gameProfiles.put(message.profileId(), message.profile());
            }
         }
      }

      return gameProfiles;
   }

   private void updateFiltersAndScroll(final Collection<PlayerEntry> newEntries) {
      this.players.clear();
      if (newEntries.isEmpty()) {
         if (this.interactionsTab != null) {
            this.interactionsTab.showEmpty();
         }
      } else {
         this.players.addAll(newEntries);
         this.filter.filter(this.players);
         this.players.sort(this.comparator);
         if (this.interactionsTab != null) {
            this.interactionsTab.replaceEntries(this.players, this.filter.isEmpty());
         }
      }

   }

   public boolean isEmpty() {
      return this.players.isEmpty();
   }

   public void addPlayer(final PlayerInfo player, final SocialInteractionsScreen.Page page) {
      UUID playerId = player.getProfile().id();
      String playerName = player.getProfile().name();

      for(PlayerEntry playerEntry : this.players) {
         if (playerEntry.playerId().equals(playerId)) {
            playerEntry.setRemoved(false);
            return;
         }
      }

      if (page == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom(playerId)) {
         if (!this.filter.matchesFilter(playerName)) {
            return;
         }

         PlayerSocialManager.Visibility visibility = this.minecraft.getPlayerSocialManager().getVisibility(playerId);
         boolean chatReportable = player.hasVerifiableChat();
         Minecraft var10002 = this.minecraft;
         SocialInteractionsScreen var10003 = this.interactionsScreen;
         Objects.requireNonNull(player);
         PlayerEntry playerEntry = new PlayerEntry(var10002, var10003, playerId, playerName, visibility, player::getSkin, chatReportable);
         this.players.add(playerEntry);
         if (this.interactionsTab != null) {
            this.interactionsTab.addEntry(playerEntry, this.filter.isEmpty());
         }
      }

   }

   public void removePlayer(final UUID id) {
      for(PlayerEntry playerEntry : this.players) {
         if (playerEntry.playerId().equals(id)) {
            playerEntry.setRemoved(true);
            return;
         }
      }

   }

   private static boolean isValidPlayerName(final String name) {
      if (name.isBlank()) {
         return false;
      } else {
         int firstCodepoint = name.codePointAt(0);
         return firstCodepoint == 95 || firstCodepoint >= 97 && firstCodepoint <= 122 || firstCodepoint >= 65 && firstCodepoint <= 90 || firstCodepoint >= 48 && firstCodepoint <= 57;
      }
   }
}
