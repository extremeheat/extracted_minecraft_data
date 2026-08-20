package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public interface ShiftNoiseFunction extends DensityFunction {
   DensityFunction.NoiseHolder offsetNoise();

   default Interval range() {
      return Interval.mul(this.offsetNoise().range(), Interval.ofExact(4.0F));
   }

   default float compute(final double localX, final double localY, final double localZ) {
      return this.offsetNoise().getValue(localX * 0.25, localY * 0.25, localZ * 0.25) * 4.0F;
   }

   default void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   MapCodec<? extends ShiftNoiseFunction> codec();

   public static record Shift(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoiseFunction {
      public static final MapCodec<Shift> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(Shift::offsetNoise)).apply(i, Shift::new));

      public Shift {
         super();
      }

      public float compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockX(), (double)context.blockY(), (double)context.blockZ());
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new Shift(visitor.visitNoise(this.offsetNoise));
      }

      public @DensityFunction.Axes int domainAxes() {
         return 7;
      }

      public MapCodec<Shift> codec() {
         return CODEC;
      }
   }

   public static record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoiseFunction {
      public static final MapCodec<ShiftA> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftA::offsetNoise)).apply(i, ShiftA::new));

      public ShiftA {
         super();
      }

      public float compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockX(), 0.0, (double)context.blockZ());
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftA(visitor.visitNoise(this.offsetNoise));
      }

      public @DensityFunction.Axes int domainAxes() {
         return 5;
      }

      public MapCodec<ShiftA> codec() {
         return CODEC;
      }
   }

   public static record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoiseFunction {
      public static final MapCodec<ShiftB> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftB::offsetNoise)).apply(i, ShiftB::new));

      public ShiftB {
         super();
      }

      public float compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockZ(), (double)context.blockX(), 0.0);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftB(visitor.visitNoise(this.offsetNoise));
      }

      public @DensityFunction.Axes int domainAxes() {
         return 5;
      }

      public MapCodec<ShiftB> codec() {
         return CODEC;
      }
   }
}
