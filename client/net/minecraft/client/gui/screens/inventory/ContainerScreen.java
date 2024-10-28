package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class ContainerScreen extends AbstractContainerScreen<ChestMenu> implements MenuAccess<ChestMenu> {
   private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
   private final int containerRows;

   public ContainerScreen(ChestMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      boolean var4 = true;
      boolean var5 = true;
      this.containerRows = var1.getRowCount();
      this.imageHeight = 114 + this.containerRows * 18;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(CONTAINER_BACKGROUND, var5, var6, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
      var1.blit(CONTAINER_BACKGROUND, var5, var6 + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
   }
}
