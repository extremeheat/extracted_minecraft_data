package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class SpriteIconButton extends LoadingButton {
   protected static final int ICON_MARGIN = 2;
   protected final ScaledWidgetSprites sprite;
   protected DisplayState displayState;

   protected SpriteIconButton(final int x, final int y, final int width, final int height, final Component message, final ScaledWidgetSprites sprite, final DisplayState displayState, final Button.OnPress onPress, final @Nullable Tooltip tooltip, final Button.CreateNarration narration, final boolean switchToLoadingAfterPress) {
      super(x, y, width, height, message, onPress, tooltip, narration, switchToLoadingAfterPress);
      this.sprite = sprite;
      this.displayState = displayState;
   }

   public void setDisplayState(final DisplayState displayState) {
      this.displayState = displayState;
   }

   public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (!this.extractLoadingStateIfLoading(graphics)) {
         this.extractDefaultSprite(graphics);
         if (this.displayState == SpriteIconButton.DisplayState.ICON_ONLY) {
            this.sprite.extractCenteredSprite(this, graphics);
         } else if (this.displayState == SpriteIconButton.DisplayState.TEXT_ONLY) {
            this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
         } else {
            this.extractTextAndIcon(graphics, mouseX, mouseY, a);
         }

      }
   }

   protected void extractTextAndIcon(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      int left = this.getX() + 2;
      int right = this.getX() + this.getWidth() - this.sprite.width() - 4;
      int centerX = this.getX() + this.getWidth() / 2;
      ActiveTextCollector output = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);
      output.acceptScrolling(this.getMessage(), centerX, left, right, this.getY(), this.getY() + this.getHeight());
      int x = this.getX() + this.getWidth() - this.sprite.width() - 2;
      int y = this.getY() + this.getHeight() / 2 - this.sprite.height() / 2;
      if (this.displayState == SpriteIconButton.DisplayState.TEXT_AND_ICON) {
         int messageWidth = Minecraft.getInstance().font.width((FormattedText)this.getMessage());
         if (messageWidth < right - left + 2) {
            x = centerX + messageWidth / 2 + 2 + 2;
         }
      }

      this.sprite.extractSprite(this, graphics, x, y);
   }

   public static Builder<?> builder(final Component message, final Button.OnPress onPress, final DisplayState displayState) {
      return new Builder(message, onPress, displayState);
   }

   public static Builder<?> builder(final Component message, final Button.OnPress onPress, final boolean iconOnly) {
      return new Builder(message, onPress, iconOnly ? SpriteIconButton.DisplayState.ICON_ONLY : SpriteIconButton.DisplayState.TEXT_AND_OFFSET_ICON);
   }

   public static class Builder<B extends Builder<B>> extends LoadingButton.Builder<B> {
      private final DisplayState displayState;
      private @Nullable ScaledWidgetSprites sprite;

      protected Builder(final Component message, final Button.OnPress onPress, final DisplayState displayState) {
         super(message, onPress);
         this.displayState = displayState;
      }

      public B sprite(final Identifier sprite, final int spriteWidth, final int spriteHeight) {
         this.sprite = new ScaledWidgetSprites(sprite, spriteWidth, spriteHeight);
         return (B)this;
      }

      public B sprite(final WidgetSprites sprite, final int spriteWidth, final int spriteHeight) {
         this.sprite = new ScaledWidgetSprites(sprite, spriteWidth, spriteHeight);
         return (B)this;
      }

      public B withTootip() {
         this.tooltip = Tooltip.create(this.message);
         return (B)this;
      }

      public SpriteIconButton build() {
         if (this.sprite == null) {
            throw new IllegalStateException("Sprite not set");
         } else {
            return new SpriteIconButton(this.x, this.y, this.width, this.height, this.message, this.sprite, this.displayState, this.onPress, this.tooltip, this.createNarration, this.switchToLoadingAfterPress);
         }
      }
   }

   public static enum DisplayState {
      ICON_ONLY,
      TEXT_ONLY,
      TEXT_AND_OFFSET_ICON,
      TEXT_AND_ICON;

      private DisplayState() {
      }

      // $FF: synthetic method
      private static DisplayState[] $values() {
         return new DisplayState[]{ICON_ONLY, TEXT_ONLY, TEXT_AND_OFFSET_ICON, TEXT_AND_ICON};
      }
   }
}
