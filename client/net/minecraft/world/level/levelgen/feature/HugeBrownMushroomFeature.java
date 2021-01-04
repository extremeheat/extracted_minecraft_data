package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class HugeBrownMushroomFeature extends Feature<HugeMushroomFeatureConfig> {
   public HugeBrownMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, HugeMushroomFeatureConfig var5) {
      int var6 = var3.nextInt(3) + 4;
      if (var3.nextInt(12) == 0) {
         var6 *= 2;
      }

      int var7 = var4.getY();
      if (var7 >= 1 && var7 + var6 + 1 < 256) {
         Block var8 = var1.getBlockState(var4.below()).getBlock();
         if (!Block.equalsDirt(var8) && var8 != Blocks.GRASS_BLOCK && var8 != Blocks.MYCELIUM) {
            return false;
         } else {
            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            int var12;
            int var13;
            for(int var10 = 0; var10 <= 1 + var6; ++var10) {
               int var11 = var10 <= 3 ? 0 : 3;

               for(var12 = -var11; var12 <= var11; ++var12) {
                  for(var13 = -var11; var13 <= var11; ++var13) {
                     BlockState var14 = var1.getBlockState(var9.set((Vec3i)var4).move(var12, var10, var13));
                     if (!var14.isAir() && !var14.is(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            BlockState var24 = (BlockState)((BlockState)Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.UP, true)).setValue(HugeMushroomBlock.DOWN, false);
            boolean var25 = true;

            for(var12 = -3; var12 <= 3; ++var12) {
               for(var13 = -3; var13 <= 3; ++var13) {
                  boolean var27 = var12 == -3;
                  boolean var15 = var12 == 3;
                  boolean var16 = var13 == -3;
                  boolean var17 = var13 == 3;
                  boolean var18 = var27 || var15;
                  boolean var19 = var16 || var17;
                  if (!var18 || !var19) {
                     var9.set((Vec3i)var4).move(var12, var6, var13);
                     if (!var1.getBlockState(var9).isSolidRender(var1, var9)) {
                        boolean var20 = var27 || var19 && var12 == -2;
                        boolean var21 = var15 || var19 && var12 == 2;
                        boolean var22 = var16 || var18 && var13 == -2;
                        boolean var23 = var17 || var18 && var13 == 2;
                        this.setBlock(var1, var9, (BlockState)((BlockState)((BlockState)((BlockState)var24.setValue(HugeMushroomBlock.WEST, var20)).setValue(HugeMushroomBlock.EAST, var21)).setValue(HugeMushroomBlock.NORTH, var22)).setValue(HugeMushroomBlock.SOUTH, var23));
                     }
                  }
               }
            }

            BlockState var26 = (BlockState)((BlockState)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, false)).setValue(HugeMushroomBlock.DOWN, false);

            for(var13 = 0; var13 < var6; ++var13) {
               var9.set((Vec3i)var4).move(Direction.UP, var13);
               if (!var1.getBlockState(var9).isSolidRender(var1, var9)) {
                  if (var5.planted) {
                     var1.setBlock(var9, var26, 3);
                  } else {
                     this.setBlock(var1, var9, var26);
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
