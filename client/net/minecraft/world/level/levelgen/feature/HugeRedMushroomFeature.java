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

public class HugeRedMushroomFeature extends Feature<HugeMushroomFeatureConfig> {
   public HugeRedMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> var1) {
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
            for(int var10 = 0; var10 <= var6; ++var10) {
               byte var11 = 0;
               if (var10 < var6 && var10 >= var6 - 3) {
                  var11 = 2;
               } else if (var10 == var6) {
                  var11 = 1;
               }

               for(var12 = -var11; var12 <= var11; ++var12) {
                  for(int var13 = -var11; var13 <= var11; ++var13) {
                     BlockState var14 = var1.getBlockState(var9.set((Vec3i)var4).move(var12, var10, var13));
                     if (!var14.isAir() && !var14.is(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            BlockState var22 = (BlockState)Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, false);

            for(int var23 = var6 - 3; var23 <= var6; ++var23) {
               var12 = var23 < var6 ? 2 : 1;
               boolean var25 = false;

               for(int var26 = -var12; var26 <= var12; ++var26) {
                  for(int var15 = -var12; var15 <= var12; ++var15) {
                     boolean var16 = var26 == -var12;
                     boolean var17 = var26 == var12;
                     boolean var18 = var15 == -var12;
                     boolean var19 = var15 == var12;
                     boolean var20 = var16 || var17;
                     boolean var21 = var18 || var19;
                     if (var23 >= var6 || var20 != var21) {
                        var9.set((Vec3i)var4).move(var26, var23, var15);
                        if (!var1.getBlockState(var9).isSolidRender(var1, var9)) {
                           this.setBlock(var1, var9, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var22.setValue(HugeMushroomBlock.UP, var23 >= var6 - 1)).setValue(HugeMushroomBlock.WEST, var26 < 0)).setValue(HugeMushroomBlock.EAST, var26 > 0)).setValue(HugeMushroomBlock.NORTH, var15 < 0)).setValue(HugeMushroomBlock.SOUTH, var15 > 0));
                        }
                     }
                  }
               }
            }

            BlockState var24 = (BlockState)((BlockState)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, false)).setValue(HugeMushroomBlock.DOWN, false);

            for(var12 = 0; var12 < var6; ++var12) {
               var9.set((Vec3i)var4).move(Direction.UP, var12);
               if (!var1.getBlockState(var9).isSolidRender(var1, var9)) {
                  if (var5.planted) {
                     var1.setBlock(var9, var24, 3);
                  } else {
                     this.setBlock(var1, var9, var24);
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
