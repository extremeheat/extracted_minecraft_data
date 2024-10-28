package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class WaterloggedVegetationPatchFeature extends VegetationPatchFeature {
   public WaterloggedVegetationPatchFeature(Codec<VegetationPatchConfiguration> var1) {
      super(var1);
   }

   protected Set<BlockPos> placeGroundPatch(WorldGenLevel var1, VegetationPatchConfiguration var2, RandomSource var3, BlockPos var4, Predicate<BlockState> var5, int var6, int var7) {
      Set var8 = super.placeGroundPatch(var1, var2, var3, var4, var5, var6, var7);
      HashSet var9 = new HashSet();
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
      Iterator var11 = var8.iterator();

      BlockPos var12;
      while(var11.hasNext()) {
         var12 = (BlockPos)var11.next();
         if (!isExposed(var1, var8, var12, var10)) {
            var9.add(var12);
         }
      }

      var11 = var9.iterator();

      while(var11.hasNext()) {
         var12 = (BlockPos)var11.next();
         var1.setBlock(var12, Blocks.WATER.defaultBlockState(), 2);
      }

      return var9;
   }

   private static boolean isExposed(WorldGenLevel var0, Set<BlockPos> var1, BlockPos var2, BlockPos.MutableBlockPos var3) {
      return isExposedDirection(var0, var2, var3, Direction.NORTH) || isExposedDirection(var0, var2, var3, Direction.EAST) || isExposedDirection(var0, var2, var3, Direction.SOUTH) || isExposedDirection(var0, var2, var3, Direction.WEST) || isExposedDirection(var0, var2, var3, Direction.DOWN);
   }

   private static boolean isExposedDirection(WorldGenLevel var0, BlockPos var1, BlockPos.MutableBlockPos var2, Direction var3) {
      var2.setWithOffset(var1, (Direction)var3);
      return !var0.getBlockState(var2).isFaceSturdy(var0, var2, var3.getOpposite());
   }

   protected boolean placeVegetation(WorldGenLevel var1, VegetationPatchConfiguration var2, ChunkGenerator var3, RandomSource var4, BlockPos var5) {
      if (super.placeVegetation(var1, var2, var3, var4, var5.below())) {
         BlockState var6 = var1.getBlockState(var5);
         if (var6.hasProperty(BlockStateProperties.WATERLOGGED) && !(Boolean)var6.getValue(BlockStateProperties.WATERLOGGED)) {
            var1.setBlock(var5, (BlockState)var6.setValue(BlockStateProperties.WATERLOGGED, true), 2);
         }

         return true;
      } else {
         return false;
      }
   }
}
