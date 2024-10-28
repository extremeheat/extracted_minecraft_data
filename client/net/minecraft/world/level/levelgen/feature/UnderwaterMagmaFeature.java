package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class UnderwaterMagmaFeature extends Feature<UnderwaterMagmaConfiguration> {
   public UnderwaterMagmaFeature(Codec<UnderwaterMagmaConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<UnderwaterMagmaConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      UnderwaterMagmaConfiguration var4 = (UnderwaterMagmaConfiguration)var1.config();
      RandomSource var5 = var1.random();
      OptionalInt var6 = getFloorY(var2, var3, var4);
      if (var6.isEmpty()) {
         return false;
      } else {
         BlockPos var7 = var3.atY(var6.getAsInt());
         Vec3i var8 = new Vec3i(var4.placementRadiusAroundFloor, var4.placementRadiusAroundFloor, var4.placementRadiusAroundFloor);
         BoundingBox var9 = BoundingBox.fromCorners(var7.subtract(var8), var7.offset(var8));
         return BlockPos.betweenClosedStream(var9).filter((var2x) -> {
            return var5.nextFloat() < var4.placementProbabilityPerValidPosition;
         }).filter((var2x) -> {
            return this.isValidPlacement(var2, var2x);
         }).mapToInt((var1x) -> {
            var2.setBlock(var1x, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
            return 1;
         }).sum() > 0;
      }
   }

   private static OptionalInt getFloorY(WorldGenLevel var0, BlockPos var1, UnderwaterMagmaConfiguration var2) {
      Predicate var3 = (var0x) -> {
         return var0x.is(Blocks.WATER);
      };
      Predicate var4 = (var0x) -> {
         return !var0x.is(Blocks.WATER);
      };
      Optional var5 = Column.scan(var0, var1, var2.floorSearchRange, var3, var4);
      return (OptionalInt)var5.map(Column::getFloor).orElseGet(OptionalInt::empty);
   }

   private boolean isValidPlacement(WorldGenLevel var1, BlockPos var2) {
      if (!this.isWaterOrAir(var1, var2) && !this.isWaterOrAir(var1, var2.below())) {
         Iterator var3 = Direction.Plane.HORIZONTAL.iterator();

         Direction var4;
         do {
            if (!var3.hasNext()) {
               return true;
            }

            var4 = (Direction)var3.next();
         } while(!this.isWaterOrAir(var1, var2.relative(var4)));

         return false;
      } else {
         return false;
      }
   }

   private boolean isWaterOrAir(LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      return var3.is(Blocks.WATER) || var3.isAir();
   }
}
