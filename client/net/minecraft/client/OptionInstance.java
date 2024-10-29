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
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
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
   public static final Enum<Boolean> BOOLEAN_VALUES;
   public static final CaptionBasedToString<Boolean> BOOLEAN_TO_STRING;
   private final TooltipSupplier<T> tooltip;
   final Function<T, Component> toString;
   private final ValueSet<T> values;
   private final Codec<T> codec;
   private final T initialValue;
   private final Consumer<T> onValueUpdate;
   final Component caption;
   T value;

   public static OptionInstance<Boolean> createBoolean(String var0, boolean var1, Consumer<Boolean> var2) {
      return createBoolean(var0, noTooltip(), var1, var2);
   }

   public static OptionInstance<Boolean> createBoolean(String var0, boolean var1) {
      return createBoolean(var0, noTooltip(), var1, (var0x) -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String var0, TooltipSupplier<Boolean> var1, boolean var2) {
      return createBoolean(var0, var1, var2, (var0x) -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String var0, TooltipSupplier<Boolean> var1, boolean var2, Consumer<Boolean> var3) {
      return createBoolean(var0, var1, BOOLEAN_TO_STRING, var2, var3);
   }

   public static OptionInstance<Boolean> createBoolean(String var0, TooltipSupplier<Boolean> var1, CaptionBasedToString<Boolean> var2, boolean var3, Consumer<Boolean> var4) {
      return new OptionInstance(var0, var1, var2, BOOLEAN_VALUES, var3, var4);
   }

   public OptionInstance(String var1, TooltipSupplier<T> var2, CaptionBasedToString<T> var3, ValueSet<T> var4, T var5, Consumer<T> var6) {
      this(var1, var2, var3, var4, var4.codec(), var5, var6);
   }

   public OptionInstance(String var1, TooltipSupplier<T> var2, CaptionBasedToString<T> var3, ValueSet<T> var4, Codec<T> var5, T var6, Consumer<T> var7) {
      super();
      this.caption = Component.translatable(var1);
      this.tooltip = var2;
      this.toString = (var2x) -> {
         return var3.toString(this.caption, var2x);
      };
      this.values = var4;
      this.codec = var5;
      this.initialValue = var6;
      this.onValueUpdate = var7;
      this.value = this.initialValue;
   }

   public static <T> TooltipSupplier<T> noTooltip() {
      return (var0) -> {
         return null;
      };
   }

   public static <T> TooltipSupplier<T> cachedConstantTooltip(Component var0) {
      return (var1) -> {
         return Tooltip.create(var0);
      };
   }

   public static <T extends OptionEnum> CaptionBasedToString<T> forOptionEnum() {
      return (var0, var1) -> {
         return var1.getCaption();
      };
   }

   public AbstractWidget createButton(Options var1) {
      return this.createButton(var1, 0, 0, 150);
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4) {
      return this.createButton(var1, var2, var3, var4, (var0) -> {
      });
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4, Consumer<T> var5) {
      return (AbstractWidget)this.values.createButton(this.tooltip, var1, var2, var3, var4, var5).apply(this);
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

   public void set(T var1) {
      Object var2 = this.values.validateValue(var1).orElseGet(() -> {
         Logger var10000 = LOGGER;
         String var10001 = String.valueOf(var1);
         var10000.error("Illegal option value " + var10001 + " for " + String.valueOf(this.caption));
         return this.initialValue;
      });
      if (!Minecraft.getInstance().isRunning()) {
         this.value = var2;
      } else {
         if (!Objects.equals(this.value, var2)) {
            this.value = var2;
            this.onValueUpdate.accept(this.value);
         }

      }
   }

   public ValueSet<T> values() {
      return this.values;
   }

   static {
      BOOLEAN_VALUES = new Enum(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
      BOOLEAN_TO_STRING = (var0, var1) -> {
         return var1 ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
      };
   }

   @FunctionalInterface
   public interface TooltipSupplier<T> {
      @Nullable
      Tooltip apply(T var1);
   }

   public interface CaptionBasedToString<T> {
      Component toString(Component var1, T var2);
   }

   public static record Enum<T>(List<T> values, Codec<T> codec) implements CycleableValueSet<T> {
      public Enum(List<T> var1, Codec<T> var2) {
         super();
         this.values = var1;
         this.codec = var2;
      }

      public Optional<T> validateValue(T var1) {
         return this.values.contains(var1) ? Optional.of(var1) : Optional.empty();
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.values);
      }

      public List<T> values() {
         return this.values;
      }

      public Codec<T> codec() {
         return this.codec;
      }
   }

   interface ValueSet<T> {
      Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6);

      Optional<T> validateValue(T var1);

      Codec<T> codec();
   }

   public static enum UnitDouble implements SliderableValueSet<Double> {
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

      public <R> SliderableValueSet<R> xmap(final DoubleFunction<? extends R> var1, final ToDoubleFunction<? super R> var2) {
         return new SliderableValueSet<R>() {
            public Optional<R> validateValue(R var1x) {
               Optional var10000 = UnitDouble.this.validateValue(var2.applyAsDouble(var1x));
               DoubleFunction var10001 = var1;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderValue(R var1x) {
               return UnitDouble.this.toSliderValue(var2.applyAsDouble(var1x));
            }

            public R fromSliderValue(double var1x) {
               return var1.apply(UnitDouble.this.fromSliderValue(var1x));
            }

            public Codec<R> codec() {
               Codec var10000 = UnitDouble.this.codec();
               DoubleFunction var10001 = var1;
               Objects.requireNonNull(var10001);
               Function var1x = var10001::apply;
               ToDoubleFunction var10002 = var2;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1x, var10002::applyAsDouble);
            }
         };
      }

      public Codec<Double> codec() {
         return Codec.withAlternative(Codec.doubleRange(0.0, 1.0), Codec.BOOL, (var0) -> {
            return var0 ? 1.0 : 0.0;
         });
      }

      // $FF: synthetic method
      public Object fromSliderValue(final double var1) {
         return this.fromSliderValue(var1);
      }

      // $FF: synthetic method
      private static UnitDouble[] $values() {
         return new UnitDouble[]{INSTANCE};
      }
   }

   public static record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements IntRangeBase, SliderableOrCyclableValueSet<Integer> {
      public ClampingLazyMaxIntRange(int var1, IntSupplier var2, int var3) {
         super();
         this.minInclusive = var1;
         this.maxSupplier = var2;
         this.encodableMaxInclusive = var3;
      }

      public Optional<Integer> validateValue(Integer var1) {
         return Optional.of(Mth.clamp(var1, this.minInclusive(), this.maxInclusive()));
      }

      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      public Codec<Integer> codec() {
         return Codec.INT.validate((var1) -> {
            int var2 = this.encodableMaxInclusive + 1;
            return var1.compareTo(this.minInclusive) >= 0 && var1.compareTo(var2) <= 0 ? DataResult.success(var1) : DataResult.error(() -> {
               return "Value " + var1 + " outside of range [" + this.minInclusive + ":" + var2 + "]";
            }, var1);
         });
      }

      public boolean createCycleButton() {
         return true;
      }

      public CycleButton.ValueListSupplier<Integer> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
      }

      public int minInclusive() {
         return this.minInclusive;
      }

      public IntSupplier maxSupplier() {
         return this.maxSupplier;
      }

      public int encodableMaxInclusive() {
         return this.encodableMaxInclusive;
      }
   }

   public static record IntRange(int minInclusive, int maxInclusive, boolean applyValueImmediately) implements IntRangeBase {
      public IntRange(int var1, int var2) {
         this(var1, var2, true);
      }

      public IntRange(int var1, int var2, boolean var3) {
         super();
         this.minInclusive = var1;
         this.maxInclusive = var2;
         this.applyValueImmediately = var3;
      }

      public Optional<Integer> validateValue(Integer var1) {
         return var1.compareTo(this.minInclusive()) >= 0 && var1.compareTo(this.maxInclusive()) <= 0 ? Optional.of(var1) : Optional.empty();
      }

      public Codec<Integer> codec() {
         return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
      }

      public int minInclusive() {
         return this.minInclusive;
      }

      public int maxInclusive() {
         return this.maxInclusive;
      }

      public boolean applyValueImmediately() {
         return this.applyValueImmediately;
      }
   }

   interface IntRangeBase extends SliderableValueSet<Integer> {
      int minInclusive();

      int maxInclusive();

      default double toSliderValue(Integer var1) {
         if (var1 == this.minInclusive()) {
            return 0.0;
         } else {
            return var1 == this.maxInclusive() ? 1.0 : Mth.map((double)var1 + 0.5, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0, 0.0, 1.0);
         }
      }

      default Integer fromSliderValue(double var1) {
         if (var1 >= 1.0) {
            var1 = 0.9999899864196777;
         }

         return Mth.floor(Mth.map(var1, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0));
      }

      default <R> SliderableValueSet<R> xmap(final IntFunction<? extends R> var1, final ToIntFunction<? super R> var2) {
         return new SliderableValueSet<R>() {
            public Optional<R> validateValue(R var1x) {
               Optional var10000 = IntRangeBase.this.validateValue(var2.applyAsInt(var1x));
               IntFunction var10001 = var1;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderValue(R var1x) {
               return IntRangeBase.this.toSliderValue(var2.applyAsInt(var1x));
            }

            public R fromSliderValue(double var1x) {
               return var1.apply(IntRangeBase.this.fromSliderValue(var1x));
            }

            public Codec<R> codec() {
               Codec var10000 = IntRangeBase.this.codec();
               IntFunction var10001 = var1;
               Objects.requireNonNull(var10001);
               Function var1x = var10001::apply;
               ToIntFunction var10002 = var2;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1x, var10002::applyAsInt);
            }
         };
      }

      // $FF: synthetic method
      default Object fromSliderValue(double var1) {
         return this.fromSliderValue(var1);
      }
   }

   public static final class OptionInstanceSliderButton<N> extends AbstractOptionSliderButton {
      private final OptionInstance<N> instance;
      private final SliderableValueSet<N> values;
      private final TooltipSupplier<N> tooltipSupplier;
      private final Consumer<N> onValueChanged;
      @Nullable
      private Long delayedApplyAt;
      private final boolean applyValueImmediately;

      OptionInstanceSliderButton(Options var1, int var2, int var3, int var4, int var5, OptionInstance<N> var6, SliderableValueSet<N> var7, TooltipSupplier<N> var8, Consumer<N> var9, boolean var10) {
         super(var1, var2, var3, var4, var5, var7.toSliderValue(var6.get()));
         this.instance = var6;
         this.values = var7;
         this.tooltipSupplier = var8;
         this.onValueChanged = var9;
         this.applyValueImmediately = var10;
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
         Object var1 = this.values.fromSliderValue(this.value);
         if (!Objects.equals(var1, this.instance.get())) {
            this.instance.set(var1);
            this.onValueChanged.accept(this.instance.get());
         }

      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         super.renderWidget(var1, var2, var3, var4);
         if (this.delayedApplyAt != null && Util.getMillis() >= this.delayedApplyAt) {
            this.delayedApplyAt = null;
            this.applyUnsavedValue();
         }

      }
   }

   public static record LazyEnum<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements CycleableValueSet<T> {
      public LazyEnum(Supplier<List<T>> var1, Function<T, Optional<T>> var2, Codec<T> var3) {
         super();
         this.values = var1;
         this.validateValue = var2;
         this.codec = var3;
      }

      public Optional<T> validateValue(T var1) {
         return (Optional)this.validateValue.apply(var1);
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create((Collection)this.values.get());
      }

      public Supplier<List<T>> values() {
         return this.values;
      }

      public Function<T, Optional<T>> validateValue() {
         return this.validateValue;
      }

      public Codec<T> codec() {
         return this.codec;
      }
   }

   public static record AltEnum<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec) implements CycleableValueSet<T> {
      public AltEnum(List<T> var1, List<T> var2, BooleanSupplier var3, CycleableValueSet.ValueSetter<T> var4, Codec<T> var5) {
         super();
         this.values = var1;
         this.altValues = var2;
         this.altCondition = var3;
         this.valueSetter = var4;
         this.codec = var5;
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.altCondition, this.values, this.altValues);
      }

      public Optional<T> validateValue(T var1) {
         return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(var1) ? Optional.of(var1) : Optional.empty();
      }

      public List<T> values() {
         return this.values;
      }

      public List<T> altValues() {
         return this.altValues;
      }

      public BooleanSupplier altCondition() {
         return this.altCondition;
      }

      public CycleableValueSet.ValueSetter<T> valueSetter() {
         return this.valueSetter;
      }

      public Codec<T> codec() {
         return this.codec;
      }
   }

   interface SliderableOrCyclableValueSet<T> extends CycleableValueSet<T>, SliderableValueSet<T> {
      boolean createCycleButton();

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6) {
         return this.createCycleButton() ? OptionInstance.CycleableValueSet.super.createButton(var1, var2, var3, var4, var5, var6) : OptionInstance.SliderableValueSet.super.createButton(var1, var2, var3, var4, var5, var6);
      }
   }

   interface CycleableValueSet<T> extends ValueSet<T> {
      CycleButton.ValueListSupplier<T> valueListSupplier();

      default ValueSetter<T> valueSetter() {
         return OptionInstance::set;
      }

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6) {
         return (var7) -> {
            return CycleButton.builder(var7.toString).withValues(this.valueListSupplier()).withTooltip(var1).withInitialValue(var7.value).create(var3, var4, var5, 20, var7.caption, (var4x, var5x) -> {
               this.valueSetter().set(var7, var5x);
               var2.save();
               var6.accept(var5x);
            });
         };
      }

      public interface ValueSetter<T> {
         void set(OptionInstance<T> var1, T var2);
      }
   }

   interface SliderableValueSet<T> extends ValueSet<T> {
      double toSliderValue(T var1);

      T fromSliderValue(double var1);

      default boolean applyValueImmediately() {
         return true;
      }

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6) {
         return (var7) -> {
            return new OptionInstanceSliderButton(var2, var3, var4, var5, 20, var7, this, var1, var6, this.applyValueImmediately());
         };
      }
   }
}
