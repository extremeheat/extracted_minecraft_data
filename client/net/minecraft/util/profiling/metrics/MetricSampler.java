package net.minecraft.util.profiling.metrics;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class MetricSampler {
   private final String name;
   private final MetricCategory category;
   private final DoubleSupplier sampler;
   private final ByteBuf ticks;
   private final ByteBuf values;
   private volatile boolean isRunning;
   @Nullable
   private final Runnable beforeTick;
   @Nullable
   final MetricSampler.ThresholdTest thresholdTest;
   private double currentValue;

   protected MetricSampler(String var1, MetricCategory var2, DoubleSupplier var3, @Nullable Runnable var4, @Nullable MetricSampler.ThresholdTest var5) {
      super();
      this.name = var1;
      this.category = var2;
      this.beforeTick = var4;
      this.sampler = var3;
      this.thresholdTest = var5;
      this.values = ByteBufAllocator.DEFAULT.buffer();
      this.ticks = ByteBufAllocator.DEFAULT.buffer();
      this.isRunning = true;
   }

   public static MetricSampler create(String var0, MetricCategory var1, DoubleSupplier var2) {
      return new MetricSampler(var0, var1, var2, null, null);
   }

   public static <T> MetricSampler create(String var0, MetricCategory var1, T var2, ToDoubleFunction<T> var3) {
      return builder(var0, var1, var3, var2).build();
   }

   public static <T> MetricSampler.MetricSamplerBuilder<T> builder(String var0, MetricCategory var1, ToDoubleFunction<T> var2, T var3) {
      return new MetricSampler.MetricSamplerBuilder<>(var0, var1, var2, (T)var3);
   }

   public void onStartTick() {
      if (!this.isRunning) {
         throw new IllegalStateException("Not running");
      } else {
         if (this.beforeTick != null) {
            this.beforeTick.run();
         }
      }
   }

   public void onEndTick(int var1) {
      this.verifyRunning();
      this.currentValue = this.sampler.getAsDouble();
      this.values.writeDouble(this.currentValue);
      this.ticks.writeInt(var1);
   }

   public void onFinished() {
      this.verifyRunning();
      this.values.release();
      this.ticks.release();
      this.isRunning = false;
   }

   private void verifyRunning() {
      if (!this.isRunning) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Sampler for metric %s not started!", this.name));
      }
   }

   DoubleSupplier getSampler() {
      return this.sampler;
   }

   public String getName() {
      return this.name;
   }

   public MetricCategory getCategory() {
      return this.category;
   }

   public MetricSampler.SamplerResult result() {
      Int2DoubleOpenHashMap var1 = new Int2DoubleOpenHashMap();
      int var2 = -2147483648;

      int var3;
      int var4;
      for(var3 = -2147483648; this.values.isReadable(8); var3 = var4) {
         var4 = this.ticks.readInt();
         if (var2 == -2147483648) {
            var2 = var4;
         }

         var1.put(var4, this.values.readDouble());
      }

      return new MetricSampler.SamplerResult(var2, var3, var1);
   }

   public boolean triggersThreshold() {
      return this.thresholdTest != null && this.thresholdTest.test(this.currentValue);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MetricSampler var2 = (MetricSampler)var1;
         return this.name.equals(var2.name) && this.category.equals(var2.category);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   public static class MetricSamplerBuilder<T> {
      private final String name;
      private final MetricCategory category;
      private final DoubleSupplier sampler;
      private final T context;
      @Nullable
      private Runnable beforeTick;
      @Nullable
      private MetricSampler.ThresholdTest thresholdTest;

      public MetricSamplerBuilder(String var1, MetricCategory var2, ToDoubleFunction<T> var3, T var4) {
         super();
         this.name = var1;
         this.category = var2;
         this.sampler = () -> var3.applyAsDouble(var4);
         this.context = (T)var4;
      }

      public MetricSampler.MetricSamplerBuilder<T> withBeforeTick(Consumer<T> var1) {
         this.beforeTick = () -> var1.accept(this.context);
         return this;
      }

      public MetricSampler.MetricSamplerBuilder<T> withThresholdAlert(MetricSampler.ThresholdTest var1) {
         this.thresholdTest = var1;
         return this;
      }

      public MetricSampler build() {
         return new MetricSampler(this.name, this.category, this.sampler, this.beforeTick, this.thresholdTest);
      }
   }

   public static class SamplerResult {
      private final Int2DoubleMap recording;
      private final int firstTick;
      private final int lastTick;

      public SamplerResult(int var1, int var2, Int2DoubleMap var3) {
         super();
         this.firstTick = var1;
         this.lastTick = var2;
         this.recording = var3;
      }

      public double valueAtTick(int var1) {
         return this.recording.get(var1);
      }

      public int getFirstTick() {
         return this.firstTick;
      }

      public int getLastTick() {
         return this.lastTick;
      }
   }

   public interface ThresholdTest {
      boolean test(double var1);
   }

   public static class ValueIncreasedByPercentage implements MetricSampler.ThresholdTest {
      private final float percentageIncreaseThreshold;
      private double previousValue = 5.0E-324;

      public ValueIncreasedByPercentage(float var1) {
         super();
         this.percentageIncreaseThreshold = var1;
      }

      @Override
      public boolean test(double var1) {
         boolean var3;
         if (this.previousValue != 5.0E-324 && !(var1 <= this.previousValue)) {
            var3 = (var1 - this.previousValue) / this.previousValue >= (double)this.percentageIncreaseThreshold;
         } else {
            var3 = false;
         }

         this.previousValue = var1;
         return var3;
      }
   }
}
