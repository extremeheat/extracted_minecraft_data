package net.minecraft.client.gui.components;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public abstract class SpriteIconButton extends Button {
   protected final WidgetSprites sprite;
   protected final int spriteWidth;
   protected final int spriteHeight;

   private SpriteIconButton(final int width, final int height, final Component message, final int spriteWidth, final int spriteHeight, final WidgetSprites sprite, final Button.OnPress onPress, final @Nullable Component tooltip, final Button.@Nullable CreateNarration narration) {
      super(0, 0, width, height, message, onPress, narration == null ? DEFAULT_NARRATION : narration);
      if (tooltip != null) {
         this.setTooltip(Tooltip.create(tooltip));
      }

      this.spriteWidth = spriteWidth;
      this.spriteHeight = spriteHeight;
      this.sprite = sprite;
   }

   protected void extractSprite(final GuiGraphicsExtractor graphics, final int x, final int y) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite.get(this.isActive(), this.isHoveredOrFocused()), x, y, this.spriteWidth, this.spriteHeight, this.alpha);
   }

   public static Builder builder(final Component message, final Button.OnPress onPress, final boolean iconOnly) {
      return new Builder(message, onPress, iconOnly);
   }

   public static class CenteredIcon extends SpriteIconButton {
      protected CenteredIcon(final int width, final int height, final Component message, final int spriteWidth, final int spriteHeight, final WidgetSprites sprite, final Button.OnPress onPress, final @Nullable Component tooltip, final Button.@Nullable CreateNarration narration) {
         super(width, height, message, spriteWidth, spriteHeight, sprite, onPress, tooltip, narration);
      }

      public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         this.extractDefaultSprite(graphics);
         int x = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
         int y = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
         this.extractSprite(graphics, x, y);
      }
   }

   public static class TextAndIcon extends SpriteIconButton {
      protected TextAndIcon(final int width, final int height, final Component message, final int spriteWidth, final int spriteHeight, final WidgetSprites sprite, final Button.OnPress onPress, final @Nullable Component tooltip, final Button.@Nullable CreateNarration narration) {
         super(width, height, message, spriteWidth, spriteHeight, sprite, onPress, tooltip, narration);
      }

      public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         this.extractDefaultSprite(graphics);
         int left = this.getX() + 2;
         int right = this.getX() + this.getWidth() - this.spriteWidth - 4;
         int centerX = this.getX() + this.getWidth() / 2;
         ActiveTextCollector output = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);
         output.acceptScrolling(this.getMessage(), centerX, left, right, this.getY(), this.getY() + this.getHeight());
         int x = this.getX() + this.getWidth() - this.spriteWidth - 2;
         int y = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
         this.extractSprite(graphics, x, y);
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

      public Builder(final Component message, final Button.OnPress onPress, final boolean iconOnly) {
         super();
         this.message = message;
         this.onPress = onPress;
         this.iconOnly = iconOnly;
      }

      public Builder width(final int width) {
         this.width = width;
         return this;
      }

      public Builder size(final int width, final int height) {
         this.width = width;
         this.height = height;
         return this;
      }

      public Builder sprite(final Identifier sprite, final int spriteWidth, final int spriteHeight) {
         this.sprite = new WidgetSprites(sprite);
         this.spriteWidth = spriteWidth;
         this.spriteHeight = spriteHeight;
         return this;
      }

      public Builder sprite(final WidgetSprites sprite, final int spriteWidth, final int spriteHeight) {
         this.sprite = sprite;
         this.spriteWidth = spriteWidth;
         this.spriteHeight = spriteHeight;
         return this;
      }

      public Builder withTootip() {
         this.tooltip = this.message;
         return this;
      }

      public Builder narration(final Button.CreateNarration narration) {
         this.narration = narration;
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
