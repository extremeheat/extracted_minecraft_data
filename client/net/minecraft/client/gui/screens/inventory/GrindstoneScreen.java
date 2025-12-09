package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.GrindstoneMenu;

public class GrindstoneScreen extends AbstractContainerScreen<GrindstoneMenu> {
   private static final Identifier ERROR_SPRITE = Identifier.withDefaultNamespace("container/grindstone/error");
   private static final Identifier GRINDSTONE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/grindstone.png");

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
      var1.blit(RenderPipelines.GUI_TEXTURED, GRINDSTONE_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      if ((((GrindstoneMenu)this.menu).getSlot(0).hasItem() || ((GrindstoneMenu)this.menu).getSlot(1).hasItem()) && !((GrindstoneMenu)this.menu).getSlot(2).hasItem()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)ERROR_SPRITE, var5 + 92, var6 + 31, 28, 21);
      }

   }
}
