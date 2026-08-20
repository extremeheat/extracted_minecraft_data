package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DistanceMetric;

public record DistanceToPointFunction(Vec3i point, DistanceMetric metric) implements DensityFunction {
   public static final MapCodec<DistanceToPointFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Vec3i.CODEC.fieldOf("point").forGetter(DistanceToPointFunction::point), DistanceMetric.CODEC.fieldOf("metric").forGetter(DistanceToPointFunction::metric)).apply(i, DistanceToPointFunction::new));

   public DistanceToPointFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.metric.compute((float)(this.point.getX() - context.blockX()), (float)(this.point.getY() - context.blockY()), (float)(this.point.getZ() - context.blockZ()));
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   public Interval range() {
      return Interval.of(0.0F, 1.0F / 0.0F);
   }

   public @DensityFunction.Axes int domainAxes() {
      return 7;
   }

   public MapCodec<DistanceToPointFunction> codec() {
      return CODEC;
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return this;
   }
}
