package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      double var5 = this.minecraft.getWindow().getGuiScale();
      RenderSystem.enableScissor((int)((double)this.getRowLeft() * var5), (int)((double)(this.height - this.y1) * var5), (int)((double)(this.getScrollbarPosition() + 6) * var5), (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * var5));
      super.render(var1, var2, var3, var4);
      RenderSystem.disableScissor();
   }

   public void updatePlayerList(Collection<UUID> var1, double var2) {
      this.players.clear();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         UUID var5 = (UUID)var4.next();
         PlayerInfo var6 = this.minecraft.player.connection.getPlayerInfo(var5);
         if (var6 != null) {
            List var10000 = this.players;
            Minecraft var10003 = this.minecraft;
            SocialInteractionsScreen var10004 = this.socialInteractionsScreen;
            UUID var10005 = var6.getProfile().getId();
            String var10006 = var6.getProfile().getName();
            Objects.requireNonNull(var6);
            var10000.add(new PlayerEntry(var10003, var10004, var10005, var10006, var6::getSkinLocation));
         }
      }

      this.updateFilteredPlayers();
      this.players.sort((var0, var1x) -> {
         return var0.getPlayerName().compareToIgnoreCase(var1x.getPlayerName());
      });
      this.replaceEntries(this.players);
      this.setScrollAmount(var2);
   }

   private void updateFilteredPlayers() {
      if (this.filter != null) {
         this.players.removeIf((var1) -> {
            return !var1.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter);
         });
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
      Iterator var4 = this.players.iterator();

      while(var4.hasNext()) {
         PlayerEntry var5 = (PlayerEntry)var4.next();
         if (var5.getPlayerId().equals(var3)) {
            var5.setRemoved(false);
            return;
         }
      }

      if ((var2 == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom(var3)) && (Strings.isNullOrEmpty(this.filter) || var1.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.filter))) {
         Minecraft var10002 = this.minecraft;
         SocialInteractionsScreen var10003 = this.socialInteractionsScreen;
         UUID var10004 = var1.getProfile().getId();
         String var10005 = var1.getProfile().getName();
         Objects.requireNonNull(var1);
         PlayerEntry var6 = new PlayerEntry(var10002, var10003, var10004, var10005, var1::getSkinLocation);
         this.addEntry(var6);
         this.players.add(var6);
      }

   }

   public void removePlayer(UUID var1) {
      Iterator var2 = this.players.iterator();

      PlayerEntry var3;
      do {
         if (!var2.hasNext()) {
            return;
         }

         var3 = (PlayerEntry)var2.next();
      } while(!var3.getPlayerId().equals(var1));

      var3.setRemoved(true);
   }
}
