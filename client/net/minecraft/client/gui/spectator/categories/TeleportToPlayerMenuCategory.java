package net.minecraft.client.gui.spectator.categories;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

public class TeleportToPlayerMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Comparator<PlayerInfo> PROFILE_ORDER = Comparator.comparing(var0 -> var0.getProfile().getId());
   private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.teleport");
   private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.teleport.prompt");
   private final List<SpectatorMenuItem> items;

   public TeleportToPlayerMenuCategory() {
      this(Minecraft.getInstance().getConnection().getListedOnlinePlayers());
   }

   public TeleportToPlayerMenuCategory(Collection<PlayerInfo> var1) {
      super();
      this.items = var1.stream()
         .filter(var0 -> var0.getGameMode() != GameType.SPECTATOR)
         .sorted(PROFILE_ORDER)
         .map(var0 -> new PlayerMenuItem(var0.getProfile()))
         .toList();
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
      GuiComponent.blit(var1, 0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
   }

   @Override
   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}
