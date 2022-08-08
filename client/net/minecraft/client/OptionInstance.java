package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
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
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import org.slf4j.Logger;

public final class OptionInstance<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Enum<Boolean> BOOLEAN_VALUES;
   private static final int TOOLTIP_WIDTH = 200;
   private final TooltipSupplierFactory<T> tooltip;
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

   public static OptionInstance<Boolean> createBoolean(String var0, TooltipSupplierFactory<Boolean> var1, boolean var2) {
      return createBoolean(var0, var1, var2, (var0x) -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String var0, TooltipSupplierFactory<Boolean> var1, boolean var2, Consumer<Boolean> var3) {
      return new OptionInstance(var0, var1, (var0x, var1x) -> {
         return var1x ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
      }, BOOLEAN_VALUES, var2, var3);
   }

   public OptionInstance(String var1, TooltipSupplierFactory<T> var2, CaptionBasedToString<T> var3, ValueSet<T> var4, T var5, Consumer<T> var6) {
      this(var1, var2, var3, var4, var4.codec(), var5, var6);
   }

   public OptionInstance(String var1, TooltipSupplierFactory<T> var2, CaptionBasedToString<T> var3, ValueSet<T> var4, Codec<T> var5, T var6, Consumer<T> var7) {
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

   public static <T> TooltipSupplierFactory<T> noTooltip() {
      return (var0) -> {
         return (var0x) -> {
            return ImmutableList.of();
         };
      };
   }

   public static <T> TooltipSupplierFactory<T> cachedConstantTooltip(Component var0) {
      return (var1) -> {
         List var2 = splitTooltip(var1, var0);
         return (var1x) -> {
            return var2;
         };
      };
   }

   public static <T extends OptionEnum> CaptionBasedToString<T> forOptionEnum() {
      return (var0, var1) -> {
         return var1.getCaption();
      };
   }

   protected static List<FormattedCharSequence> splitTooltip(Minecraft var0, Component var1) {
      return var0.font.split(var1, 200);
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4) {
      TooltipSupplier var5 = (TooltipSupplier)this.tooltip.apply(Minecraft.getInstance());
      return (AbstractWidget)this.values.createButton(var5, var1, var2, var3, var4).apply(this);
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
         LOGGER.error("Illegal option value " + var1 + " for " + this.caption);
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
   }

   public interface TooltipSupplierFactory<T> extends Function<Minecraft, TooltipSupplier<T>> {
   }

   public interface CaptionBasedToString<T> {
      Component toString(Component var1, T var2);
   }

   public static record Enum<T>(List<T> a, Codec<T> b) implements CycleableValueSet<T> {
      private final List<T> values;
      private final Codec<T> codec;

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
      Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5);

      Optional<T> validateValue(T var1);

      Codec<T> codec();
   }

   @FunctionalInterface
   public interface TooltipSupplier<T> extends Function<T, List<FormattedCharSequence>> {
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
         return Codec.either(Codec.doubleRange(0.0, 1.0), Codec.BOOL).xmap((var0) -> {
            return (Double)var0.map((var0x) -> {
               return var0x;
            }, (var0x) -> {
               return var0x ? 1.0 : 0.0;
            });
         }, Either::left);
      }

      // $FF: synthetic method
      public Object fromSliderValue(double var1) {
         return this.fromSliderValue(var1);
      }

      // $FF: synthetic method
      private static UnitDouble[] $values() {
         return new UnitDouble[]{INSTANCE};
      }
   }

   public static record ClampingLazyMaxIntRange(int a, IntSupplier b) implements IntRangeBase, SliderableOrCyclableValueSet<Integer> {
      private final int minInclusive;
      private final IntSupplier maxSupplier;

      public ClampingLazyMaxIntRange(int var1, IntSupplier var2) {
         super();
         this.minInclusive = var1;
         this.maxSupplier = var2;
      }

      public Optional<Integer> validateValue(Integer var1) {
         return Optional.of(Mth.clamp(var1, this.minInclusive(), this.maxInclusive()));
      }

      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      public Codec<Integer> codec() {
         Function var1 = (var1x) -> {
            int var2 = this.maxSupplier.getAsInt() + 1;
            return var1x.compareTo(this.minInclusive) >= 0 && var1x.compareTo(var2) <= 0 ? DataResult.success(var1x) : DataResult.error("Value " + var1x + " outside of range [" + this.minInclusive + ":" + var2 + "]", var1x);
         };
         return Codec.INT.flatXmap(var1, var1);
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
   }

   public static record IntRange(int a, int b) implements IntRangeBase {
      private final int minInclusive;
      private final int maxInclusive;

      public IntRange(int var1, int var2) {
         super();
         this.minInclusive = var1;
         this.maxInclusive = var2;
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
   }

   interface IntRangeBase extends SliderableValueSet<Integer> {
      int minInclusive();

      int maxInclusive();

      default double toSliderValue(Integer var1) {
         return (double)Mth.map((float)var1, (float)this.minInclusive(), (float)this.maxInclusive(), 0.0F, 1.0F);
      }

      default Integer fromSliderValue(double var1) {
         return Mth.floor(Mth.map(var1, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive()));
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

   private static final class OptionInstanceSliderButton<N> extends AbstractOptionSliderButton implements TooltipAccessor {
      private final OptionInstance<N> instance;
      private final SliderableValueSet<N> values;
      private final TooltipSupplier<N> tooltip;

      OptionInstanceSliderButton(Options var1, int var2, int var3, int var4, int var5, OptionInstance<N> var6, SliderableValueSet<N> var7, TooltipSupplier<N> var8) {
         super(var1, var2, var3, var4, var5, var7.toSliderValue(var6.get()));
         this.instance = var6;
         this.values = var7;
         this.tooltip = var8;
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage((Component)this.instance.toString.apply(this.instance.get()));
      }

      protected void applyValue() {
         this.instance.set(this.values.fromSliderValue(this.value));
         this.options.save();
      }

      public List<FormattedCharSequence> getTooltip() {
         return (List)this.tooltip.apply(this.values.fromSliderValue(this.value));
      }
   }

   public static record LazyEnum<T>(Supplier<List<T>> a, Function<T, Optional<T>> b, Codec<T> c) implements CycleableValueSet<T> {
      private final Supplier<List<T>> values;
      private final Function<T, Optional<T>> validateValue;
      private final Codec<T> codec;

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

   public static record AltEnum<T>(List<T> a, List<T> b, BooleanSupplier c, CycleableValueSet.ValueSetter<T> d, Codec<T> e) implements CycleableValueSet<T> {
      private final List<T> values;
      private final List<T> altValues;
      private final BooleanSupplier altCondition;
      private final CycleableValueSet.ValueSetter<T> valueSetter;
      private final Codec<T> codec;

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

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5) {
         return this.createCycleButton() ? OptionInstance.CycleableValueSet.super.createButton(var1, var2, var3, var4, var5) : OptionInstance.SliderableValueSet.super.createButton(var1, var2, var3, var4, var5);
      }
   }

   interface CycleableValueSet<T> extends ValueSet<T> {
      CycleButton.ValueListSupplier<T> valueListSupplier();

      default ValueSetter<T> valueSetter() {
         return OptionInstance::set;
      }

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5) {
         return (var6) -> {
            return CycleButton.builder(var6.toString).withValues(this.valueListSupplier()).withTooltip(var1).withInitialValue(var6.value).create(var3, var4, var5, 20, var6.caption, (var3x, var4x) -> {
               this.valueSetter().set(var6, var4x);
               var2.save();
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

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5) {
         return (var6) -> {
            return new OptionInstanceSliderButton(var2, var3, var4, var5, 20, var6, this, var1);
         };
      }
   }
}
