package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;

public class SocialInteractionsPlayerList extends ContainerObjectSelectionList<PlayerEntry> {
   private final SocialInteractionsScreen socialInteractionsScreen;
   private final List<PlayerEntry> players = Lists.newArrayList();
   @Nullable
   private String filter;

   public SocialInteractionsPlayerList(SocialInteractionsScreen var1, Minecraft var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5, var6);
      this.socialInteractionsScreen = var1;
   }

   protected void renderListBackground(GuiGraphics var1) {
   }

   protected void renderListSeparators(GuiGraphics var1) {
   }

   protected void enableScissor(GuiGraphics var1) {
      var1.enableScissor(this.getX(), this.getY() + 4, this.getRight(), this.getBottom());
   }

   public void updatePlayerList(Collection<UUID> var1, double var2, boolean var4) {
      HashMap var5 = new HashMap();
      this.addOnlinePlayers(var1, var5);
      if (var4) {
         this.addSeenPlayers(var5);
      }

      this.updatePlayersFromChatLog(var5, var4);
      this.updateFiltersAndScroll(var5.values(), var2);
   }

   private void addOnlinePlayers(Collection<UUID> var1, Map<UUID, PlayerEntry> var2) {
      ClientPacketListener var3 = this.minecraft.player.connection;

      for(UUID var5 : var1) {
         PlayerInfo var6 = var3.getPlayerInfo(var5);
         if (var6 != null) {
            PlayerEntry var7 = this.makePlayerEntry(var5, var6);
            var2.put(var5, var7);
         }
      }

   }

   private void addSeenPlayers(Map<UUID, PlayerEntry> var1) {
      Map var2 = this.minecraft.player.connection.getSeenPlayers();

      for(Map.Entry var4 : var2.entrySet()) {
         var1.computeIfAbsent((UUID)var4.getKey(), (var2x) -> {
            PlayerEntry var3 = this.makePlayerEntry(var2x, (PlayerInfo)var4.getValue());
            var3.setRemoved(true);
            return var3;
         });
      }

   }

   private PlayerEntry makePlayerEntry(UUID var1, PlayerInfo var2) {
      Minecraft var10002 = this.minecraft;
      SocialInteractionsScreen var10003 = this.socialInteractionsScreen;
      String var10005 = var2.getProfile().name();
      Objects.requireNonNull(var2);
      return new PlayerEntry(var10002, var10003, var1, var10005, var2::getSkin, var2.hasVerifiableChat());
   }

   private void updatePlayersFromChatLog(Map<UUID, PlayerEntry> var1, boolean var2) {
      Map var3 = collectProfilesFromChatLog(this.minecraft.getReportingContext().chatLog());
      var3.forEach((var3x, var4) -> {
         PlayerEntry var5;
         if (var2) {
            var5 = (PlayerEntry)var1.computeIfAbsent(var3x, (var2x) -> {
               PlayerEntry var3 = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, var4.id(), var4.name(), this.minecraft.getSkinManager().createLookup(var4, true), true);
               var3.setRemoved(true);
               return var3;
            });
         } else {
            var5 = (PlayerEntry)var1.get(var3x);
            if (var5 == null) {
               return;
            }
         }

         var5.setHasRecentMessages(true);
      });
   }

   private static Map<UUID, GameProfile> collectProfilesFromChatLog(ChatLog var0) {
      Object2ObjectLinkedOpenHashMap var1 = new Object2ObjectLinkedOpenHashMap();

      for(int var2 = var0.end(); var2 >= var0.start(); --var2) {
         LoggedChatEvent var3 = var0.lookup(var2);
         if (var3 instanceof LoggedChatMessage.Player var4) {
            if (var4.message().hasSignature()) {
               var1.put(var4.profileId(), var4.profile());
            }
         }
      }

      return var1;
   }

   private void sortPlayerEntries() {
      this.players.sort(Comparator.comparing((var1) -> {
         if (this.minecraft.isLocalPlayer(var1.getPlayerId())) {
            return 0;
         } else if (this.minecraft.getReportingContext().hasDraftReportFor(var1.getPlayerId())) {
            return 1;
         } else if (var1.getPlayerId().version() == 2) {
            return 4;
         } else {
            return var1.hasRecentMessages() ? 2 : 3;
         }
      }).thenComparing((var0) -> {
         if (!var0.getPlayerName().isBlank()) {
            int var1 = var0.getPlayerName().codePointAt(0);
            if (var1 == 95 || var1 >= 97 && var1 <= 122 || var1 >= 65 && var1 <= 90 || var1 >= 48 && var1 <= 57) {
               return 0;
            }
         }

         return 1;
      }).thenComparing(PlayerEntry::getPlayerName, String::compareToIgnoreCase));
   }

   private void updateFiltersAndScroll(Collection<PlayerEntry> var1, double var2) {
      this.players.clear();
      this.players.addAll(var1);
      this.sortPlayerEntries();
      this.updateFilteredPlayers();
      this.replaceEntries(this.players);
      this.setScrollAmount(var2);
   }

   private void updateFilteredPlayers() {
      if (this.filter != null) {
         this.players.removeIf((var1) -> !var1.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter));
         this.replaceEntries(this.players);
      }

   }

   public void setFilter(String var1) {
      this.filter = var1;
   }

   public boolean isEmpty() {
      return this.players.isEmpty();
   }

   public void addPlayer(PlayerInfo var1, SocialInteractionsScreen.Page var2) {
      UUID var3 = var1.getProfile().id();

      for(PlayerEntry var5 : this.players) {
         if (var5.getPlayerId().equals(var3)) {
            var5.setRemoved(false);
            return;
         }
      }

      if ((var2 == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom(var3)) && (Strings.isNullOrEmpty(this.filter) || var1.getProfile().name().toLowerCase(Locale.ROOT).contains(this.filter))) {
         boolean var6 = var1.hasVerifiableChat();
         Minecraft var10002 = this.minecraft;
         SocialInteractionsScreen var10003 = this.socialInteractionsScreen;
         UUID var10004 = var1.getProfile().id();
         String var10005 = var1.getProfile().name();
         Objects.requireNonNull(var1);
         PlayerEntry var7 = new PlayerEntry(var10002, var10003, var10004, var10005, var1::getSkin, var6);
         this.addEntry(var7);
         this.players.add(var7);
      }

   }

   public void removePlayer(UUID var1) {
      for(PlayerEntry var3 : this.players) {
         if (var3.getPlayerId().equals(var1)) {
            var3.setRemoved(true);
            return;
         }
      }

   }

   public void refreshHasDraftReport() {
      this.players.forEach((var1) -> var1.refreshHasDraftReport(this.minecraft.getReportingContext()));
   }
}
