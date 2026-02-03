package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public abstract class Button extends AbstractButton {
   public static final int SMALL_WIDTH = 120;
   public static final int DEFAULT_WIDTH = 150;
   public static final int BIG_WIDTH = 200;
   public static final int DEFAULT_HEIGHT = 20;
   public static final int DEFAULT_SPACING = 8;
   protected static final CreateNarration DEFAULT_NARRATION = (defaultNarrationSupplier) -> (MutableComponent)defaultNarrationSupplier.get();
   protected final OnPress onPress;
   protected final CreateNarration createNarration;

   public static Builder builder(final Component message, final OnPress onPress) {
      return new Builder(message, onPress);
   }

   protected Button(final int x, final int y, final int width, final int height, final Component message, final OnPress onPress, final CreateNarration createNarration) {
      super(x, y, width, height, message);
      this.onPress = onPress;
      this.createNarration = createNarration;
   }

   public void onPress(final InputWithModifiers input) {
      this.onPress.onPress(this);
   }

   protected MutableComponent createNarrationMessage() {
      return this.createNarration.createNarrationMessage(() -> super.createNarrationMessage());
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      this.defaultButtonNarrationText(output);
   }

   public static class Plain extends Button {
      protected Plain(final int x, final int y, final int width, final int height, final Component message, final OnPress onPress, final CreateNarration createNarration) {
         super(x, y, width, height, message, onPress, createNarration);
      }

      protected void renderContents(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
         this.renderDefaultSprite(graphics);
         this.renderDefaultLabel(graphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
      }
   }

   public static class Builder {
      private final Component message;
      private final OnPress onPress;
      private @Nullable Tooltip tooltip;
      private int x;
      private int y;
      private int width = 150;
      private int height = 20;
      private CreateNarration createNarration;

      public Builder(final Component message, final OnPress onPress) {
         super();
         this.createNarration = Button.DEFAULT_NARRATION;
         this.message = message;
         this.onPress = onPress;
      }

      public Builder pos(final int x, final int y) {
         this.x = x;
         this.y = y;
         return this;
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

      public Builder bounds(final int x, final int y, final int width, final int height) {
         return this.pos(x, y).size(width, height);
      }

      public Builder tooltip(final @Nullable Tooltip tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      public Builder createNarration(final CreateNarration createNarration) {
         this.createNarration = createNarration;
         return this;
      }

      public Button build() {
         Button button = new Plain(this.x, this.y, this.width, this.height, this.message, this.onPress, this.createNarration);
         button.setTooltip(this.tooltip);
         return button;
      }
   }

   public interface CreateNarration {
      MutableComponent createNarrationMessage(Supplier<MutableComponent> defaultNarrationSupplier);
   }

   public interface OnPress {
      void onPress(final Button button);
   }
}
