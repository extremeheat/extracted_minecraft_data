package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;

public record CacheFunction(DensityFunction input) implements DensityFunction {
   public static final MapCodec<CacheFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(CacheFunction::input)).apply(i, CacheFunction::new));

   public CacheFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      throw new IllegalStateException("Cannot compile cache before it has been deduplicated");
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      return input == this.input ? this : new CacheFunction(input);
   }

   public Interval range() {
      return this.input.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes();
   }

   public MapCodec<CacheFunction> codec() {
      return CODEC;
   }
}
