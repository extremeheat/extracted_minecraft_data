package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.GrindstoneMenu;

public class GrindstoneScreen extends AbstractContainerScreen<GrindstoneMenu> {
   private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/grindstone/error");
   private static final ResourceLocation GRINDSTONE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/grindstone.png");

   public GrindstoneScreen(GrindstoneMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(GRINDSTONE_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      if ((((GrindstoneMenu)this.menu).getSlot(0).hasItem() || ((GrindstoneMenu)this.menu).getSlot(1).hasItem()) && !((GrindstoneMenu)this.menu).getSlot(2).hasItem()) {
         var1.blitSprite(ERROR_SPRITE, var5 + 92, var6 + 31, 28, 21);
      }

   }
}
