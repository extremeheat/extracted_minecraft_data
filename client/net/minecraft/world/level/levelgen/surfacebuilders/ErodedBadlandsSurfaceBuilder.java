package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
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

   public ErodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> var1) {
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

      int var34 = var4 & 15;
      int var20 = var5 & 15;
      BlockState var35 = WHITE_TERRACOTTA;
      SurfaceBuilderConfiguration var22 = var3.getGenerationSettings().getSurfaceBuilderConfig();
      BlockState var36 = var22.getUnderMaterial();
      BlockState var24 = var22.getTopMaterial();
      BlockState var25 = var36;
      int var26 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      boolean var27 = Math.cos(var7 / 3.0D * 3.141592653589793D) > 0.0D;
      int var28 = -1;
      boolean var29 = false;
      BlockPos.MutableBlockPos var30 = new BlockPos.MutableBlockPos();

      for(int var31 = Math.max(var6, (int)var15 + 1); var31 >= 0; --var31) {
         var30.set(var34, var31, var20);
         if (var2.getBlockState(var30).isAir() && var31 < (int)var15) {
            var2.setBlockState(var30, var9, false);
         }

         BlockState var32 = var2.getBlockState(var30);
         if (var32.isAir()) {
            var28 = -1;
         } else if (var32.is(var9.getBlock())) {
            if (var28 == -1) {
               var29 = false;
               if (var26 <= 0) {
                  var35 = Blocks.AIR.defaultBlockState();
                  var25 = var9;
               } else if (var31 >= var11 - 4 && var31 <= var11 + 1) {
                  var35 = WHITE_TERRACOTTA;
                  var25 = var36;
               }

               if (var31 < var11 && (var35 == null || var35.isAir())) {
                  var35 = var10;
               }

               var28 = var26 + Math.max(0, var31 - var11);
               if (var31 >= var11 - 1) {
                  if (var31 <= var11 + 3 + var26) {
                     var2.setBlockState(var30, var24, false);
                     var29 = true;
                  } else {
                     BlockState var37;
                     if (var31 >= 64 && var31 <= 127) {
                        if (var27) {
                           var37 = TERRACOTTA;
                        } else {
                           var37 = this.getBand(var4, var31, var5);
                        }
                     } else {
                        var37 = ORANGE_TERRACOTTA;
                     }

                     var2.setBlockState(var30, var37, false);
                  }
               } else {
                  var2.setBlockState(var30, var25, false);
                  Block var33 = var25.getBlock();
                  if (var33 == Blocks.WHITE_TERRACOTTA || var33 == Blocks.ORANGE_TERRACOTTA || var33 == Blocks.MAGENTA_TERRACOTTA || var33 == Blocks.LIGHT_BLUE_TERRACOTTA || var33 == Blocks.YELLOW_TERRACOTTA || var33 == Blocks.LIME_TERRACOTTA || var33 == Blocks.PINK_TERRACOTTA || var33 == Blocks.GRAY_TERRACOTTA || var33 == Blocks.LIGHT_GRAY_TERRACOTTA || var33 == Blocks.CYAN_TERRACOTTA || var33 == Blocks.PURPLE_TERRACOTTA || var33 == Blocks.BLUE_TERRACOTTA || var33 == Blocks.BROWN_TERRACOTTA || var33 == Blocks.GREEN_TERRACOTTA || var33 == Blocks.RED_TERRACOTTA || var33 == Blocks.BLACK_TERRACOTTA) {
                     var2.setBlockState(var30, ORANGE_TERRACOTTA, false);
                  }
               }
            } else if (var28 > 0) {
               --var28;
               if (var29) {
                  var2.setBlockState(var30, ORANGE_TERRACOTTA, false);
               } else {
                  var2.setBlockState(var30, this.getBand(var4, var31, var5), false);
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
