package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class StateSwitchingButton extends AbstractWidget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public StateSwitchingButton(int var1, int var2, int var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, CommonComponents.EMPTY);
      this.isStateTriggered = var5;
   }

   public void initTextureValues(int var1, int var2, int var3, int var4, ResourceLocation var5) {
      this.xTexStart = var1;
      this.yTexStart = var2;
      this.xDiffTex = var3;
      this.yDiffTex = var4;
      this.resourceLocation = var5;
   }

   public void setStateTriggered(boolean var1) {
      this.isStateTriggered = var1;
   }

   public boolean isStateTriggered() {
      return this.isStateTriggered;
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
      this.defaultButtonNarrationText(var1);
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      RenderSystem.disableDepthTest();
      int var5 = this.xTexStart;
      int var6 = this.yTexStart;
      if (this.isStateTriggered) {
         var5 += this.xDiffTex;
      }

      if (this.isHoveredOrFocused()) {
         var6 += this.yDiffTex;
      }

      var1.blit(this.resourceLocation, this.getX(), this.getY(), var5, var6, this.width, this.height);
      RenderSystem.enableDepthTest();
   }
}
