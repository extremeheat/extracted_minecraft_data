package net.minecraft.client.gui.components;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public class SpriteIconCycleButton<Value> extends AbstractCycleButton<Value> {
   private final Function<Value, ScaledWidgetSprites> valueSprite;

   private SpriteIconCycleButton(final int x, final int y, final int width, final int height, final Component name, final int index, final Value value, final Supplier<Value> defaultValueSupplier, final AbstractCycleButton.ValueListSupplier<Value> values, final Function<Value, Component> valueStringifier, final Function<Value, ScaledWidgetSprites> valueSprite, final AbstractCycleButton.OnValueChange<Value> onValueChange, final OptionInstance.TooltipSupplier<Value> tooltipSupplier) {
      super(x, y, width, height, name, index, value, defaultValueSupplier, values, valueStringifier, onValueChange, tooltipSupplier);
      this.valueSprite = valueSprite;
      this.updateValue(value);
   }

   protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      this.extractDefaultSprite(graphics);
      ((ScaledWidgetSprites)this.valueSprite.apply(this.value)).extractCenteredSprite(this, graphics);
   }

   protected MutableComponent createNarrationMessage() {
      return wrapDefaultNarrationMessage(this.name);
   }

   public static <Value> Builder<Value> builder(final Component message, final Function<Value, Component> valueStringifier, final Supplier<Value> defaultValueSupplier) {
      return new Builder<Value>(message, valueStringifier, defaultValueSupplier);
   }

   public static <Value> Builder<Value> builder(final Component message, final Function<Value, Component> valueStringifier, final Value defaultValue) {
      return new Builder<Value>(message, valueStringifier, () -> defaultValue);
   }

   public static class Builder<Value> extends AbstractCycleButton.Builder<Value, Builder<Value>> {
      private final Component message;
      private int width = 150;
      private int height = 20;
      private @Nullable Function<Value, ScaledWidgetSprites> valueSprite;

      public Builder(final Component message, final Function<Value, Component> valueStringifier, final Supplier<Value> defaultValueSupplier) {
         super(valueStringifier, defaultValueSupplier);
         this.message = message;
      }

      public Builder<Value> width(final int width) {
         this.width = width;
         return this;
      }

      public Builder<Value> size(final int width, final int height) {
         this.width = width;
         this.height = height;
         return this;
      }

      public Builder<Value> sprite(final Function<Value, ScaledWidgetSprites> valueSprite) {
         this.valueSprite = valueSprite;
         return this;
      }

      public SpriteIconCycleButton<Value> build(final int x, final int y) {
         return this.build(x, y, (var0, var1) -> {
         });
      }

      public SpriteIconCycleButton<Value> build(final AbstractCycleButton.OnValueChange<Value> valueChangeListener) {
         return this.build(0, 0, valueChangeListener);
      }

      public SpriteIconCycleButton<Value> build(final int x, final int y, final AbstractCycleButton.OnValueChange<Value> valueChangeListener) {
         List<Value> values = this.values.getDefaultList();
         if (values.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
         } else if (this.valueSprite == null) {
            throw new IllegalStateException("Sprite not set");
         } else {
            Value initialValue = (Value)this.defaultValueSupplier.get();
            int initialIndex = values.indexOf(initialValue);
            return new SpriteIconCycleButton<Value>(x, y, this.width, this.height, this.message, initialIndex, initialValue, this.defaultValueSupplier, this.values, this.valueStringifier, this.valueSprite, valueChangeListener, this.tooltipSupplier);
         }
      }
   }
}
