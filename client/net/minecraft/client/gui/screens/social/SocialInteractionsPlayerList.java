package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

public class SocialInteractionsPlayerList extends ContainerObjectSelectionList<PlayerEntry> {
   private final SocialInteractionsScreen socialInteractionsScreen;
   private final List<PlayerEntry> players = Lists.newArrayList();
   @Nullable
   private String filter;

   public SocialInteractionsPlayerList(SocialInteractionsScreen var1, Minecraft var2, int var3, int var4, int var5, int var6, int var7) {
      super(var2, var3, var4, var5, var6, var7);
      this.socialInteractionsScreen = var1;
      this.setRenderBackground(false);
      this.setRenderTopAndBottom(false);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      double var5 = this.minecraft.getWindow().getGuiScale();
      RenderSystem.enableScissor(
         (int)((double)this.getRowLeft() * var5),
         (int)((double)(this.height - this.y1) * var5),
         (int)((double)(this.getScrollbarPosition() + 6) * var5),
         (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * var5)
      );
      super.render(var1, var2, var3, var4);
      RenderSystem.disableScissor();
   }

   public void updatePlayerList(Collection<UUID> var1, double var2, boolean var4) {
      HashMap var5 = new HashMap();
      this.addOnlinePlayers(var1, var5);
      this.updatePlayersFromChatLog(var5, var4);
      this.updateFiltersAndScroll(var5.values(), var2);
   }

   private void addOnlinePlayers(Collection<UUID> var1, Map<UUID, PlayerEntry> var2) {
      ClientPacketListener var3 = this.minecraft.player.connection;

      for(UUID var5 : var1) {
         PlayerInfo var6 = var3.getPlayerInfo(var5);
         if (var6 != null) {
            UUID var7 = var6.getProfile().getId();
            boolean var8 = var6.getProfilePublicKey() != null;
            var2.put(var7, new PlayerEntry(this.minecraft, this.socialInteractionsScreen, var7, var6.getProfile().getName(), var6::getSkinLocation, var8));
         }
      }
   }

   private void updatePlayersFromChatLog(Map<UUID, PlayerEntry> var1, boolean var2) {
      for(GameProfile var5 : this.minecraft.getReportingContext().chatLog().selectAllDescending().reportableGameProfiles()) {
         PlayerEntry var6;
         if (var2) {
            var6 = var1.computeIfAbsent(
               var5.getId(),
               var2x -> {
                  PlayerEntry var3 = new PlayerEntry(
                     this.minecraft,
                     this.socialInteractionsScreen,
                     var5.getId(),
                     var5.getName(),
                     Suppliers.memoize(() -> this.minecraft.getSkinManager().getInsecureSkinLocation(var5)),
                     true
                  );
                  var3.setRemoved(true);
                  return var3;
               }
            );
         } else {
            var6 = (PlayerEntry)var1.get(var5.getId());
            if (var6 == null) {
               continue;
            }
         }

         var6.setHasRecentMessages(true);
      }
   }

   private void sortPlayerEntries() {
      this.players.sort(Comparator.<PlayerEntry, Integer>comparing(var1 -> {
         if (var1.getPlayerId().equals(this.minecraft.getUser().getProfileId())) {
            return 0;
         } else if (var1.getPlayerId().version() == 2) {
            return 3;
         } else {
            return var1.hasRecentMessages() ? 1 : 2;
         }
      }).thenComparing(var0 -> {
         int var1 = var0.getPlayerName().codePointAt(0);
         return var1 != 95 && (var1 < 97 || var1 > 122) && (var1 < 65 || var1 > 90) && (var1 < 48 || var1 > 57) ? 1 : 0;
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
         this.players.removeIf(var1 -> !var1.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter));
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
      UUID var3 = var1.getProfile().getId();

      for(PlayerEntry var5 : this.players) {
         if (var5.getPlayerId().equals(var3)) {
            var5.setRemoved(false);
            return;
         }
      }

      if ((var2 == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom(var3))
         && (Strings.isNullOrEmpty(this.filter) || var1.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.filter))) {
         boolean var6 = var1.getProfilePublicKey() != null;
         PlayerEntry var7 = new PlayerEntry(
            this.minecraft, this.socialInteractionsScreen, var1.getProfile().getId(), var1.getProfile().getName(), var1::getSkinLocation, var6
         );
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
}
