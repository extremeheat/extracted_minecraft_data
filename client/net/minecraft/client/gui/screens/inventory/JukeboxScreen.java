package net.minecraft.client.gui.screens.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.JukeboxMenu;
import net.minecraft.world.item.ItemStack;

public class JukeboxScreen extends ContainerScreen {
   private static final Component PLAYING_NEXT_TOOLTIP = Component.translatable("record.playingNext");
   private final JukeboxMenu jukeboxMenu;

   public JukeboxScreen(final ChestMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title);
      this.jukeboxMenu = (JukeboxMenu)menu;
   }

   protected List<Component> getTooltipFromContainerItem(final ItemStack itemStack) {
      List<Component> tooltip = new ArrayList(super.getTooltipFromContainerItem(itemStack));
      if (this.jukeboxMenu != null && this.jukeboxMenu.getPlayingNextPos().isPresent() && this.hoveredSlot.index == this.jukeboxMenu.getPlayingNextPos().getAsInt()) {
         tooltip.add(PLAYING_NEXT_TOOLTIP);
      }

      return tooltip;
   }
}
