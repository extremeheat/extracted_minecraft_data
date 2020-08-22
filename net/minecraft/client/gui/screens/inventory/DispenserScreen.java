package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DispenserMenu;

public class DispenserScreen extends AbstractContainerScreen {
   private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation("textures/gui/container/dispenser.png");

   public DispenserScreen(DispenserMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   protected void renderLabels(int var1, int var2) {
      String var3 = this.title.getColoredString();
      this.font.draw(var3, (float)(this.imageWidth / 2 - this.font.width(var3) / 2), 6.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(CONTAINER_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
   }
}
