package net.minecraft.client.gui.spectator.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeleportToTeamMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Identifier TELEPORT_TO_TEAM_SPRITE = Identifier.withDefaultNamespace("spectator/teleport_to_team");
   private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.team_teleport");
   private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.team_teleport.prompt");
   private final List<SpectatorMenuItem> items;

   public TeleportToTeamMenuCategory() {
      super();
      Minecraft minecraft = Minecraft.getInstance();
      this.items = createTeamEntries(minecraft, minecraft.level.getScoreboard());
   }

   private static List<SpectatorMenuItem> createTeamEntries(final Minecraft minecraft, final Scoreboard scoreboard) {
      return scoreboard.getPlayerTeams().stream().flatMap((team) -> TeleportToTeamMenuCategory.TeamSelectionItem.create(minecraft, team).stream()).toList();
   }

   public List<SpectatorMenuItem> getItems() {
      return this.items;
   }

   public Component getPrompt() {
      return TELEPORT_PROMPT;
   }

   public void selectItem(final SpectatorMenu menu) {
      menu.selectCategory(this);
   }

   public Component getName() {
      return TELEPORT_TEXT;
   }

   public void renderIcon(final GuiGraphics graphics, final float brightness, final float alpha) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)TELEPORT_TO_TEAM_SPRITE, 0, 0, 16, 16, ARGB.colorFromFloat(alpha, brightness, brightness, brightness));
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }

   private static class TeamSelectionItem implements SpectatorMenuItem {
      private final PlayerTeam team;
      private final Supplier<PlayerSkin> iconSkin;
      private final List<PlayerInfo> players;

      private TeamSelectionItem(final PlayerTeam team, final List<PlayerInfo> players, final Supplier<PlayerSkin> iconSkin) {
         super();
         this.team = team;
         this.players = players;
         this.iconSkin = iconSkin;
      }

      public static Optional<SpectatorMenuItem> create(final Minecraft minecraft, final PlayerTeam team) {
         List<PlayerInfo> players = new ArrayList();

         for(String name : team.getPlayers()) {
            PlayerInfo info = minecraft.getConnection().getPlayerInfo(name);
            if (info != null && info.getGameMode() != GameType.SPECTATOR) {
               players.add(info);
            }
         }

         if (players.isEmpty()) {
            return Optional.empty();
         } else {
            PlayerInfo playerInfo = (PlayerInfo)players.get(RandomSource.createThreadLocalInstance().nextInt(players.size()));
            Objects.requireNonNull(playerInfo);
            return Optional.of(new TeamSelectionItem(team, players, playerInfo::getSkin));
         }
      }

      public void selectItem(final SpectatorMenu menu) {
         menu.selectCategory(new TeleportToPlayerMenuCategory(this.players));
      }

      public Component getName() {
         return this.team.getDisplayName();
      }

      public void renderIcon(final GuiGraphics graphics, final float brightness, final float alpha) {
         Integer teamColor = this.team.getColor().getColor();
         if (teamColor != null) {
            float red = (float)(teamColor >> 16 & 255) / 255.0F;
            float green = (float)(teamColor >> 8 & 255) / 255.0F;
            float blue = (float)(teamColor & 255) / 255.0F;
            graphics.fill(1, 1, 15, 15, ARGB.colorFromFloat(alpha, red * brightness, green * brightness, blue * brightness));
         }

         PlayerFaceRenderer.draw(graphics, (PlayerSkin)this.iconSkin.get(), 2, 2, 12, ARGB.colorFromFloat(alpha, brightness, brightness, brightness));
      }

      public boolean isEnabled() {
         return true;
      }
   }
}
