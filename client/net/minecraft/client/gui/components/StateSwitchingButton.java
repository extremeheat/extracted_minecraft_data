package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class StateSwitchingButton extends AbstractWidget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public StateSwitchingButton(int var1, int var2, int var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, TextComponent.EMPTY);
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

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      var5.getTextureManager().bind(this.resourceLocation);
      RenderSystem.disableDepthTest();
      int var6 = this.xTexStart;
      int var7 = this.yTexStart;
      if (this.isStateTriggered) {
         var6 += this.xDiffTex;
      }

      if (this.isHovered()) {
         var7 += this.yDiffTex;
      }

      this.blit(var1, this.x, this.y, var6, var7, this.width, this.height);
      RenderSystem.enableDepthTest();
   }
}
