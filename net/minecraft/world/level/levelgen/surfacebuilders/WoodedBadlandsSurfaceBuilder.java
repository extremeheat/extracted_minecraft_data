package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;

   public WoodedBadlandsSurfaceBuilder(Function var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      int var15 = var4 & 15;
      int var16 = var5 & 15;
      BlockState var17 = WHITE_TERRACOTTA;
      BlockState var18 = var3.getSurfaceBuilderConfig().getUnderMaterial();
      int var19 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      boolean var20 = Math.cos(var7 / 3.0D * 3.141592653589793D) > 0.0D;
      int var21 = -1;
      boolean var22 = false;
      int var23 = 0;
      BlockPos.MutableBlockPos var24 = new BlockPos.MutableBlockPos();

      for(int var25 = var6; var25 >= 0; --var25) {
         if (var23 < 15) {
            var24.set(var15, var25, var16);
            BlockState var26 = var2.getBlockState(var24);
            if (var26.isAir()) {
               var21 = -1;
            } else if (var26.getBlock() == var9.getBlock()) {
               if (var21 == -1) {
                  var22 = false;
                  if (var19 <= 0) {
                     var17 = Blocks.AIR.defaultBlockState();
                     var18 = var9;
                  } else if (var25 >= var11 - 4 && var25 <= var11 + 1) {
                     var17 = WHITE_TERRACOTTA;
                     var18 = var3.getSurfaceBuilderConfig().getUnderMaterial();
                  }

                  if (var25 < var11 && (var17 == null || var17.isAir())) {
                     var17 = var10;
                  }

                  var21 = var19 + Math.max(0, var25 - var11);
                  if (var25 >= var11 - 1) {
                     if (var25 > 86 + var19 * 2) {
                        if (var20) {
                           var2.setBlockState(var24, Blocks.COARSE_DIRT.defaultBlockState(), false);
                        } else {
                           var2.setBlockState(var24, Blocks.GRASS_BLOCK.defaultBlockState(), false);
                        }
                     } else if (var25 > var11 + 3 + var19) {
                        BlockState var27;
                        if (var25 >= 64 && var25 <= 127) {
                           if (var20) {
                              var27 = TERRACOTTA;
                           } else {
                              var27 = this.getBand(var4, var25, var5);
                           }
                        } else {
                           var27 = ORANGE_TERRACOTTA;
                        }

                        var2.setBlockState(var24, var27, false);
                     } else {
                        var2.setBlockState(var24, var3.getSurfaceBuilderConfig().getTopMaterial(), false);
                        var22 = true;
                     }
                  } else {
                     var2.setBlockState(var24, var18, false);
                     if (var18 == WHITE_TERRACOTTA) {
                        var2.setBlockState(var24, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (var21 > 0) {
                  --var21;
                  if (var22) {
                     var2.setBlockState(var24, ORANGE_TERRACOTTA, false);
                  } else {
                     var2.setBlockState(var24, this.getBand(var4, var25, var5), false);
                  }
               }

               ++var23;
            }
         }
      }

   }

   static {
      WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
      ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
      TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
   }
}
