package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;

public abstract class ItemCombinerScreen<T extends ItemCombinerMenu> extends AbstractContainerScreen<T> implements ContainerListener {
   private final ResourceLocation menuResource;

   public ItemCombinerScreen(T var1, Inventory var2, Component var3, ResourceLocation var4) {
      super(var1, var2, var3);
      this.menuResource = var4;
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

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderFg(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderFg(GuiGraphics var1, int var2, int var3, float var4) {
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      var1.blit(this.menuResource, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.renderErrorIcon(var1, this.leftPos, this.topPos);
   }

   protected abstract void renderErrorIcon(GuiGraphics var1, int var2, int var3);

   public void dataChanged(AbstractContainerMenu var1, int var2, int var3) {
   }

   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
   }
}
