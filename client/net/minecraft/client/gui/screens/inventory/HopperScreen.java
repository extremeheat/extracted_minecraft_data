package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HopperMenu;

public class HopperScreen extends AbstractContainerScreen<HopperMenu> {
   private static final ResourceLocation HOPPER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/hopper.png");

   public HopperScreen(HopperMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageHeight = 133;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(HOPPER_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
   }
}
