package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VinesFeature extends Feature {
   private static final Direction[] DIRECTIONS = Direction.values();

   public VinesFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(var4);

      for(int var7 = var4.getY(); var7 < 256; ++var7) {
         var6.set((Vec3i)var4);
         var6.move(var3.nextInt(4) - var3.nextInt(4), 0, var3.nextInt(4) - var3.nextInt(4));
         var6.setY(var7);
         if (var1.isEmptyBlock(var6)) {
            Direction[] var8 = DIRECTIONS;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Direction var11 = var8[var10];
               if (var11 != Direction.DOWN && VineBlock.isAcceptableNeighbour(var1, var6, var11)) {
                  var1.setBlock(var6, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(var11), true), 2);
                  break;
               }
            }
         }
      }

      return true;
   }
}
