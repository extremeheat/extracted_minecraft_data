package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.PlayerTeam;

public class TeleportToTeamMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private final List items = Lists.newArrayList();

   public TeleportToTeamMenuCategory() {
      Minecraft var1 = Minecraft.getInstance();
      Iterator var2 = var1.level.getScoreboard().getPlayerTeams().iterator();

      while(var2.hasNext()) {
         PlayerTeam var3 = (PlayerTeam)var2.next();
         this.items.add(new TeleportToTeamMenuCategory.TeamSelectionItem(var3));
      }

   }

   public List getItems() {
      return this.items;
   }

   public Component getPrompt() {
      return new TranslatableComponent("spectatorMenu.team_teleport.prompt", new Object[0]);
   }

   public void selectItem(SpectatorMenu var1) {
      var1.selectCategory(this);
   }

   public Component getName() {
      return new TranslatableComponent("spectatorMenu.team_teleport", new Object[0]);
   }

   public void renderIcon(float var1, int var2) {
      Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
      GuiComponent.blit(0, 0, 16.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      Iterator var1 = this.items.iterator();

      SpectatorMenuItem var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (SpectatorMenuItem)var1.next();
      } while(!var2.isEnabled());

      return true;
   }

   class TeamSelectionItem implements SpectatorMenuItem {
      private final PlayerTeam team;
      private final ResourceLocation location;
      private final List players;

      public TeamSelectionItem(PlayerTeam var2) {
         this.team = var2;
         this.players = Lists.newArrayList();
         Iterator var3 = var2.getPlayers().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            PlayerInfo var5 = Minecraft.getInstance().getConnection().getPlayerInfo(var4);
            if (var5 != null) {
               this.players.add(var5);
            }
         }

         if (this.players.isEmpty()) {
            this.location = DefaultPlayerSkin.getDefaultSkin();
         } else {
            String var6 = ((PlayerInfo)this.players.get((new Random()).nextInt(this.players.size()))).getProfile().getName();
            this.location = AbstractClientPlayer.getSkinLocation(var6);
            AbstractClientPlayer.registerSkinTexture(this.location, var6);
         }

      }

      public void selectItem(SpectatorMenu var1) {
         var1.selectCategory(new TeleportToPlayerMenuCategory(this.players));
      }

      public Component getName() {
         return this.team.getDisplayName();
      }

      public void renderIcon(float var1, int var2) {
         Integer var3 = this.team.getColor().getColor();
         if (var3 != null) {
            float var4 = (float)(var3 >> 16 & 255) / 255.0F;
            float var5 = (float)(var3 >> 8 & 255) / 255.0F;
            float var6 = (float)(var3 & 255) / 255.0F;
            GuiComponent.fill(1, 1, 15, 15, Mth.color(var4 * var1, var5 * var1, var6 * var1) | var2 << 24);
         }

         Minecraft.getInstance().getTextureManager().bind(this.location);
         RenderSystem.color4f(var1, var1, var1, (float)var2 / 255.0F);
         GuiComponent.blit(2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
         GuiComponent.blit(2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
      }

      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
