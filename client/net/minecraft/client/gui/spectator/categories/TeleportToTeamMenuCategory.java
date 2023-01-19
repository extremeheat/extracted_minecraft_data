package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.scores.PlayerTeam;

public class TeleportToTeamMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.team_teleport");
   private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.team_teleport.prompt");
   private final List<SpectatorMenuItem> items = Lists.newArrayList();

   public TeleportToTeamMenuCategory() {
      super();
      Minecraft var1 = Minecraft.getInstance();

      for(PlayerTeam var3 : var1.level.getScoreboard().getPlayerTeams()) {
         this.items.add(new TeleportToTeamMenuCategory.TeamSelectionItem(var3));
      }
   }

   @Override
   public List<SpectatorMenuItem> getItems() {
      return this.items;
   }

   @Override
   public Component getPrompt() {
      return TELEPORT_PROMPT;
   }

   @Override
   public void selectItem(SpectatorMenu var1) {
      var1.selectCategory(this);
   }

   @Override
   public Component getName() {
      return TELEPORT_TEXT;
   }

   @Override
   public void renderIcon(PoseStack var1, float var2, int var3) {
      RenderSystem.setShaderTexture(0, SpectatorGui.SPECTATOR_LOCATION);
      GuiComponent.blit(var1, 0, 0, 16.0F, 0.0F, 16, 16, 256, 256);
   }

   @Override
   public boolean isEnabled() {
      for(SpectatorMenuItem var2 : this.items) {
         if (var2.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   static class TeamSelectionItem implements SpectatorMenuItem {
      private final PlayerTeam team;
      private final ResourceLocation location;
      private final List<PlayerInfo> players;

      public TeamSelectionItem(PlayerTeam var1) {
         super();
         this.team = var1;
         this.players = Lists.newArrayList();

         for(String var3 : var1.getPlayers()) {
            PlayerInfo var4 = Minecraft.getInstance().getConnection().getPlayerInfo(var3);
            if (var4 != null) {
               this.players.add(var4);
            }
         }

         if (this.players.isEmpty()) {
            this.location = DefaultPlayerSkin.getDefaultSkin();
         } else {
            String var5 = this.players.get(RandomSource.create().nextInt(this.players.size())).getProfile().getName();
            this.location = AbstractClientPlayer.getSkinLocation(var5);
            AbstractClientPlayer.registerSkinTexture(this.location, var5);
         }
      }

      @Override
      public void selectItem(SpectatorMenu var1) {
         var1.selectCategory(new TeleportToPlayerMenuCategory(this.players));
      }

      @Override
      public Component getName() {
         return this.team.getDisplayName();
      }

      @Override
      public void renderIcon(PoseStack var1, float var2, int var3) {
         Integer var4 = this.team.getColor().getColor();
         if (var4 != null) {
            float var5 = (float)(var4 >> 16 & 0xFF) / 255.0F;
            float var6 = (float)(var4 >> 8 & 0xFF) / 255.0F;
            float var7 = (float)(var4 & 0xFF) / 255.0F;
            GuiComponent.fill(var1, 1, 1, 15, 15, Mth.color(var5 * var2, var6 * var2, var7 * var2) | var3 << 24);
         }

         RenderSystem.setShaderTexture(0, this.location);
         RenderSystem.setShaderColor(var2, var2, var2, (float)var3 / 255.0F);
         GuiComponent.blit(var1, 2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
         GuiComponent.blit(var1, 2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
      }

      @Override
      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
