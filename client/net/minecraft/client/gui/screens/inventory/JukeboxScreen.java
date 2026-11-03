package net.minecraft.client.gui.screens.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.JukeboxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class JukeboxScreen extends ContainerScreen {
   private static final Component PLAYING_NEXT_TOOLTIP = Component.translatable("record.playingNext");
   private final JukeboxMenu jukeboxMenu;

   public JukeboxScreen(final ChestMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title);
      this.jukeboxMenu = (JukeboxMenu)menu;
   }

   protected void updateSlotWidgets() {
      List<SlotWidget> widgets = new ArrayList();

      for(Slot slot : (this.menu).slots) {
         widgets.add(new SlotWidget(slot, this.minecraft, this.imageWidth, this.leftPos, this.topPos, this::createSlotTooltip, () -> null));
      }

      this.setSlotWidgets(widgets);
   }

   private Tooltip createSlotTooltip(final Slot slot) {
      ItemStack itemStack = slot.getItem();
      List<Component> tooltip = new ArrayList(getTooltipFromItem(this.minecraft, itemStack));
      if (this.jukeboxMenu.getPlayingNextPos().isPresent() && slot.index == this.jukeboxMenu.getPlayingNextPos().getAsInt()) {
         tooltip.add(PLAYING_NEXT_TOOLTIP);
      }

      return Tooltip.createForItem(tooltip, itemStack);
   }
}
