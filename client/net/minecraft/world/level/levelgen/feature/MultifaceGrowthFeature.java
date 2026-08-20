package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record MultifaceGrowthFeature(Block placeBlock, int searchRange, boolean canPlaceOnFloor, boolean canPlaceOnCeiling, boolean canPlaceOnWall, float chanceOfSpreading, HolderSet<Block> canBePlacedOn) implements Feature {
   public static final MapCodec<MultifaceGrowthFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BuiltInRegistries.BLOCK.byNameCodec().validate(MultifaceGrowthFeature::validateBlock).fieldOf("block").forGetter(MultifaceGrowthFeature::placeBlock), Codec.intRange(1, 64).optionalFieldOf("search_range", 10).forGetter(MultifaceGrowthFeature::searchRange), Codec.BOOL.optionalFieldOf("can_place_on_floor", false).forGetter(MultifaceGrowthFeature::canPlaceOnFloor), Codec.BOOL.optionalFieldOf("can_place_on_ceiling", false).forGetter(MultifaceGrowthFeature::canPlaceOnCeiling), Codec.BOOL.optionalFieldOf("can_place_on_wall", false).forGetter(MultifaceGrowthFeature::canPlaceOnWall), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance_of_spreading", 0.5F).forGetter(MultifaceGrowthFeature::chanceOfSpreading), RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("can_be_placed_on").forGetter(MultifaceGrowthFeature::canBePlacedOn)).apply(i, MultifaceGrowthFeature::new));

   public MultifaceGrowthFeature {
      super();
   }

   private static DataResult<Block> validateBlock(final Block block) {
      DataResult var10000;
      if (block instanceof MultifaceSpreadeableBlock multifaceBlock) {
         var10000 = DataResult.success(multifaceBlock);
      } else {
         var10000 = DataResult.error(() -> "Growth block should be a multiface spreadeable block");
      }

      return var10000;
   }

   public MapCodec<MultifaceGrowthFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      if (!isAirOrWater(level.getBlockState(origin))) {
         return false;
      } else {
         Block var6 = this.placeBlock;
         if (!(var6 instanceof MultifaceSpreadeableBlock)) {
            return false;
         } else {
            MultifaceSpreadeableBlock placerBlock = (MultifaceSpreadeableBlock)var6;
            List var13 = this.getShuffledDirections(random);
            if (this.placeGrowthIfPossible(placerBlock, level, origin, level.getBlockState(origin), random, var13)) {
               return true;
            } else {
               BlockPos.MutableBlockPos pos = origin.mutable();

               for(Direction searchDirection : var13) {
                  pos.set(origin);
                  List<Direction> placementDirections = this.getShuffledDirectionsExcept(random, searchDirection.getOpposite());

                  for(int i = 0; i < this.searchRange; ++i) {
                     pos.setWithOffset(origin, (Direction)searchDirection);
                     BlockState state = level.getBlockState(pos);
                     if (!isAirOrWater(state) && !state.is(this.placeBlock)) {
                        break;
                     }

                     if (this.placeGrowthIfPossible(placerBlock, level, pos, state, random, placementDirections)) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      }
   }

   public boolean placeGrowthIfPossible(final MultifaceSpreadeableBlock placerBlock, final WorldGenLevel level, final BlockPos pos, final BlockState oldState, final RandomSource random, final List<Direction> placementDirections) {
      BlockPos.MutableBlockPos mutable = pos.mutable();

      for(Direction placementDirection : placementDirections) {
         BlockState neighbourState = level.getBlockState(mutable.setWithOffset(pos, (Direction)placementDirection));
         if (neighbourState.is(this.canBePlacedOn)) {
            BlockState newState = placerBlock.getStateForPlacement(oldState, level, pos, placementDirection);
            if (newState == null) {
               return false;
            }

            level.setBlockAndUpdate(pos, newState);
            level.getChunk(pos).markPosForPostProcessing(pos);
            if (random.nextFloat() < this.chanceOfSpreading) {
               placerBlock.getSpreader().spreadFromFaceTowardRandomDirection(newState, level, pos, placementDirection, random, true);
            }

            return true;
         }
      }

      return false;
   }

   private ObjectArrayList<Direction> validDirections() {
      ObjectArrayList<Direction> validDirections = new ObjectArrayList(6);
      if (this.canPlaceOnCeiling) {
         validDirections.add(Direction.UP);
      }

      if (this.canPlaceOnFloor) {
         validDirections.add(Direction.DOWN);
      }

      if (this.canPlaceOnWall) {
         Direction.Plane var10000 = Direction.Plane.HORIZONTAL;
         Objects.requireNonNull(validDirections);
         var10000.forEach(validDirections::add);
      }

      return validDirections;
   }

   private List<Direction> getShuffledDirectionsExcept(final RandomSource random, final Direction excludeDirection) {
      return Util.toShuffledList(this.validDirections().stream().filter((direction) -> direction != excludeDirection), random);
   }

   private List<Direction> getShuffledDirections(final RandomSource random) {
      return Util.shuffledCopy(this.validDirections(), random);
   }

   private static boolean isAirOrWater(final BlockState state) {
      return state.isAir() || state.is(Blocks.WATER);
   }
}
