package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
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

   public void updatePlayerList(Collection<UUID> var1, double var2) {
      this.players.clear();

      for(UUID var5 : var1) {
         PlayerInfo var6 = this.minecraft.player.connection.getPlayerInfo(var5);
         if (var6 != null) {
            this.players
               .add(
                  new PlayerEntry(this.minecraft, this.socialInteractionsScreen, var6.getProfile().getId(), var6.getProfile().getName(), var6::getSkinLocation)
               );
         }
      }

      this.updateFilteredPlayers();
      this.players.sort((var0, var1x) -> var0.getPlayerName().compareToIgnoreCase(var1x.getPlayerName()));
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
         PlayerEntry var6 = new PlayerEntry(
            this.minecraft, this.socialInteractionsScreen, var1.getProfile().getId(), var1.getProfile().getName(), var1::getSkinLocation
         );
         this.addEntry(var6);
         this.players.add(var6);
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
