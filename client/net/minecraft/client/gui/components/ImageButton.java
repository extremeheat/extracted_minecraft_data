package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageButton extends Button {
   protected final ResourceLocation resourceLocation;
   protected final int xTexStart;
   protected final int yTexStart;
   protected final int yDiffTex;
   protected final int textureWidth;
   protected final int textureHeight;

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, ResourceLocation var7, Button.OnPress var8) {
      this(var1, var2, var3, var4, var5, var6, var4, var7, 256, 256, var8);
   }

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation var8, Button.OnPress var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, 256, 256, var9);
   }

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation var8, int var9, int var10, Button.OnPress var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, CommonComponents.EMPTY);
   }

   public ImageButton(
      int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation var8, int var9, int var10, Button.OnPress var11, Component var12
   ) {
      super(var1, var2, var3, var4, var12, var11, DEFAULT_NARRATION);
      this.textureWidth = var9;
      this.textureHeight = var10;
      this.xTexStart = var5;
      this.yTexStart = var6;
      this.yDiffTex = var7;
      this.resourceLocation = var8;
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTexture(
         var1,
         this.resourceLocation,
         this.getX(),
         this.getY(),
         this.xTexStart,
         this.yTexStart,
         this.yDiffTex,
         this.width,
         this.height,
         this.textureWidth,
         this.textureHeight
      );
   }
}
