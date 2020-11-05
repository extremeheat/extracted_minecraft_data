package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
   private static final Component TELEPORT_TEXT = new TranslatableComponent("spectatorMenu.team_teleport");
   private static final Component TELEPORT_PROMPT = new TranslatableComponent("spectatorMenu.team_teleport.prompt");
   private final List<SpectatorMenuItem> items = Lists.newArrayList();

   public TeleportToTeamMenuCategory() {
      super();
      Minecraft var1 = Minecraft.getInstance();
      Iterator var2 = var1.level.getScoreboard().getPlayerTeams().iterator();

      while(var2.hasNext()) {
         PlayerTeam var3 = (PlayerTeam)var2.next();
         this.items.add(new TeleportToTeamMenuCategory.TeamSelectionItem(var3));
      }

   }

   public List<SpectatorMenuItem> getItems() {
      return this.items;
   }

   public Component getPrompt() {
      return TELEPORT_PROMPT;
   }

   public void selectItem(SpectatorMenu var1) {
      var1.selectCategory(this);
   }

   public Component getName() {
      return TELEPORT_TEXT;
   }

   public void renderIcon(PoseStack var1, float var2, int var3) {
      Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
      GuiComponent.blit(var1, 0, 0, 16.0F, 0.0F, 16, 16, 256, 256);
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
      private final List<PlayerInfo> players;

      public TeamSelectionItem(PlayerTeam var2) {
         super();
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

      public void renderIcon(PoseStack var1, float var2, int var3) {
         Integer var4 = this.team.getColor().getColor();
         if (var4 != null) {
            float var5 = (float)(var4 >> 16 & 255) / 255.0F;
            float var6 = (float)(var4 >> 8 & 255) / 255.0F;
            float var7 = (float)(var4 & 255) / 255.0F;
            GuiComponent.fill(var1, 1, 1, 15, 15, Mth.color(var5 * var2, var6 * var2, var7 * var2) | var3 << 24);
         }

         Minecraft.getInstance().getTextureManager().bind(this.location);
         RenderSystem.color4f(var2, var2, var2, (float)var3 / 255.0F);
         GuiComponent.blit(var1, 2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
         GuiComponent.blit(var1, 2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
      }

      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
