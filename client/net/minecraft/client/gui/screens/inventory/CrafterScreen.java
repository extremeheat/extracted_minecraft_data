package net.minecraft.client.gui.screens.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.inventory.CrafterSlotWidget;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CrafterSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class CrafterScreen extends AbstractContainerScreen<CrafterMenu> {
   private static final Identifier POWERED_REDSTONE_LOCATION_SPRITE = Identifier.withDefaultNamespace("container/crafter/powered_redstone");
   private static final Identifier UNPOWERED_REDSTONE_LOCATION_SPRITE = Identifier.withDefaultNamespace("container/crafter/unpowered_redstone");
   private static final Identifier CONTAINER_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/crafter.png");
   private final Player player;

   public CrafterScreen(final CrafterMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title);
      this.player = inventory.player;
   }

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
   }

   protected void updateSlotWidgets() {
      List<SlotWidget> widgets = new ArrayList();

      for(Slot slot : (this.menu).slots) {
         if (slot instanceof CrafterSlot crafterSlot) {
            widgets.add(new CrafterSlotWidget(crafterSlot, this.minecraft, this.imageWidth, this.leftPos, this.topPos));
         } else {
            widgets.add(new SlotWidget(slot, this.minecraft, this.imageWidth, this.leftPos, this.topPos));
         }
      }

      this.setSlotWidgets(widgets);
   }

   protected void slotClicked(final @Nullable SlotWidget slotWidget, final int slotId, final int buttonNum, final ContainerInput containerInput) {
      if (slotWidget != null) {
         Slot var6 = slotWidget.slot();
         if (var6 instanceof CrafterSlot) {
            CrafterSlot crafterSlot = (CrafterSlot)var6;
            if (!crafterSlot.hasItem() && !this.player.isSpectator()) {
               switch (containerInput) {
                  case PICKUP:
                     if (crafterSlot.isDisabled()) {
                        this.enableSlot(slotId);
                     } else if (((CrafterMenu)this.menu).getCarried().isEmpty()) {
                        this.disableSlot(slotId);
                     }
                     break;
                  case SWAP:
                     ItemStack playerInventoryItem = this.player.getInventory().getItem(buttonNum);
                     if (crafterSlot.isDisabled() && !playerInventoryItem.isEmpty()) {
                        this.enableSlot(slotId);
                     }
               }
            }
         }
      }

      super.slotClicked(slotWidget, slotId, buttonNum, containerInput);
   }

   private void enableSlot(final int slotId) {
      this.updateSlotState(slotId, true);
   }

   private void disableSlot(final int slotId) {
      this.updateSlotState(slotId, false);
   }

   private void updateSlotState(final int slotId, final boolean enabled) {
      ((CrafterMenu)this.menu).setSlotState(slotId, enabled);
      super.handleSlotStateChanged(slotId, (this.menu).containerId, enabled);
      float pitch = enabled ? 1.0F : 0.75F;
      this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, pitch);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics) {
      super.extractRenderState(graphics);
      this.extractRedstone(graphics);
   }

   private void extractRedstone(final GuiGraphicsExtractor graphics) {
      int xo = this.width / 2 + 9;
      int yo = this.height / 2 - 48;
      Identifier redstoneArrowTexture;
      if (((CrafterMenu)this.menu).isPowered()) {
         redstoneArrowTexture = POWERED_REDSTONE_LOCATION_SPRITE;
      } else {
         redstoneArrowTexture = UNPOWERED_REDSTONE_LOCATION_SPRITE;
      }

      graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)redstoneArrowTexture, xo, yo, 16, 16);
   }

   public void extractBackground(final GuiGraphicsExtractor graphics) {
      super.extractBackground(graphics);
      int xo = (this.width - this.imageWidth) / 2;
      int yo = (this.height - this.imageHeight) / 2;
      graphics.blit(GuiPipeline.TEXTURED, CONTAINER_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }
}
