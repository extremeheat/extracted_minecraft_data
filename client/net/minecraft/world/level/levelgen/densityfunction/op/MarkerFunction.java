package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record MarkerFunction(Type type, DensityFunction wrapped) implements DensityFunction {
   public MarkerFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.wrapped.compute(context);
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.wrapped.fillArray(output, contextProvider);
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new MarkerFunction(this.type, visitor.apply(this.wrapped));
   }

   public Interval range() {
      Interval var10000;
      switch (this.type.ordinal()) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
            var10000 = this.wrapped.range();
            break;
         case 5:
            var10000 = Interval.INFINITE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @DensityFunction.Axes int domainAxes() {
      int var10000;
      switch (this.type.ordinal()) {
         case 0:
         case 3:
         case 4:
         case 5:
            var10000 = this.wrapped.domainAxes();
            break;
         case 1:
         case 2:
            var10000 = this.wrapped.domainAxes() & -3;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public MapCodec<MarkerFunction> codec() {
      return this.type.codec;
   }

   public static enum Type implements StringRepresentable {
      Interpolated("interpolated"),
      FlatCache("flat_cache"),
      Cache2D("cache_2d"),
      CacheOnce("cache_once"),
      CacheAllInCell("cache_all_in_cell"),
      BlendDensity("blend_density");

      private final String name;
      public final MapCodec<MarkerFunction> codec = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(MarkerFunction::wrapped)).apply(i, (input) -> new MarkerFunction(this, input)));

      private Type(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{Interpolated, FlatCache, Cache2D, CacheOnce, CacheAllInCell, BlendDensity};
      }
   }
}
