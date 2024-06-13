package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import org.slf4j.Logger;

public final class OptionInstance<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final OptionInstance.Enum<Boolean> BOOLEAN_VALUES = new OptionInstance.Enum<>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
   public static final OptionInstance.CaptionBasedToString<Boolean> BOOLEAN_TO_STRING = (var0, var1) -> var1
         ? CommonComponents.OPTION_ON
         : CommonComponents.OPTION_OFF;
   private final OptionInstance.TooltipSupplier<T> tooltip;
   final Function<T, Component> toString;
   private final OptionInstance.ValueSet<T> values;
   private final Codec<T> codec;
   private final T initialValue;
   private final Consumer<T> onValueUpdate;
   final Component caption;
   T value;

   public static OptionInstance<Boolean> createBoolean(String var0, boolean var1, Consumer<Boolean> var2) {
      return createBoolean(var0, noTooltip(), var1, var2);
   }

   public static OptionInstance<Boolean> createBoolean(String var0, boolean var1) {
      return createBoolean(var0, noTooltip(), var1, var0x -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String var0, OptionInstance.TooltipSupplier<Boolean> var1, boolean var2) {
      return createBoolean(var0, var1, var2, var0x -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String var0, OptionInstance.TooltipSupplier<Boolean> var1, boolean var2, Consumer<Boolean> var3) {
      return createBoolean(var0, var1, BOOLEAN_TO_STRING, var2, var3);
   }

   public static OptionInstance<Boolean> createBoolean(
      String var0, OptionInstance.TooltipSupplier<Boolean> var1, OptionInstance.CaptionBasedToString<Boolean> var2, boolean var3, Consumer<Boolean> var4
   ) {
      return new OptionInstance<>(var0, var1, var2, BOOLEAN_VALUES, var3, var4);
   }

   public OptionInstance(
      String var1,
      OptionInstance.TooltipSupplier<T> var2,
      OptionInstance.CaptionBasedToString<T> var3,
      OptionInstance.ValueSet<T> var4,
      T var5,
      Consumer<T> var6
   ) {
      this(var1, var2, var3, var4, var4.codec(), (T)var5, var6);
   }

   public OptionInstance(
      String var1,
      OptionInstance.TooltipSupplier<T> var2,
      OptionInstance.CaptionBasedToString<T> var3,
      OptionInstance.ValueSet<T> var4,
      Codec<T> var5,
      T var6,
      Consumer<T> var7
   ) {
      super();
      this.caption = Component.translatable(var1);
      this.tooltip = var2;
      this.toString = var2x -> var3.toString(this.caption, var2x);
      this.values = var4;
      this.codec = var5;
      this.initialValue = (T)var6;
      this.onValueUpdate = var7;
      this.value = this.initialValue;
   }

   public static <T> OptionInstance.TooltipSupplier<T> noTooltip() {
      return var0 -> null;
   }

   public static <T> OptionInstance.TooltipSupplier<T> cachedConstantTooltip(Component var0) {
      return var1 -> Tooltip.create(var0);
   }

   public static <T extends OptionEnum> OptionInstance.CaptionBasedToString<T> forOptionEnum() {
      return (var0, var1) -> var1.getCaption();
   }

   public AbstractWidget createButton(Options var1) {
      return this.createButton(var1, 0, 0, 150);
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4) {
      return this.createButton(var1, var2, var3, var4, var0 -> {
      });
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4, Consumer<T> var5) {
      return this.values.createButton(this.tooltip, var1, var2, var3, var4, var5).apply(this);
   }

   public T get() {
      return this.value;
   }

   public Codec<T> codec() {
      return this.codec;
   }

   @Override
   public String toString() {
      return this.caption.getString();
   }

   public void set(T var1) {
      Object var2 = this.values.validateValue((T)var1).orElseGet(() -> {
         LOGGER.error("Illegal option value " + var1 + " for " + this.caption);
         return this.initialValue;
      });
      if (!Minecraft.getInstance().isRunning()) {
         this.value = (T)var2;
      } else {
         if (!Objects.equals(this.value, var2)) {
            this.value = (T)var2;
            this.onValueUpdate.accept(this.value);
         }
      }
   }

   public OptionInstance.ValueSet<T> values() {
      return this.values;
   }

   public static record AltEnum<T>(
      List<T> values, List<T> altValues, BooleanSupplier altCondition, OptionInstance.CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec
   ) implements OptionInstance.CycleableValueSet<T> {
      public AltEnum(
         List<T> values, List<T> altValues, BooleanSupplier altCondition, OptionInstance.CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec
      ) {
         super();
         this.values = values;
         this.altValues = altValues;
         this.altCondition = altCondition;
         this.valueSetter = valueSetter;
         this.codec = codec;
      }

      @Override
      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.altCondition, this.values, this.altValues);
      }

      @Override
      public Optional<T> validateValue(T var1) {
         return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(var1) ? Optional.of((T)var1) : Optional.empty();
      }
   }

   public interface CaptionBasedToString<T> {
      Component toString(Component var1, T var2);
   }

   public static record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive)
      implements OptionInstance.IntRangeBase,
      OptionInstance.SliderableOrCyclableValueSet<Integer> {
      public ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) {
         super();
         this.minInclusive = minInclusive;
         this.maxSupplier = maxSupplier;
         this.encodableMaxInclusive = encodableMaxInclusive;
      }

      public Optional<Integer> validateValue(Integer var1) {
         return Optional.of(Mth.clamp(var1, this.minInclusive(), this.maxInclusive()));
      }

      @Override
      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      @Override
      public Codec<Integer> codec() {
         return Codec.INT
            .validate(
               var1 -> {
                  int var2 = this.encodableMaxInclusive + 1;
                  return var1.compareTo(this.minInclusive) >= 0 && var1.compareTo(var2) <= 0
                     ? DataResult.success(var1)
                     : DataResult.error(() -> "Value " + var1 + " outside of range [" + this.minInclusive + ":" + var2 + "]", var1);
               }
            );
      }

      @Override
      public boolean createCycleButton() {
         return true;
      }

      @Override
      public CycleButton.ValueListSupplier<Integer> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
      }
   }

   interface CycleableValueSet<T> extends OptionInstance.ValueSet<T> {
      CycleButton.ValueListSupplier<T> valueListSupplier();

      default OptionInstance.CycleableValueSet.ValueSetter<T> valueSetter() {
         return OptionInstance::set;
      }

      @Override
      default Function<OptionInstance<T>, AbstractWidget> createButton(
         OptionInstance.TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6
      ) {
         return var7 -> CycleButton.builder(var7.toString)
               .withValues(this.valueListSupplier())
               .withTooltip(var1)
               .withInitialValue(var7.value)
               .create(var3, var4, var5, 20, var7.caption, (var4xx, var5xx) -> {
                  this.valueSetter().set(var7, var5xx);
                  var2.save();
                  var6.accept(var5xx);
               });
      }

      public interface ValueSetter<T> {
         void set(OptionInstance<T> var1, T var2);
      }
   }

   public static record Enum<T>(List<T> values, Codec<T> codec) implements OptionInstance.CycleableValueSet<T> {
      public Enum(List<T> values, Codec<T> codec) {
         super();
         this.values = values;
         this.codec = codec;
      }

      @Override
      public Optional<T> validateValue(T var1) {
         return this.values.contains(var1) ? Optional.of((T)var1) : Optional.empty();
      }

      @Override
      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.values);
      }
   }

   public static record IntRange(int minInclusive, int maxInclusive) implements OptionInstance.IntRangeBase {
      public IntRange(int minInclusive, int maxInclusive) {
         super();
         this.minInclusive = minInclusive;
         this.maxInclusive = maxInclusive;
      }

      public Optional<Integer> validateValue(Integer var1) {
         return var1.compareTo(this.minInclusive()) >= 0 && var1.compareTo(this.maxInclusive()) <= 0 ? Optional.of(var1) : Optional.empty();
      }

      @Override
      public Codec<Integer> codec() {
         return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
      }
   }

   interface IntRangeBase extends OptionInstance.SliderableValueSet<Integer> {
      int minInclusive();

      int maxInclusive();

      default double toSliderValue(Integer var1) {
         return (double)Mth.map((float)var1.intValue(), (float)this.minInclusive(), (float)this.maxInclusive(), 0.0F, 1.0F);
      }

      default Integer fromSliderValue(double var1) {
         return Mth.floor(Mth.map(var1, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive()));
      }

      default <R> OptionInstance.SliderableValueSet<R> xmap(final IntFunction<? extends R> var1, final ToIntFunction<? super R> var2) {
         return new OptionInstance.SliderableValueSet<R>() {
            @Override
            public Optional<R> validateValue(R var1x) {
               return IntRangeBase.this.validateValue(Integer.valueOf(var2.applyAsInt(var1x))).map(var1::apply);
            }

            @Override
            public double toSliderValue(R var1x) {
               return IntRangeBase.this.toSliderValue(var2.applyAsInt(var1x));
            }

            @Override
            public R fromSliderValue(double var1x) {
               return (R)var1.apply(IntRangeBase.this.fromSliderValue(var1x));
            }

            @Override
            public Codec<R> codec() {
               return IntRangeBase.this.codec().xmap(var1::apply, var2::applyAsInt);
            }
         };
      }
   }

   public static record LazyEnum<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec)
      implements OptionInstance.CycleableValueSet<T> {
      public LazyEnum(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) {
         super();
         this.values = values;
         this.validateValue = validateValue;
         this.codec = codec;
      }

      @Override
      public Optional<T> validateValue(T var1) {
         return this.validateValue.apply((T)var1);
      }

      @Override
      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.values.get());
      }
   }

   static final class OptionInstanceSliderButton<N> extends AbstractOptionSliderButton {
      private final OptionInstance<N> instance;
      private final OptionInstance.SliderableValueSet<N> values;
      private final OptionInstance.TooltipSupplier<N> tooltipSupplier;
      private final Consumer<N> onValueChanged;

      OptionInstanceSliderButton(
         Options var1,
         int var2,
         int var3,
         int var4,
         int var5,
         OptionInstance<N> var6,
         OptionInstance.SliderableValueSet<N> var7,
         OptionInstance.TooltipSupplier<N> var8,
         Consumer<N> var9
      ) {
         super(var1, var2, var3, var4, var5, var7.toSliderValue(var6.get()));
         this.instance = var6;
         this.values = var7;
         this.tooltipSupplier = var8;
         this.onValueChanged = var9;
         this.updateMessage();
      }

      @Override
      protected void updateMessage() {
         this.setMessage(this.instance.toString.apply(this.instance.get()));
         this.setTooltip(this.tooltipSupplier.apply(this.values.fromSliderValue(this.value)));
      }

      @Override
      protected void applyValue() {
         this.instance.set(this.values.fromSliderValue(this.value));
         this.options.save();
         this.onValueChanged.accept(this.instance.get());
      }
   }

   interface SliderableOrCyclableValueSet<T> extends OptionInstance.CycleableValueSet<T>, OptionInstance.SliderableValueSet<T> {
      boolean createCycleButton();

      @Override
      default Function<OptionInstance<T>, AbstractWidget> createButton(
         OptionInstance.TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6
      ) {
         return this.createCycleButton()
            ? OptionInstance.CycleableValueSet.super.createButton(var1, var2, var3, var4, var5, var6)
            : OptionInstance.SliderableValueSet.super.createButton(var1, var2, var3, var4, var5, var6);
      }
   }

   interface SliderableValueSet<T> extends OptionInstance.ValueSet<T> {
      double toSliderValue(T var1);

      T fromSliderValue(double var1);

      @Override
      default Function<OptionInstance<T>, AbstractWidget> createButton(
         OptionInstance.TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6
      ) {
         return var7 -> new OptionInstance.OptionInstanceSliderButton<>(var2, var3, var4, var5, 20, var7, this, var1, var6);
      }
   }

   @FunctionalInterface
   public interface TooltipSupplier<T> {
      @Nullable
      Tooltip apply(T var1);
   }

   public static enum UnitDouble implements OptionInstance.SliderableValueSet<Double> {
      INSTANCE;

      private UnitDouble() {
      }

      public Optional<Double> validateValue(Double var1) {
         return var1 >= 0.0 && var1 <= 1.0 ? Optional.of(var1) : Optional.empty();
      }

      public double toSliderValue(Double var1) {
         return var1;
      }

      public Double fromSliderValue(double var1) {
         return var1;
      }

      public <R> OptionInstance.SliderableValueSet<R> xmap(final DoubleFunction<? extends R> var1, final ToDoubleFunction<? super R> var2) {
         return new OptionInstance.SliderableValueSet<R>() {
            @Override
            public Optional<R> validateValue(R var1x) {
               return UnitDouble.this.validateValue(var2.applyAsDouble(var1x)).map(var1::apply);
            }

            @Override
            public double toSliderValue(R var1x) {
               return UnitDouble.this.toSliderValue(var2.applyAsDouble(var1x));
            }

            @Override
            public R fromSliderValue(double var1x) {
               return (R)var1.apply(UnitDouble.this.fromSliderValue(var1x));
            }

            @Override
            public Codec<R> codec() {
               return UnitDouble.this.codec().xmap(var1::apply, var2::applyAsDouble);
            }
         };
      }

      @Override
      public Codec<Double> codec() {
         return Codec.withAlternative(Codec.doubleRange(0.0, 1.0), Codec.BOOL, var0 -> var0 ? 1.0 : 0.0);
      }
   }

   interface ValueSet<T> {
      Function<OptionInstance<T>, AbstractWidget> createButton(
         OptionInstance.TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6
      );

      Optional<T> validateValue(T var1);

      Codec<T> codec();
   }
}
