package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Iterator;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;

public class TeleportToPlayerMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Ordering<PlayerInfo> PROFILE_ORDER = Ordering.from((var0, var1) -> {
      return ComparisonChain.start().compare(var0.getProfile().getId(), var1.getProfile().getId()).result();
   });
   private static final Component TELEPORT_TEXT = new TranslatableComponent("spectatorMenu.teleport");
   private static final Component TELEPORT_PROMPT = new TranslatableComponent("spectatorMenu.teleport.prompt");
   private final List<SpectatorMenuItem> items;

   public TeleportToPlayerMenuCategory() {
      this(PROFILE_ORDER.sortedCopy(Minecraft.getInstance().getConnection().getOnlinePlayers()));
   }

   public TeleportToPlayerMenuCategory(Collection<PlayerInfo> var1) {
      super();
      this.items = Lists.newArrayList();
      Iterator var2 = PROFILE_ORDER.sortedCopy(var1).iterator();

      while(var2.hasNext()) {
         PlayerInfo var3 = (PlayerInfo)var2.next();
         if (var3.getGameMode() != GameType.SPECTATOR) {
            this.items.add(new PlayerMenuItem(var3.getProfile()));
         }
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
      GuiComponent.blit(var1, 0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}
