package net.minecraft.client.gui.spectator.categories;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public class TeleportToPlayerMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final ResourceLocation TELEPORT_TO_PLAYER_SPRITE = ResourceLocation.withDefaultNamespace("spectator/teleport_to_player");
   private static final Comparator<PlayerInfo> PROFILE_ORDER = Comparator.comparing((var0) -> {
      return var0.getProfile().getId();
   });
   private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.teleport");
   private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.teleport.prompt");
   private final List<SpectatorMenuItem> items;

   public TeleportToPlayerMenuCategory() {
      this(Minecraft.getInstance().getConnection().getListedOnlinePlayers());
   }

   public TeleportToPlayerMenuCategory(Collection<PlayerInfo> var1) {
      super();
      this.items = var1.stream().filter((var0) -> {
         return var0.getGameMode() != GameType.SPECTATOR;
      }).sorted(PROFILE_ORDER).map((var0) -> {
         return new PlayerMenuItem(var0.getProfile());
      }).toList();
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

   public void renderIcon(GuiGraphics var1, float var2, int var3) {
      var1.blitSprite(TELEPORT_TO_PLAYER_SPRITE, 0, 0, 16, 16);
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}
