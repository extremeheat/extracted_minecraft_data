package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock extends VegetationBlock implements BonemealableBlock {
   private static final int SEARCH_RADIUS = 4;
   private static final int MAX_NEIGHBORS_TO_GROW = 4;
   private static final VoxelShape SHAPE = Block.column(6.0, 0.0, 6.0);
   private final ResourceKey<Feature> feature;

   public MushroomBlock(final ResourceKey<Feature> feature, final BlockBehaviour.Properties properties) {
      super(properties);
      this.feature = feature;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected void randomTick(final BlockState state, final ServerLevel level, BlockPos pos, final RandomSource random) {
      if (random.nextInt(25) == 0) {
         if (!canSpread(level, pos)) {
            return;
         }

         BlockPos offset = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

         for(int i = 0; i < 4; ++i) {
            if (canSpreadTo(level, offset)) {
               pos = offset;
            }

            offset = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
         }

         if (canSpreadTo(level, offset)) {
            level.setBlock(offset, state, 2);
         }
      }

   }

   @VisibleForTesting
   public static boolean canSpread(final ServerLevel level, final BlockPos pos) {
      BlockPos minPos = pos.offset(-4, -1, -4);
      BlockPos maxPos = pos.offset(4, 1, 4);
      Block block = level.getBlockState(pos).getBlock();
      return level.findBlocksIn(minPos, maxPos).filterState((state) -> state.is(block)).atMostMatched(4);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return canSupportCenter(level, pos);
   }

   @VisibleForTesting
   public static boolean canSpreadTo(final LevelReader level, final BlockPos pos) {
      if (!level.isEmptyBlock(pos)) {
         return false;
      } else {
         BlockPos belowPos = pos.below();
         BlockState below = level.getBlockState(belowPos);
         if (below.is(BlockTags.OVERRIDES_MUSHROOM_LIGHT_REQUIREMENT)) {
            return true;
         } else if (level.getRawBrightness(pos, 0) >= 13) {
            return false;
         } else {
            return canSupportCenter(level, pos);
         }
      }
   }

   public boolean growMushroom(final ServerLevel level, final BlockPos pos, final BlockState state, final RandomSource random) {
      Optional<? extends Holder<Feature>> feature = level.registryAccess().lookupOrThrow(Registries.FEATURE).get(this.feature);
      if (feature.isEmpty()) {
         return false;
      } else {
         level.removeBlock(pos, false);
         if (((Feature)((Holder)feature.get()).value()).place(level, level.getChunkSource().getGenerator(), random, pos)) {
            return true;
         } else {
            level.setBlockAndUpdate(pos, state);
            return false;
         }
      }
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      if (level instanceof ServerLevel serverLevel) {
         Optional<? extends Holder<Feature>> featureHolder = serverLevel.registryAccess().lookupOrThrow(Registries.FEATURE).get(this.feature);
         if (featureHolder.isPresent()) {
            Feature feature = (Feature)((Holder)featureHolder.get()).value();
            if (feature instanceof AbstractHugeMushroomFeature) {
               AbstractHugeMushroomFeature mushroomFeature = (AbstractHugeMushroomFeature)feature;
               int minHeight = 4 + mushroomFeature.foliageRadius();
               return level.isInsideBuildHeight(pos.above(minHeight));
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return (double)random.nextFloat() < 0.4;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      this.growMushroom(level, pos, state, random);
   }

   protected static boolean canSupportCenter(final LevelReader level, final BlockPos pos) {
      return canSupportCenter(level, pos.below(), Direction.UP);
   }
}
