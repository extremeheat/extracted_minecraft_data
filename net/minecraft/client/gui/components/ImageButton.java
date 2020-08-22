package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ImageButton extends Button {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffTex;
   private final int textureWidth;
   private final int textureHeight;

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation var8, Button.OnPress var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, 256, 256, var9);
   }

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation var8, int var9, int var10, Button.OnPress var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, "");
   }

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation var8, int var9, int var10, Button.OnPress var11, String var12) {
      super(var1, var2, var3, var4, var12, var11);
      this.textureWidth = var9;
      this.textureHeight = var10;
      this.xTexStart = var5;
      this.yTexStart = var6;
      this.yDiffTex = var7;
      this.resourceLocation = var8;
   }

   public void setPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft var4 = Minecraft.getInstance();
      var4.getTextureManager().bind(this.resourceLocation);
      RenderSystem.disableDepthTest();
      int var5 = this.yTexStart;
      if (this.isHovered()) {
         var5 += this.yDiffTex;
      }

      blit(this.x, this.y, (float)this.xTexStart, (float)var5, this.width, this.height, this.textureWidth, this.textureHeight);
      RenderSystem.enableDepthTest();
   }
}
