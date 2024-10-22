package net.minecraft.client.gui.spectator.categories;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeleportToTeamMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final ResourceLocation TELEPORT_TO_TEAM_SPRITE = ResourceLocation.withDefaultNamespace("spectator/teleport_to_team");
   private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.team_teleport");
   private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.team_teleport.prompt");
   private final List<SpectatorMenuItem> items;

   public TeleportToTeamMenuCategory() {
      super();
      Minecraft var1 = Minecraft.getInstance();
      this.items = createTeamEntries(var1, var1.level.getScoreboard());
   }

   private static List<SpectatorMenuItem> createTeamEntries(Minecraft var0, Scoreboard var1) {
      return var1.getPlayerTeams().stream().flatMap(var1x -> TeleportToTeamMenuCategory.TeamSelectionItem.create(var0, var1x).stream()).toList();
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
   public void renderIcon(GuiGraphics var1, float var2, float var3) {
      var1.blitSprite(RenderType::guiTextured, TELEPORT_TO_TEAM_SPRITE, 0, 0, 16, 16, ARGB.colorFromFloat(var3, var2, var2, var2));
   }

   @Override
   public boolean isEnabled() {
      return !this.items.isEmpty();
   }

   static class TeamSelectionItem implements SpectatorMenuItem {
      private final PlayerTeam team;
      private final Supplier<PlayerSkin> iconSkin;
      private final List<PlayerInfo> players;

      private TeamSelectionItem(PlayerTeam var1, List<PlayerInfo> var2, Supplier<PlayerSkin> var3) {
         super();
         this.team = var1;
         this.players = var2;
         this.iconSkin = var3;
      }

      public static Optional<SpectatorMenuItem> create(Minecraft var0, PlayerTeam var1) {
         ArrayList var2 = new ArrayList();

         for (String var4 : var1.getPlayers()) {
            PlayerInfo var5 = var0.getConnection().getPlayerInfo(var4);
            if (var5 != null && var5.getGameMode() != GameType.SPECTATOR) {
               var2.add(var5);
            }
         }

         if (var2.isEmpty()) {
            return Optional.empty();
         } else {
            GameProfile var6 = ((PlayerInfo)var2.get(RandomSource.create().nextInt(var2.size()))).getProfile();
            Supplier var7 = var0.getSkinManager().lookupInsecure(var6);
            return Optional.of(new TeleportToTeamMenuCategory.TeamSelectionItem(var1, var2, var7));
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
      public void renderIcon(GuiGraphics var1, float var2, float var3) {
         Integer var4 = this.team.getColor().getColor();
         if (var4 != null) {
            float var5 = (float)(var4 >> 16 & 0xFF) / 255.0F;
            float var6 = (float)(var4 >> 8 & 0xFF) / 255.0F;
            float var7 = (float)(var4 & 0xFF) / 255.0F;
            var1.fill(1, 1, 15, 15, ARGB.colorFromFloat(var3, var5 * var2, var6 * var2, var7 * var2));
         }

         PlayerFaceRenderer.draw(var1, this.iconSkin.get(), 2, 2, 12, ARGB.colorFromFloat(var3, var2, var2, var2));
      }

      @Override
      public boolean isEnabled() {
         return true;
      }
   }
}
