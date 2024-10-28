package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class SpriteIconButton extends Button {
   protected final ResourceLocation sprite;
   protected final int spriteWidth;
   protected final int spriteHeight;

   SpriteIconButton(int var1, int var2, Component var3, int var4, int var5, ResourceLocation var6, Button.OnPress var7, @Nullable Button.CreateNarration var8) {
      super(0, 0, var1, var2, var3, var7, var8 == null ? DEFAULT_NARRATION : var8);
      this.spriteWidth = var4;
      this.spriteHeight = var5;
      this.sprite = var6;
   }

   public static Builder builder(Component var0, Button.OnPress var1, boolean var2) {
      return new Builder(var0, var1, var2);
   }

   public static class Builder {
      private final Component message;
      private final Button.OnPress onPress;
      private final boolean iconOnly;
      private int width = 150;
      private int height = 20;
      @Nullable
      private ResourceLocation sprite;
      private int spriteWidth;
      private int spriteHeight;
      @Nullable
      Button.CreateNarration narration;

      public Builder(Component var1, Button.OnPress var2, boolean var3) {
         super();
         this.message = var1;
         this.onPress = var2;
         this.iconOnly = var3;
      }

      public Builder width(int var1) {
         this.width = var1;
         return this;
      }

      public Builder size(int var1, int var2) {
         this.width = var1;
         this.height = var2;
         return this;
      }

      public Builder sprite(ResourceLocation var1, int var2, int var3) {
         this.sprite = var1;
         this.spriteWidth = var2;
         this.spriteHeight = var3;
         return this;
      }

      public Builder narration(Button.CreateNarration var1) {
         this.narration = var1;
         return this;
      }

      public SpriteIconButton build() {
         if (this.sprite == null) {
            throw new IllegalStateException("Sprite not set");
         } else {
            return (SpriteIconButton)(this.iconOnly ? new CenteredIcon(this.width, this.height, this.message, this.spriteWidth, this.spriteHeight, this.sprite, this.onPress, this.narration) : new TextAndIcon(this.width, this.height, this.message, this.spriteWidth, this.spriteHeight, this.sprite, this.onPress, this.narration));
         }
      }
   }

   public static class TextAndIcon extends SpriteIconButton {
      protected TextAndIcon(int var1, int var2, Component var3, int var4, int var5, ResourceLocation var6, Button.OnPress var7, @Nullable Button.CreateNarration var8) {
         super(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         super.renderWidget(var1, var2, var3, var4);
         int var5 = this.getX() + this.getWidth() - this.spriteWidth - 2;
         int var6 = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
         var1.blitSprite(RenderType::guiTextured, this.sprite, var5, var6, this.spriteWidth, this.spriteHeight);
      }

      public void renderString(GuiGraphics var1, Font var2, int var3) {
         int var4 = this.getX() + 2;
         int var5 = this.getX() + this.getWidth() - this.spriteWidth - 4;
         int var6 = this.getX() + this.getWidth() / 2;
         renderScrollingString(var1, var2, this.getMessage(), var6, var4, this.getY(), var5, this.getY() + this.getHeight(), var3);
      }
   }

   public static class CenteredIcon extends SpriteIconButton {
      protected CenteredIcon(int var1, int var2, Component var3, int var4, int var5, ResourceLocation var6, Button.OnPress var7, @Nullable Button.CreateNarration var8) {
         super(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         super.renderWidget(var1, var2, var3, var4);
         int var5 = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
         int var6 = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
         var1.blitSprite(RenderType::guiTextured, this.sprite, var5, var6, this.spriteWidth, this.spriteHeight);
      }

      public void renderString(GuiGraphics var1, Font var2, int var3) {
      }
   }
}
