package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;

   public ErodedBadlandsSurfaceBuilder(Function var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      double var15 = 0.0D;
      double var17 = Math.min(Math.abs(var7), this.pillarNoise.getValue((double)var4 * 0.25D, (double)var5 * 0.25D, false) * 15.0D);
      if (var17 > 0.0D) {
         double var19 = 0.001953125D;
         double var21 = Math.abs(this.pillarRoofNoise.getValue((double)var4 * 0.001953125D, (double)var5 * 0.001953125D, false));
         var15 = var17 * var17 * 2.5D;
         double var23 = Math.ceil(var21 * 50.0D) + 14.0D;
         if (var15 > var23) {
            var15 = var23;
         }

         var15 += 64.0D;
      }

      int var31 = var4 & 15;
      int var20 = var5 & 15;
      BlockState var32 = WHITE_TERRACOTTA;
      BlockState var22 = var3.getSurfaceBuilderConfig().getUnderMaterial();
      int var33 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      boolean var24 = Math.cos(var7 / 3.0D * 3.141592653589793D) > 0.0D;
      int var25 = -1;
      boolean var26 = false;
      BlockPos.MutableBlockPos var27 = new BlockPos.MutableBlockPos();

      for(int var28 = Math.max(var6, (int)var15 + 1); var28 >= 0; --var28) {
         var27.set(var31, var28, var20);
         if (var2.getBlockState(var27).isAir() && var28 < (int)var15) {
            var2.setBlockState(var27, var9, false);
         }

         BlockState var29 = var2.getBlockState(var27);
         if (var29.isAir()) {
            var25 = -1;
         } else if (var29.getBlock() == var9.getBlock()) {
            if (var25 == -1) {
               var26 = false;
               if (var33 <= 0) {
                  var32 = Blocks.AIR.defaultBlockState();
                  var22 = var9;
               } else if (var28 >= var11 - 4 && var28 <= var11 + 1) {
                  var32 = WHITE_TERRACOTTA;
                  var22 = var3.getSurfaceBuilderConfig().getUnderMaterial();
               }

               if (var28 < var11 && (var32 == null || var32.isAir())) {
                  var32 = var10;
               }

               var25 = var33 + Math.max(0, var28 - var11);
               if (var28 >= var11 - 1) {
                  if (var28 <= var11 + 3 + var33) {
                     var2.setBlockState(var27, var3.getSurfaceBuilderConfig().getTopMaterial(), false);
                     var26 = true;
                  } else {
                     BlockState var34;
                     if (var28 >= 64 && var28 <= 127) {
                        if (var24) {
                           var34 = TERRACOTTA;
                        } else {
                           var34 = this.getBand(var4, var28, var5);
                        }
                     } else {
                        var34 = ORANGE_TERRACOTTA;
                     }

                     var2.setBlockState(var27, var34, false);
                  }
               } else {
                  var2.setBlockState(var27, var22, false);
                  Block var30 = var22.getBlock();
                  if (var30 == Blocks.WHITE_TERRACOTTA || var30 == Blocks.ORANGE_TERRACOTTA || var30 == Blocks.MAGENTA_TERRACOTTA || var30 == Blocks.LIGHT_BLUE_TERRACOTTA || var30 == Blocks.YELLOW_TERRACOTTA || var30 == Blocks.LIME_TERRACOTTA || var30 == Blocks.PINK_TERRACOTTA || var30 == Blocks.GRAY_TERRACOTTA || var30 == Blocks.LIGHT_GRAY_TERRACOTTA || var30 == Blocks.CYAN_TERRACOTTA || var30 == Blocks.PURPLE_TERRACOTTA || var30 == Blocks.BLUE_TERRACOTTA || var30 == Blocks.BROWN_TERRACOTTA || var30 == Blocks.GREEN_TERRACOTTA || var30 == Blocks.RED_TERRACOTTA || var30 == Blocks.BLACK_TERRACOTTA) {
                     var2.setBlockState(var27, ORANGE_TERRACOTTA, false);
                  }
               }
            } else if (var25 > 0) {
               --var25;
               if (var26) {
                  var2.setBlockState(var27, ORANGE_TERRACOTTA, false);
               } else {
                  var2.setBlockState(var27, this.getBand(var4, var28, var5), false);
               }
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
