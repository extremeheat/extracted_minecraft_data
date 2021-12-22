package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerScreen<T extends ItemCombinerMenu> extends AbstractContainerScreen<T> implements ContainerListener {
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      RenderSystem.disableBlend();
      this.renderFg(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderFg(PoseStack var1, int var2, int var3, float var4) {
   }

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.menuResource);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      this.blit(var1, var5 + 59, var6 + 20, 0, this.imageHeight + (((ItemCombinerMenu)this.menu).getSlot(0).hasItem() ? 0 : 16), 110, 16);
      if ((((ItemCombinerMenu)this.menu).getSlot(0).hasItem() || ((ItemCombinerMenu)this.menu).getSlot(1).hasItem()) && !((ItemCombinerMenu)this.menu).getSlot(2).hasItem()) {
         this.blit(var1, var5 + 99, var6 + 45, this.imageWidth, 0, 28, 21);
      }

   }

   public void dataChanged(AbstractContainerMenu var1, int var2, int var3) {
   }

   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
   }
}
