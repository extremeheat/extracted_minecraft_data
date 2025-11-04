package net.minecraft.client.gui.components;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public abstract class SpriteIconButton extends Button {
   protected final WidgetSprites sprite;
   protected final int spriteWidth;
   protected final int spriteHeight;

   SpriteIconButton(int var1, int var2, Component var3, int var4, int var5, WidgetSprites var6, Button.OnPress var7, @Nullable Component var8, Button.@Nullable CreateNarration var9) {
      super(0, 0, var1, var2, var3, var7, var9 == null ? DEFAULT_NARRATION : var9);
      if (var8 != null) {
         this.setTooltip(Tooltip.create(var8));
      }

      this.spriteWidth = var4;
      this.spriteHeight = var5;
      this.sprite = var6;
   }

   protected void renderSprite(GuiGraphics var1, int var2, int var3) {
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite.get(this.isActive(), this.isHoveredOrFocused()), var2, var3, this.spriteWidth, this.spriteHeight, this.alpha);
   }

   public static Builder builder(Component var0, Button.OnPress var1, boolean var2) {
      return new Builder(var0, var1, var2);
   }

   public static class CenteredIcon extends SpriteIconButton {
      protected CenteredIcon(int var1, int var2, Component var3, int var4, int var5, WidgetSprites var6, Button.OnPress var7, @Nullable Component var8, Button.@Nullable CreateNarration var9) {
         super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

      public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
         this.renderDefaultSprite(var1);
         int var5 = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
         int var6 = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
         this.renderSprite(var1, var5, var6);
      }
   }

   public static class TextAndIcon extends SpriteIconButton {
      protected TextAndIcon(int var1, int var2, Component var3, int var4, int var5, WidgetSprites var6, Button.OnPress var7, @Nullable Component var8, Button.@Nullable CreateNarration var9) {
         super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

      public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
         this.renderDefaultSprite(var1);
         int var5 = this.getX() + 2;
         int var6 = this.getX() + this.getWidth() - this.spriteWidth - 4;
         int var7 = this.getX() + this.getWidth() / 2;
         ActiveTextCollector var8 = var1.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE);
         var8.acceptScrolling(this.getMessage(), var7, var5, var6, this.getY(), this.getY() + this.getHeight());
         int var9 = this.getX() + this.getWidth() - this.spriteWidth - 2;
         int var10 = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
         this.renderSprite(var1, var9, var10);
      }
   }

   public static class Builder {
      private final Component message;
      private final Button.OnPress onPress;
      private final boolean iconOnly;
      private int width = 150;
      private int height = 20;
      private @Nullable WidgetSprites sprite;
      private int spriteWidth;
      private int spriteHeight;
      private @Nullable Component tooltip;
      private Button.@Nullable CreateNarration narration;

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

      public Builder sprite(Identifier var1, int var2, int var3) {
         this.sprite = new WidgetSprites(var1);
         this.spriteWidth = var2;
         this.spriteHeight = var3;
         return this;
      }

      public Builder sprite(WidgetSprites var1, int var2, int var3) {
         this.sprite = var1;
         this.spriteWidth = var2;
         this.spriteHeight = var3;
         return this;
      }

      public Builder withTootip() {
         this.tooltip = this.message;
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
            return (SpriteIconButton)(this.iconOnly ? new CenteredIcon(this.width, this.height, this.message, this.spriteWidth, this.spriteHeight, this.sprite, this.onPress, this.tooltip, this.narration) : new TextAndIcon(this.width, this.height, this.message, this.spriteWidth, this.spriteHeight, this.sprite, this.onPress, this.tooltip, this.narration));
         }
      }
   }
}
