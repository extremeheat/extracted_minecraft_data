package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;

public abstract class ItemCombinerScreen<T extends ItemCombinerMenu> extends AbstractContainerScreen<T> implements ContainerListener {
   private final Identifier menuResource;

   public ItemCombinerScreen(final T menu, final Inventory inventory, final Component title, final Identifier menuResource) {
      super(menu, inventory, title);
      this.menuResource = menuResource;
   }

   protected void subInit() {
   }

   protected void init() {
      super.init();
      this.subInit();
      ((ItemCombinerMenu)this.menu).addSlotListener(this);
   }

   public void removed() {
      super.removed();
      ((ItemCombinerMenu)this.menu).removeSlotListener(this);
   }

   protected void renderBg(final GuiGraphics graphics, final float a, final int xm, final int ym) {
      graphics.blit(RenderPipelines.GUI_TEXTURED, this.menuResource, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      this.renderErrorIcon(graphics, this.leftPos, this.topPos);
   }

   protected abstract void renderErrorIcon(final GuiGraphics graphics, final int xo, final int yo);

   public void dataChanged(final AbstractContainerMenu container, final int id, final int value) {
   }

   public void slotChanged(final AbstractContainerMenu container, final int slotIndex, final ItemStack itemStack) {
   }
}
