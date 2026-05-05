package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ResettableOptionWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class OptionInstance<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Enum<Boolean> BOOLEAN_VALUES;
   public static final CaptionBasedToString<Boolean> BOOLEAN_TO_STRING;
   public static final ValueUpdateListener<Object> NO_ACTION;
   private final TooltipSupplier<T> tooltip;
   private final Function<T, Component> toString;
   private final ValueSet<T> values;
   private final Codec<T> codec;
   private final T initialValue;
   private final ValueUpdateListener<? super T> onValueUpdate;
   private final Component caption;
   private T value;

   public static OptionInstance<Boolean> createBoolean(final String captionId, final boolean initialValue, final ValueUpdateListener<? super Boolean> onValueUpdate) {
      return createBoolean(captionId, noTooltip(), initialValue, onValueUpdate);
   }

   public static OptionInstance<Boolean> createBoolean(final String captionId, final boolean initialValue) {
      return createBoolean(captionId, noTooltip(), initialValue, NO_ACTION);
   }

   public static OptionInstance<Boolean> createBoolean(final String captionId, final TooltipSupplier<Boolean> tooltip, final boolean initialValue) {
      return createBoolean(captionId, tooltip, initialValue, NO_ACTION);
   }

   public static OptionInstance<Boolean> createBoolean(final String captionId, final TooltipSupplier<Boolean> tooltip, final boolean initialValue, final ValueUpdateListener<? super Boolean> onValueUpdate) {
      return createBoolean(captionId, tooltip, BOOLEAN_TO_STRING, initialValue, onValueUpdate);
   }

   public static OptionInstance<Boolean> createBoolean(final String captionId, final TooltipSupplier<Boolean> tooltip, final CaptionBasedToString<Boolean> toString, final boolean initialValue, final ValueUpdateListener<? super Boolean> onValueUpdate) {
      return new OptionInstance<Boolean>(captionId, tooltip, toString, BOOLEAN_VALUES, initialValue, onValueUpdate);
   }

   public OptionInstance(final String captionId, final TooltipSupplier<T> tooltip, final CaptionBasedToString<T> toString, final ValueSet<T> values, final T initialValue, final ValueUpdateListener<? super T> onValueUpdate) {
      this(captionId, tooltip, toString, values, values.codec(), initialValue, onValueUpdate);
   }

   public OptionInstance(final String captionId, final TooltipSupplier<T> tooltip, final CaptionBasedToString<T> toString, final ValueSet<T> values, final Codec<T> codec, final T initialValue, final ValueUpdateListener<? super T> onValueUpdate) {
      super();
      this.caption = Component.translatable(captionId);
      this.tooltip = tooltip;
      this.toString = (value) -> toString.toString(this.caption, value);
      this.values = values;
      this.codec = codec;
      this.initialValue = initialValue;
      this.onValueUpdate = onValueUpdate;
      this.value = this.initialValue;
   }

   public static <T> TooltipSupplier<T> noTooltip() {
      return (var0) -> null;
   }

   public static <T> TooltipSupplier<T> cachedConstantTooltip(final Component tooltipComponent) {
      return (var1) -> Tooltip.create(tooltipComponent);
   }

   public AbstractWidget createButton(final Options options) {
      return this.createButton(options, 0, 0, 150);
   }

   public AbstractWidget createButton(final Options options, final int x, final int y, final int width) {
      return this.createButton(options, x, y, width, NO_ACTION);
   }

   public AbstractWidget createButton(final Options options, final int x, final int y, final int width, final ValueUpdateListener<? super T> onValueChanged) {
      return (AbstractWidget)this.values.createButton(this.tooltip, options, x, y, width, onValueChanged).apply(this);
   }

   public T get() {
      return this.value;
   }

   public Codec<T> codec() {
      return this.codec;
   }

   public String toString() {
      return this.caption.getString();
   }

   public void set(final T value) {
      T newValue = (T)this.values.validateValue(value).orElseGet(() -> {
         LOGGER.error("Illegal option value {} for {}", value, this.caption.getString());
         return this.initialValue;
      });
      if (!Minecraft.getInstance().isRunning()) {
         this.value = newValue;
      } else {
         if (!Objects.equals(this.value, newValue)) {
            this.value = newValue;
            this.onValueUpdate.valueChanged(newValue);
         }

      }
   }

   public ValueSet<T> values() {
      return this.values;
   }

   static {
      BOOLEAN_VALUES = new Enum<Boolean>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
      BOOLEAN_TO_STRING = (var0, b) -> b ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
      NO_ACTION = (var0) -> {
      };
   }

   public interface SliderableValueSet<T> extends ValueSet<T> {
      double toSliderValue(final T value);

      default Optional<T> next(final T current) {
         return Optional.empty();
      }

      default Optional<T> previous(final T current) {
         return Optional.empty();
      }

      T fromSliderValue(final double slider);

      default boolean applyValueImmediately() {
         return true;
      }

      default Function<OptionInstance<T>, AbstractWidget> createButton(final TooltipSupplier<T> tooltip, final Options options, final int x, final int y, final int width, final ValueUpdateListener<? super T> onValueChanged) {
         return (instance) -> new OptionInstanceSliderButton(options, x, y, width, 20, instance, this, tooltip, onValueChanged, this.applyValueImmediately());
      }
   }

   public interface CycleableValueSet<T> extends ValueSet<T> {
      CycleButton.ValueListSupplier<T> valueListSupplier();

      default ValueSetter<T> valueSetter() {
         return OptionInstance::set;
      }

      default Function<OptionInstance<T>, AbstractWidget> createButton(final TooltipSupplier<T> tooltip, final Options options, final int x, final int y, final int width, final ValueUpdateListener<? super T> onValueChanged) {
         return (instance) -> {
            Function var10000 = instance.toString;
            Objects.requireNonNull(instance);
            return CycleButton.builder(var10000, instance::get).withValues(this.valueListSupplier()).withTooltip(tooltip).create(x, y, width, 20, instance.caption, (var4, value) -> {
               this.valueSetter().set(instance, value);
               options.save();
               onValueChanged.valueChanged(value);
            });
         };
      }

      public interface ValueSetter<T> {
         void set(final OptionInstance<T> instance, final T value);
      }
   }

   public interface SliderableOrCyclableValueSet<T> extends SliderableValueSet<T>, CycleableValueSet<T> {
      boolean createCycleButton();

      default Function<OptionInstance<T>, AbstractWidget> createButton(final TooltipSupplier<T> tooltip, final Options options, final int x, final int y, final int width, final ValueUpdateListener<? super T> onValueChanged) {
         return this.createCycleButton() ? OptionInstance.CycleableValueSet.super.createButton(tooltip, options, x, y, width, onValueChanged) : OptionInstance.SliderableValueSet.super.createButton(tooltip, options, x, y, width, onValueChanged);
      }
   }

   public static record AltEnum<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec) implements CycleableValueSet<T> {
      public AltEnum {
         super();
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.<T>create(this.altCondition, this.values, this.altValues);
      }

      public Optional<T> validateValue(final T value) {
         return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
      }
   }

   public static record Enum<T>(List<T> values, Codec<T> codec) implements CycleableValueSet<T> {
      public Enum {
         super();
      }

      public Optional<T> validateValue(final T value) {
         return this.values.contains(value) ? Optional.of(value) : Optional.empty();
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.<T>create(this.values);
      }
   }

   public static record LazyEnum<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements CycleableValueSet<T> {
      public LazyEnum {
         super();
      }

      public Optional<T> validateValue(final T value) {
         return (Optional)this.validateValue.apply(value);
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.<T>create((Collection)this.values.get());
      }
   }

   public static final class OptionInstanceSliderButton<N> extends AbstractOptionSliderButton implements ResettableOptionWidget {
      private final OptionInstance<N> instance;
      private final SliderableValueSet<N> values;
      private final TooltipSupplier<N> tooltipSupplier;
      private final ValueUpdateListener<? super N> onValueChanged;
      private @Nullable Long delayedApplyAt;
      private final boolean applyValueImmediately;

      private OptionInstanceSliderButton(final Options options, final int x, final int y, final int width, final int height, final OptionInstance<N> instance, final SliderableValueSet<N> values, final TooltipSupplier<N> tooltipSupplier, final ValueUpdateListener<? super N> onValueChanged, final boolean applyValueImmediately) {
         super(options, x, y, width, height, values.toSliderValue(instance.get()));
         this.instance = instance;
         this.values = values;
         this.tooltipSupplier = tooltipSupplier;
         this.onValueChanged = onValueChanged;
         this.applyValueImmediately = applyValueImmediately;
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage((Component)this.instance.toString.apply(this.values.fromSliderValue(this.value)));
         this.setTooltip(this.tooltipSupplier.apply(this.values.fromSliderValue(this.value)));
      }

      protected void applyValue() {
         if (this.applyValueImmediately) {
            this.applyUnsavedValue();
         } else {
            this.delayedApplyAt = Util.getMillis() + 600L;
         }

      }

      public void applyUnsavedValue() {
         N sliderValue = this.values.fromSliderValue(this.value);
         if (!Objects.equals(sliderValue, this.instance.get())) {
            this.instance.set(sliderValue);
            this.onValueChanged.valueChanged(this.instance.get());
         }

      }

      public void resetValue() {
         if (this.value != this.values.toSliderValue(this.instance.get())) {
            this.value = this.values.toSliderValue(this.instance.get());
            this.delayedApplyAt = null;
            this.updateMessage();
         }

      }

      public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
         if (this.delayedApplyAt != null && Util.getMillis() >= this.delayedApplyAt) {
            this.delayedApplyAt = null;
            this.applyUnsavedValue();
            this.resetValue();
         }

      }

      public void onRelease(final MouseButtonEvent event) {
         super.onRelease(event);
         if (this.applyValueImmediately) {
            this.resetValue();
         }

      }

      public boolean keyPressed(final KeyEvent event) {
         if (event.isSelection()) {
            this.canChangeValue = !this.canChangeValue;
            return true;
         } else {
            if (this.canChangeValue) {
               boolean left = event.isLeft();
               boolean right = event.isRight();
               if (left) {
                  Optional<N> previous = this.values.previous(this.values.fromSliderValue(this.value));
                  if (previous.isPresent()) {
                     this.setValue(this.values.toSliderValue(previous.get()));
                     return true;
                  }
               }

               if (right) {
                  Optional<N> next = this.values.next(this.values.fromSliderValue(this.value));
                  if (next.isPresent()) {
                     this.setValue(this.values.toSliderValue(next.get()));
                     return true;
                  }
               }

               if (left || right) {
                  float direction = left ? -1.0F : 1.0F;
                  this.setValue(this.value + (double)(direction / (float)(this.width - 8)));
                  return true;
               }
            }

            return false;
         }
      }
   }

   public interface IntRangeBase extends SliderableValueSet<Integer> {
      int minInclusive();

      int maxInclusive();

      default Optional<Integer> next(final Integer current) {
         return Optional.of(current + 1);
      }

      default Optional<Integer> previous(final Integer current) {
         return Optional.of(current - 1);
      }

      default double toSliderValue(final Integer value) {
         if (value == this.minInclusive()) {
            return 0.0;
         } else {
            return value == this.maxInclusive() ? 1.0 : Mth.map((double)value + 0.5, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0, 0.0, 1.0);
         }
      }

      default Integer fromSliderValue(double slider) {
         if (slider >= 1.0) {
            slider = 0.9999899864196777;
         }

         return Mth.floor(Mth.map(slider, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0));
      }

      default <R> SliderableValueSet<R> xmap(final IntFunction<? extends R> to, final ToIntFunction<? super R> from, final boolean discrete) {
         return new SliderableValueSet<R>() {
            {
               Objects.requireNonNull(IntRangeBase.this);
            }

            public Optional<R> validateValue(final R value) {
               Optional var10000 = IntRangeBase.this.validateValue(from.applyAsInt(value));
               IntFunction var10001 = to;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderValue(final R value) {
               return IntRangeBase.this.toSliderValue(from.applyAsInt(value));
            }

            public Optional<R> next(final R current) {
               if (!discrete) {
                  return Optional.empty();
               } else {
                  int currentIndex = from.applyAsInt(current);
                  return Optional.of(to.apply((Integer)IntRangeBase.this.validateValue(currentIndex + 1).orElse(currentIndex)));
               }
            }

            public Optional<R> previous(final R current) {
               if (!discrete) {
                  return Optional.empty();
               } else {
                  int currentIndex = from.applyAsInt(current);
                  return Optional.of(to.apply((Integer)IntRangeBase.this.validateValue(currentIndex - 1).orElse(currentIndex)));
               }
            }

            public R fromSliderValue(final double slider) {
               return (R)to.apply(IntRangeBase.this.fromSliderValue(slider));
            }

            public Codec<R> codec() {
               Codec var10000 = IntRangeBase.this.codec();
               IntFunction var10001 = to;
               Objects.requireNonNull(var10001);
               Function var1 = var10001::apply;
               ToIntFunction var10002 = from;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1, var10002::applyAsInt);
            }
         };
      }
   }

   public static record IntRange(int minInclusive, int maxInclusive, boolean applyValueImmediately) implements IntRangeBase {
      public IntRange(final int minInclusive, final int maxInclusive) {
         this(minInclusive, maxInclusive, true);
      }

      public IntRange {
         super();
      }

      public Optional<Integer> validateValue(final Integer value) {
         return value.compareTo(this.minInclusive()) >= 0 && value.compareTo(this.maxInclusive()) <= 0 ? Optional.of(value) : Optional.empty();
      }

      public Codec<Integer> codec() {
         return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
      }
   }

   public static record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements IntRangeBase, SliderableOrCyclableValueSet<Integer> {
      public ClampingLazyMaxIntRange {
         super();
      }

      public Optional<Integer> validateValue(final Integer value) {
         return Optional.of(Mth.clamp(value, this.minInclusive(), this.maxInclusive()));
      }

      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      public Codec<Integer> codec() {
         return Codec.INT.validate((value) -> {
            int maxExclusive = this.encodableMaxInclusive + 1;
            return value.compareTo(this.minInclusive) >= 0 && value.compareTo(maxExclusive) <= 0 ? DataResult.success(value) : DataResult.error(() -> "Value " + value + " outside of range [" + this.minInclusive + ":" + maxExclusive + "]", value);
         });
      }

      public boolean createCycleButton() {
         return true;
      }

      public CycleButton.ValueListSupplier<Integer> valueListSupplier() {
         return CycleButton.ValueListSupplier.<Integer>create(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
      }
   }

   public static record SliderableEnum<T>(List<T> values, Codec<T> codec) implements SliderableValueSet<T> {
      public SliderableEnum {
         super();
      }

      public double toSliderValue(final T value) {
         if (value == this.values.getFirst()) {
            return 0.0;
         } else {
            return value == this.values.getLast() ? 1.0 : Mth.map((double)this.values.indexOf(value), 0.0, (double)(this.values.size() - 1), 0.0, 1.0);
         }
      }

      public Optional<T> next(final T current) {
         int currentIntex = this.values.indexOf(current);
         int nextIndex = Mth.clamp(currentIntex + 1, 0, this.values.size() - 1);
         return Optional.of(this.values.get(nextIndex));
      }

      public Optional<T> previous(final T current) {
         int currentIntex = this.values.indexOf(current);
         int previousIndex = Mth.clamp(currentIntex - 1, 0, this.values.size() - 1);
         return Optional.of(this.values.get(previousIndex));
      }

      public T fromSliderValue(double slider) {
         if (slider >= 1.0) {
            slider = 0.9999899864196777;
         }

         int index = Mth.floor(Mth.map(slider, 0.0, 1.0, 0.0, (double)this.values.size()));
         return (T)this.values.get(Mth.clamp(index, 0, this.values.size() - 1));
      }

      public Optional<T> validateValue(final T value) {
         int index = this.values.indexOf(value);
         return index > -1 ? Optional.of(value) : Optional.empty();
      }
   }

   public static enum UnitDouble implements SliderableValueSet<Double> {
      INSTANCE;

      private UnitDouble() {
      }

      public Optional<Double> validateValue(final Double value) {
         return value >= 0.0 && value <= 1.0 ? Optional.of(value) : Optional.empty();
      }

      public double toSliderValue(final Double value) {
         return value;
      }

      public Double fromSliderValue(final double slider) {
         return slider;
      }

      public <R> SliderableValueSet<R> xmap(final DoubleFunction<? extends R> to, final ToDoubleFunction<? super R> from) {
         return new SliderableValueSet<R>() {
            {
               Objects.requireNonNull(UnitDouble.this);
            }

            public Optional<R> validateValue(final R value) {
               Optional var10000 = UnitDouble.this.validateValue(from.applyAsDouble(value));
               DoubleFunction var10001 = to;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderValue(final R value) {
               return UnitDouble.this.toSliderValue(from.applyAsDouble(value));
            }

            public R fromSliderValue(final double slider) {
               return (R)to.apply(UnitDouble.this.fromSliderValue(slider));
            }

            public Codec<R> codec() {
               Codec var10000 = UnitDouble.this.codec();
               DoubleFunction var10001 = to;
               Objects.requireNonNull(var10001);
               Function var1 = var10001::apply;
               ToDoubleFunction var10002 = from;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1, var10002::applyAsDouble);
            }
         };
      }

      public Codec<Double> codec() {
         return Codec.withAlternative(Codec.doubleRange(0.0, 1.0), Codec.BOOL, (b) -> b ? 1.0 : 0.0);
      }

      // $FF: synthetic method
      private static UnitDouble[] $values() {
         return new UnitDouble[]{INSTANCE};
      }
   }

   @FunctionalInterface
   public interface CaptionBasedToString<T> {
      Component toString(Component caption, T value);
   }

   @FunctionalInterface
   public interface TooltipSupplier<T> {
      @Nullable Tooltip apply(T value);
   }

   public interface ValueSet<T> {
      Function<OptionInstance<T>, AbstractWidget> createButton(final TooltipSupplier<T> tooltip, Options options, final int x, final int y, final int width, final ValueUpdateListener<? super T> onValueChanged);

      Optional<T> validateValue(final T value);

      Codec<T> codec();
   }

   @FunctionalInterface
   public interface ValueUpdateListener<T> {
      void valueChanged(T newValue);
   }
}
