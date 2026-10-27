package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record CornerConstraint(List<DensityPredicate> densityPredicates) {
   public static final Codec<CornerConstraint> CODEC;

   public CornerConstraint {
      super();
   }

   public boolean test(final BoundingBox boundingBox, final RandomState randomState) {
      for(DensityPredicate densityPredicate : this.densityPredicates()) {
         if (!densityPredicate.test(boundingBox, randomState)) {
            return false;
         }
      }

      return true;
   }

   static {
      CODEC = CornerConstraint.DensityPredicate.CODEC.listOf(1, 10).xmap(CornerConstraint::new, CornerConstraint::densityPredicates);
   }

   public static record DensityPredicate(Holder<DensityFunction> densityFunction, InclusiveRange<Double> acceptableDensity, int padding, CornersToCheck corners, InclusiveRange<Integer> requiredCorners) {
      public static final Codec<DensityPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(DensityFunction.REFERENCE_CODEC.fieldOf("density_function").forGetter(DensityPredicate::densityFunction), InclusiveRange.codec(Codec.DOUBLE).fieldOf("acceptable_density").forGetter(DensityPredicate::acceptableDensity), Codec.intRange(0, 8).optionalFieldOf("padding", 0).forGetter(DensityPredicate::padding), CornersToCheck.CODEC.optionalFieldOf("corners", CornersToCheck.ALL).forGetter(DensityPredicate::corners), InclusiveRange.codec(Codec.intRange(1, 8)).fieldOf("required_corners").forGetter(DensityPredicate::requiredCorners)).apply(i, DensityPredicate::new)).validate((densityPredicate) -> densityPredicate.corners != CornersToCheck.ALL && (Integer)densityPredicate.requiredCorners.maxInclusive() > 4 ? DataResult.error(() -> "The number of required corners must be at most 4 when corners is " + densityPredicate.corners.name()) : DataResult.success(densityPredicate));

      public DensityPredicate {
         super();
      }

      private boolean test(final BoundingBox boundingBox, final RandomState randomState) {
         DensitySampler sampler = randomState.getSampler((DensityFunction)this.densityFunction().value());
         BoundingBox paddedBoundingBox = boundingBox.inflatedBy(this.padding());
         List<BlockPos> corners = getCorners(paddedBoundingBox, this.corners());
         int totalCornerCount = corners.size();
         int checkedCorners = 0;
         int acceptedCorners = 0;

         for(BlockPos pos : corners) {
            double density = (double)sampler.sampleValue(SamplerContext.EMPTY_UNCACHED, pos.getX(), pos.getY(), pos.getZ());
            ++checkedCorners;
            if (this.acceptableDensity().isValueInRange(density)) {
               ++acceptedCorners;
               if (acceptedCorners > (Integer)this.requiredCorners().maxInclusive()) {
                  return false;
               }
            }

            int remainingCorners = totalCornerCount - checkedCorners;
            if (acceptedCorners + remainingCorners < (Integer)this.requiredCorners().minInclusive()) {
               return false;
            }
         }

         return true;
      }

      private static List<BlockPos> getCorners(final BoundingBox boundingBox, final CornersToCheck corners) {
         List<BlockPos> result = new ArrayList(corners == CornersToCheck.ALL ? 8 : 4);
         Consumer<BlockPos> consumer = (pos) -> result.add(pos.immutable());
         switch (corners) {
            case BOTTOM -> boundingBox.forAllBottomCorners(consumer);
            case TOP -> boundingBox.forAllTopCorners(consumer);
            default -> boundingBox.forAllCorners(consumer);
         }

         return result;
      }
   }
}
