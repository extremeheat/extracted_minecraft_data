package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class StateSwitchingButton extends AbstractWidget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public StateSwitchingButton(int var1, int var2, int var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, "");
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

   public void setPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft var4 = Minecraft.getInstance();
      var4.getTextureManager().bind(this.resourceLocation);
      GlStateManager.disableDepthTest();
      int var5 = this.xTexStart;
      int var6 = this.yTexStart;
      if (this.isStateTriggered) {
         var5 += this.xDiffTex;
      }

      if (this.isHovered()) {
         var6 += this.yDiffTex;
      }

      this.blit(this.x, this.y, var5, var6, this.width, this.height);
      GlStateManager.enableDepthTest();
   }
}
