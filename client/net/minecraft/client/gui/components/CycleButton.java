package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class CycleButton<T> extends AbstractButton implements ResettableOptionWidget {
   public static final BooleanSupplier DEFAULT_ALT_LIST_SELECTOR = () -> Minecraft.getInstance().hasAltDown();
   private static final List<Boolean> BOOLEAN_OPTIONS;
   private final Supplier<T> defaultValueSupplier;
   private final Component name;
   private int index;
   private T value;
   private final ValueListSupplier<T> values;
   private final Function<T, Component> valueStringifier;
   private final Function<CycleButton<T>, MutableComponent> narrationProvider;
   private final OnValueChange<T> onValueChange;
   private final DisplayState displayState;
   private final OptionInstance.TooltipSupplier<T> tooltipSupplier;
   private final SpriteSupplier<T> spriteSupplier;

   private CycleButton(final int x, final int y, final int width, final int height, final Component message, final Component name, final int index, final T value, final Supplier<T> defaultValueSupplier, final ValueListSupplier<T> values, final Function<T, Component> valueStringifier, final Function<CycleButton<T>, MutableComponent> narrationProvider, final OnValueChange<T> onValueChange, final OptionInstance.TooltipSupplier<T> tooltipSupplier, final DisplayState displayState, final SpriteSupplier<T> spriteSupplier) {
      super(x, y, width, height, message);
      this.name = name;
      this.index = index;
      this.defaultValueSupplier = defaultValueSupplier;
      this.value = value;
      this.values = values;
      this.valueStringifier = valueStringifier;
      this.narrationProvider = narrationProvider;
      this.onValueChange = onValueChange;
      this.displayState = displayState;
      this.tooltipSupplier = tooltipSupplier;
      this.spriteSupplier = spriteSupplier;
      this.updateTooltip();
   }

   protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      Identifier sprite = this.spriteSupplier.apply(this, this.getValue());
      if (sprite != null) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      } else {
         this.extractDefaultSprite(graphics);
      }

      if (this.displayState != CycleButton.DisplayState.HIDE) {
         this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
      }

   }

   private void updateTooltip() {
      this.setTooltip(this.tooltipSupplier.apply(this.value));
   }

   public void onPress(final InputWithModifiers input) {
      if (input.hasShiftDown()) {
         this.cycleValue(-1);
      } else {
         this.cycleValue(1);
      }

   }

   private void cycleValue(final int delta) {
      List<T> list = this.values.getSelectedList();
      this.index = Mth.positiveModulo(this.index + delta, list.size());
      T newValue = (T)list.get(this.index);
      this.updateValue(newValue);
      this.onValueChange.onValueChange(this, newValue);
   }

   private T getCycledValue(final int delta) {
      List<T> list = this.values.getSelectedList();
      return (T)list.get(Mth.positiveModulo(this.index + delta, list.size()));
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (scrollY > 0.0) {
         this.cycleValue(-1);
      } else if (scrollY < 0.0) {
         this.cycleValue(1);
      }

      return true;
   }

   public void setValue(final T newValue) {
      List<T> list = this.values.getSelectedList();
      int newIndex = list.indexOf(newValue);
      if (newIndex != -1) {
         this.index = newIndex;
      }

      this.updateValue(newValue);
   }

   public void resetValue() {
      this.setValue(this.defaultValueSupplier.get());
   }

   private void updateValue(final T newValue) {
      Component newMessage = this.createLabelForValue(newValue);
      this.setMessage(newMessage);
      this.value = newValue;
      this.updateTooltip();
   }

   private Component createLabelForValue(final T newValue) {
      return (Component)(this.displayState == CycleButton.DisplayState.VALUE ? (Component)this.valueStringifier.apply(newValue) : this.createFullName(newValue));
   }

   private MutableComponent createFullName(final T newValue) {
      return CommonComponents.optionNameValue(this.name, (Component)this.valueStringifier.apply(newValue));
   }

   public T getValue() {
      return this.value;
   }

   protected MutableComponent createNarrationMessage() {
      return (MutableComponent)this.narrationProvider.apply(this);
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         T nextValue = (T)this.getCycledValue(1);
         Component nextValueText = this.createLabelForValue(nextValue);
         if (this.isFocused()) {
            output.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.cycle_button.usage.focused", nextValueText));
         } else {
            output.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.cycle_button.usage.hovered", nextValueText));
         }
      }

   }

   public MutableComponent createDefaultNarrationMessage() {
      return wrapDefaultNarrationMessage((Component)(this.displayState == CycleButton.DisplayState.VALUE ? this.createFullName(this.value) : this.getMessage()));
   }

   public static <T> Builder<T> builder(final Function<T, Component> valueStringifier, final Supplier<T> defaultValueSupplier) {
      return new Builder<T>(valueStringifier, defaultValueSupplier);
   }

   public static <T> Builder<T> builder(final Function<T, Component> valueStringifier, final T defaultValue) {
      return new Builder<T>(valueStringifier, () -> defaultValue);
   }

   public static Builder<Boolean> booleanBuilder(final Component trueText, final Component falseText, final boolean defaultValue) {
      return (new Builder((b) -> b == Boolean.TRUE ? trueText : falseText, () -> defaultValue)).withValues(BOOLEAN_OPTIONS);
   }

   public static Builder<Boolean> onOffBuilder(final boolean initialValue) {
      return (new Builder((b) -> b == Boolean.TRUE ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF, () -> initialValue)).withValues(BOOLEAN_OPTIONS);
   }

   static {
      BOOLEAN_OPTIONS = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
   }

   public static class Builder<T> {
      private final Supplier<T> defaultValueSupplier;
      private final Function<T, Component> valueStringifier;
      private OptionInstance.TooltipSupplier<T> tooltipSupplier = (value) -> null;
      private SpriteSupplier<T> spriteSupplier = (button, value) -> null;
      private Function<CycleButton<T>, MutableComponent> narrationProvider = CycleButton::createDefaultNarrationMessage;
      private ValueListSupplier<T> values = CycleButton.ValueListSupplier.<T>create(ImmutableList.of());
      private DisplayState displayState;

      public Builder(final Function<T, Component> valueStringifier, final Supplier<T> defaultValueSupplier) {
         super();
         this.displayState = CycleButton.DisplayState.NAME_AND_VALUE;
         this.valueStringifier = valueStringifier;
         this.defaultValueSupplier = defaultValueSupplier;
      }

      public Builder<T> withValues(final Collection<T> values) {
         return this.withValues(CycleButton.ValueListSupplier.create(values));
      }

      @SafeVarargs
      public final Builder<T> withValues(final T... values) {
         return this.withValues(ImmutableList.copyOf(values));
      }

      public Builder<T> withValues(final List<T> values, final List<T> altValues) {
         return this.withValues(CycleButton.ValueListSupplier.create(CycleButton.DEFAULT_ALT_LIST_SELECTOR, values, altValues));
      }

      public Builder<T> withValues(final BooleanSupplier altCondition, final List<T> values, final List<T> altValues) {
         return this.withValues(CycleButton.ValueListSupplier.create(altCondition, values, altValues));
      }

      public Builder<T> withValues(final ValueListSupplier<T> valueListSupplier) {
         this.values = valueListSupplier;
         return this;
      }

      public Builder<T> withTooltip(final OptionInstance.TooltipSupplier<T> tooltipSupplier) {
         this.tooltipSupplier = tooltipSupplier;
         return this;
      }

      public Builder<T> withCustomNarration(final Function<CycleButton<T>, MutableComponent> narrationProvider) {
         this.narrationProvider = narrationProvider;
         return this;
      }

      public Builder<T> withSprite(final SpriteSupplier<T> spriteSupplier) {
         this.spriteSupplier = spriteSupplier;
         return this;
      }

      public Builder<T> displayState(final DisplayState state) {
         this.displayState = state;
         return this;
      }

      public Builder<T> displayOnlyValue() {
         return this.displayState(CycleButton.DisplayState.VALUE);
      }

      public CycleButton<T> create(final Component name, final OnValueChange<T> valueChangeListener) {
         return this.create(0, 0, 150, 20, name, valueChangeListener);
      }

      public CycleButton<T> create(final int x, final int y, final int width, final int height, final Component name) {
         return this.create(x, y, width, height, name, (button, value) -> {
         });
      }

      public CycleButton<T> create(final int x, final int y, final int width, final int height, final Component name, final OnValueChange<T> valueChangeListener) {
         List<T> values = this.values.getDefaultList();
         if (values.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
         } else {
            T initialValue = (T)this.defaultValueSupplier.get();
            int initialIndex = values.indexOf(initialValue);
            Component valueText = (Component)this.valueStringifier.apply(initialValue);
            Component initialTitle = (Component)(this.displayState == CycleButton.DisplayState.VALUE ? valueText : CommonComponents.optionNameValue(name, valueText));
            return new CycleButton<T>(x, y, width, height, initialTitle, name, initialIndex, initialValue, this.defaultValueSupplier, this.values, this.valueStringifier, this.narrationProvider, valueChangeListener, this.tooltipSupplier, this.displayState, this.spriteSupplier);
         }
      }
   }

   public interface ValueListSupplier<T> {
      List<T> getSelectedList();

      List<T> getDefaultList();

      static <T> ValueListSupplier<T> create(final Collection<T> values) {
         final List<T> copy = ImmutableList.copyOf(values);
         return new ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return copy;
            }

            public List<T> getDefaultList() {
               return copy;
            }
         };
      }

      static <T> ValueListSupplier<T> create(final BooleanSupplier altSelector, final List<T> defaultList, final List<T> altList) {
         final List<T> defaultCopy = ImmutableList.copyOf(defaultList);
         final List<T> altCopy = ImmutableList.copyOf(altList);
         return new ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return altSelector.getAsBoolean() ? altCopy : defaultCopy;
            }

            public List<T> getDefaultList() {
               return defaultCopy;
            }
         };
      }
   }

   public static enum DisplayState {
      NAME_AND_VALUE,
      VALUE,
      HIDE;

      private DisplayState() {
      }

      // $FF: synthetic method
      private static DisplayState[] $values() {
         return new DisplayState[]{NAME_AND_VALUE, VALUE, HIDE};
      }
   }

   @FunctionalInterface
   public interface OnValueChange<T> {
      void onValueChange(CycleButton<T> button, T value);
   }

   @FunctionalInterface
   public interface SpriteSupplier<T> {
      @Nullable Identifier apply(CycleButton<T> button, T value);
   }
}
