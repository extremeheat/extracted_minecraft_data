package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public class LoadingButton extends Button {
   private static final Identifier BUTTON_DISABLED_SPRITE = Identifier.withDefaultNamespace("widget/button_disabled");
   private static final Identifier LOADING_SPRITE = Identifier.withDefaultNamespace("widget/loading");
   private static final Tooltip LOADING_TOOLTIP = Tooltip.create(Component.translatable("gui.loading"));
   private static final int LOADING_SPRITE_W = 5;
   private static final int LOADING_SPRITE_H = 2;
   private final boolean switchToLoadingAfterPress;
   private @Nullable Tooltip defaultTooltip;
   private boolean loading;

   public LoadingButton(final int x, final int y, final int width, final int height, final Component message, final Button.OnPress onPress, final @Nullable Tooltip tooltip, final Button.CreateNarration narration, final boolean switchToLoadingAfterPress) {
      super(x, y, width, height, message, onPress, narration);
      this.setTooltip(tooltip);
      this.switchToLoadingAfterPress = switchToLoadingAfterPress;
   }

   public void setLoading(final boolean loading) {
      this.setLoading(loading, LOADING_TOOLTIP);
   }

   public void setLoading(final boolean loading, final Tooltip loadingTooltip) {
      if (this.loading != loading) {
         this.loading = loading;
         super.setTooltip(loading ? loadingTooltip : this.defaultTooltip);
      }

   }

   public void setTooltip(final @Nullable Tooltip tooltip) {
      this.defaultTooltip = tooltip;
      if (!this.loading) {
         super.setTooltip(tooltip);
      }

   }

   public boolean isActive() {
      return super.isActive() && !this.loading;
   }

   public void onPress(final InputWithModifiers input) {
      if (this.switchToLoadingAfterPress) {
         this.setLoading(true);
      }

      super.onPress(input);
   }

   protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (!this.extractLoadingStateIfLoading(graphics)) {
         this.extractDefaultSprite(graphics);
         this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
      }
   }

   protected boolean extractLoadingStateIfLoading(final GuiGraphicsExtractor graphics) {
      if (!this.loading) {
         return false;
      } else {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_DISABLED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)LOADING_SPRITE, this.getX() + (this.getWidth() - 5) / 2, this.getY() + (this.getHeight() - 2) / 2, 5, 2, ARGB.white(this.alpha));
         return true;
      }
   }

   public static Builder<?> builder(final Component message, final Button.OnPress onPress) {
      return new Builder(message, onPress);
   }

   public static class Builder<B extends Builder<B>> extends Button.Builder<B> {
      protected boolean switchToLoadingAfterPress;

      protected Builder(final Component message, final Button.OnPress onPress) {
         super(message, onPress);
      }

      public B switchToLoadingAfterPress() {
         this.switchToLoadingAfterPress = true;
         return (B)this;
      }

      public LoadingButton build() {
         return new LoadingButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.tooltip, this.createNarration, this.switchToLoadingAfterPress);
      }
   }
}
