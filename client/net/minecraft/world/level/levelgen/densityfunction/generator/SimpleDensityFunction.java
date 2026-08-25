package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.ContextBoundSampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;

public enum SimpleDensityFunction implements DensityFunction {
   BLEND_ALPHA("blend_alpha"),
   BLEND_OFFSET("blend_offset"),
   BEARDIFIER("beardifier");

   private final String id;
   private final MapCodec<SimpleDensityFunction> codec = MapCodec.unit(this);

   private SimpleDensityFunction(final String id) {
      this.id = id;
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      ContextBoundSampler var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = new ContextBoundSampler(Blender.ALPHA_KEY, new ConstantFunction.Sampler(1.0F));
         case 1 -> var10000 = new ContextBoundSampler(Blender.OFFSET_KEY, new ConstantFunction.Sampler(0.0F));
         case 2 -> var10000 = new ContextBoundSampler(Beardifier.CONTEXT_KEY, new ConstantFunction.Sampler(0.0F));
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
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
