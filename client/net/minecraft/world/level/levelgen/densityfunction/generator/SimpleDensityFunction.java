package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public enum SimpleDensityFunction implements DensityFunction {
   BLEND_ALPHA("blend_alpha"),
   BLEND_OFFSET("blend_offset"),
   BEARDIFIER("beardifier");

   private final String id;
   private final MapCodec<SimpleDensityFunction> codec = MapCodec.unit(this);

   private SimpleDensityFunction(final String id) {
      this.id = id;
   }

   private float value() {
      float var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = 1.0F;
            break;
         case 1:
         case 2:
            var10000 = 0.0F;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.value();
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      Arrays.fill(output, this.value());
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return this;
   }

   public Interval range() {
      Interval var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = Interval.of(0.0F, 1.0F);
         case 1 -> var10000 = Interval.INFINITE;
         case 2 -> var10000 = Beardifier.RANGE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @DensityFunction.Axes int domainAxes() {
      byte var10000;
      switch (this.ordinal()) {
         case 0:
         case 1:
            var10000 = 5;
            break;
         case 2:
            var10000 = 7;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String id() {
      return this.id;
   }

   public MapCodec<SimpleDensityFunction> codec() {
      return this.codec;
   }

   // $FF: synthetic method
   private static SimpleDensityFunction[] $values() {
      return new SimpleDensityFunction[]{BLEND_ALPHA, BLEND_OFFSET, BEARDIFIER};
   }
}
