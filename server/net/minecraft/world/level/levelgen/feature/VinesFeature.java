package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VinesFeature extends Feature<NoneFeatureConfiguration> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public VinesFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      BlockPos.MutableBlockPos var6 = var4.mutable();

      for(int var7 = 64; var7 < 256; ++var7) {
         var6.set(var4);
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
