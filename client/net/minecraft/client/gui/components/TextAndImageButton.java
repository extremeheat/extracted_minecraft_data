package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextAndImageButton extends Button {
   protected final ResourceLocation resourceLocation;
   protected final int xTexStart;
   protected final int yTexStart;
   protected final int yDiffTex;
   protected final int textureWidth;
   protected final int textureHeight;
   private final int xOffset;
   private final int yOffset;
   private final int usedTextureWidth;
   private final int usedTextureHeight;

   TextAndImageButton(
      Component var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, ResourceLocation var11, Button.OnPress var12
   ) {
      super(0, 0, 150, 20, var1, var12, DEFAULT_NARRATION);
      this.textureWidth = var9;
      this.textureHeight = var10;
      this.xTexStart = var2;
      this.yTexStart = var3;
      this.yDiffTex = var6;
      this.resourceLocation = var11;
      this.xOffset = var4;
      this.yOffset = var5;
      this.usedTextureWidth = var7;
      this.usedTextureHeight = var8;
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderWidget(var1, var2, var3, var4);
      this.renderTexture(
         var1,
         this.resourceLocation,
         this.getXOffset(),
         this.getYOffset(),
         this.xTexStart,
         this.yTexStart,
         this.yDiffTex,
         this.usedTextureWidth,
         this.usedTextureHeight,
         this.textureWidth,
         this.textureHeight
      );
   }

   @Override
   public void renderString(GuiGraphics var1, Font var2, int var3) {
      int var4 = this.getX() + 2;
      int var5 = this.getX() + this.getWidth() - this.usedTextureWidth - 6;
      renderScrollingString(var1, var2, this.getMessage(), var4, this.getY(), var5, this.getY() + this.getHeight(), var3);
   }

   private int getXOffset() {
      return this.getX() + (this.width / 2 - this.usedTextureWidth / 2) + this.xOffset;
   }

   private int getYOffset() {
      return this.getY() + this.yOffset;
   }

   public static TextAndImageButton.Builder builder(Component var0, ResourceLocation var1, Button.OnPress var2) {
      return new TextAndImageButton.Builder(var0, var1, var2);
   }

   public static class Builder {
      private final Component message;
      private final ResourceLocation resourceLocation;
      private final Button.OnPress onPress;
      private int xTexStart;
      private int yTexStart;
      private int yDiffTex;
      private int usedTextureWidth;
      private int usedTextureHeight;
      private int textureWidth;
      private int textureHeight;
      private int xOffset;
      private int yOffset;

      public Builder(Component var1, ResourceLocation var2, Button.OnPress var3) {
         super();
         this.message = var1;
         this.resourceLocation = var2;
         this.onPress = var3;
      }

      public TextAndImageButton.Builder texStart(int var1, int var2) {
         this.xTexStart = var1;
         this.yTexStart = var2;
         return this;
      }

      public TextAndImageButton.Builder offset(int var1, int var2) {
         this.xOffset = var1;
         this.yOffset = var2;
         return this;
      }

      public TextAndImageButton.Builder yDiffTex(int var1) {
         this.yDiffTex = var1;
         return this;
      }

      public TextAndImageButton.Builder usedTextureSize(int var1, int var2) {
         this.usedTextureWidth = var1;
         this.usedTextureHeight = var2;
         return this;
      }

      public TextAndImageButton.Builder textureSize(int var1, int var2) {
         this.textureWidth = var1;
         this.textureHeight = var2;
         return this;
      }

      public TextAndImageButton build() {
         return new TextAndImageButton(
            this.message,
            this.xTexStart,
            this.yTexStart,
            this.xOffset,
            this.yOffset,
            this.yDiffTex,
            this.usedTextureWidth,
            this.usedTextureHeight,
            this.textureWidth,
            this.textureHeight,
            this.resourceLocation,
            this.onPress
         );
      }
   }
}
