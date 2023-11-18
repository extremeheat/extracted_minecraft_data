package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageWidget extends AbstractWidget {
   private final ResourceLocation imageLocation;

   public ImageWidget(int var1, int var2, ResourceLocation var3) {
      this(0, 0, var1, var2, var3);
   }

   public ImageWidget(int var1, int var2, int var3, int var4, ResourceLocation var5) {
      super(var1, var2, var3, var4, Component.empty());
      this.imageLocation = var5;
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.getWidth();
      int var6 = this.getHeight();
      var1.blit(this.imageLocation, this.getX(), this.getY(), 0.0F, 0.0F, var5, var6, var5, var6);
   }
}
