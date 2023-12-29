package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CrafterSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CrafterScreen extends AbstractContainerScreen<CrafterMenu> {
   private static final ResourceLocation DISABLED_SLOT_LOCATION_SPRITE = new ResourceLocation("container/crafter/disabled_slot");
   private static final ResourceLocation POWERED_REDSTONE_LOCATION_SPRITE = new ResourceLocation("container/crafter/powered_redstone");
   private static final ResourceLocation UNPOWERED_REDSTONE_LOCATION_SPRITE = new ResourceLocation("container/crafter/unpowered_redstone");
   private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation("textures/gui/container/crafter.png");
   private static final Component DISABLED_SLOT_TOOLTIP = Component.translatable("gui.togglable_slot");
   private final Player player;

   public CrafterScreen(CrafterMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.player = var2.player;
   }

   @Override
   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   @Override
   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      if (var1 instanceof CrafterSlot && !var1.hasItem() && !this.player.isSpectator()) {
         switch(var4) {
            case PICKUP:
               if (this.menu.isSlotDisabled(var2)) {
                  this.enableSlot(var2);
               } else if (this.menu.getCarried().isEmpty()) {
                  this.disableSlot(var2);
               }
               break;
            case SWAP:
               ItemStack var5 = this.player.getInventory().getItem(var3);
               if (this.menu.isSlotDisabled(var2) && !var5.isEmpty()) {
                  this.enableSlot(var2);
               }
         }
      }

      super.slotClicked(var1, var2, var3, var4);
   }

   private void enableSlot(int var1) {
      this.updateSlotState(var1, true);
   }

   private void disableSlot(int var1) {
      this.updateSlotState(var1, false);
   }

   private void updateSlotState(int var1, boolean var2) {
      this.menu.setSlotState(var1, var2);
      super.handleSlotStateChanged(var1, this.menu.containerId, var2);
      float var3 = var2 ? 1.0F : 0.75F;
      this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, var3);
   }

   @Override
   public void renderSlot(GuiGraphics var1, Slot var2) {
      if (var2 instanceof CrafterSlot var3 && this.menu.isSlotDisabled(var2.index)) {
         this.renderDisabledSlot(var1, (CrafterSlot)var3);
         return;
      }

      super.renderSlot(var1, var2);
   }

   private void renderDisabledSlot(GuiGraphics var1, CrafterSlot var2) {
      var1.blitSprite(DISABLED_SLOT_LOCATION_SPRITE, var2.x - 1, var2.y - 1, 18, 18);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderRedstone(var1);
      this.renderTooltip(var1, var2, var3);
      if (this.hoveredSlot instanceof CrafterSlot
         && !this.menu.isSlotDisabled(this.hoveredSlot.index)
         && this.menu.getCarried().isEmpty()
         && !this.hoveredSlot.hasItem()) {
         var1.renderTooltip(this.font, DISABLED_SLOT_TOOLTIP, var2, var3);
      }
   }

   private void renderRedstone(GuiGraphics var1) {
      int var2 = this.width / 2 + 9;
      int var3 = this.height / 2 - 48;
      ResourceLocation var4;
      if (this.menu.isPowered()) {
         var4 = POWERED_REDSTONE_LOCATION_SPRITE;
      } else {
         var4 = UNPOWERED_REDSTONE_LOCATION_SPRITE;
      }

      var1.blitSprite(var4, var2, var3, 16, 16);
   }

   @Override
   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(CONTAINER_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
   }
}
