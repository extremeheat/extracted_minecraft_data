package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;

   public WoodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      int var15 = var4 & 15;
      int var16 = var5 & 15;
      BlockState var17 = WHITE_TERRACOTTA;
      SurfaceBuilderConfiguration var18 = var3.getGenerationSettings().getSurfaceBuilderConfig();
      BlockState var19 = var18.getUnderMaterial();
      BlockState var20 = var18.getTopMaterial();
      BlockState var21 = var19;
      int var22 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      boolean var23 = Math.cos(var7 / 3.0D * 3.141592653589793D) > 0.0D;
      int var24 = -1;
      boolean var25 = false;
      int var26 = 0;
      BlockPos.MutableBlockPos var27 = new BlockPos.MutableBlockPos();

      for(int var28 = var6; var28 >= 0; --var28) {
         if (var26 < 15) {
            var27.set(var15, var28, var16);
            BlockState var29 = var2.getBlockState(var27);
            if (var29.isAir()) {
               var24 = -1;
            } else if (var29.is(var9.getBlock())) {
               if (var24 == -1) {
                  var25 = false;
                  if (var22 <= 0) {
                     var17 = Blocks.AIR.defaultBlockState();
                     var21 = var9;
                  } else if (var28 >= var11 - 4 && var28 <= var11 + 1) {
                     var17 = WHITE_TERRACOTTA;
                     var21 = var19;
                  }

                  if (var28 < var11 && (var17 == null || var17.isAir())) {
                     var17 = var10;
                  }

                  var24 = var22 + Math.max(0, var28 - var11);
                  if (var28 >= var11 - 1) {
                     if (var28 > 86 + var22 * 2) {
                        if (var23) {
                           var2.setBlockState(var27, Blocks.COARSE_DIRT.defaultBlockState(), false);
                        } else {
                           var2.setBlockState(var27, Blocks.GRASS_BLOCK.defaultBlockState(), false);
                        }
                     } else if (var28 > var11 + 3 + var22) {
                        BlockState var30;
                        if (var28 >= 64 && var28 <= 127) {
                           if (var23) {
                              var30 = TERRACOTTA;
                           } else {
                              var30 = this.getBand(var4, var28, var5);
                           }
                        } else {
                           var30 = ORANGE_TERRACOTTA;
                        }

                        var2.setBlockState(var27, var30, false);
                     } else {
                        var2.setBlockState(var27, var20, false);
                        var25 = true;
                     }
                  } else {
                     var2.setBlockState(var27, var21, false);
                     if (var21 == WHITE_TERRACOTTA) {
                        var2.setBlockState(var27, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (var24 > 0) {
                  --var24;
                  if (var25) {
                     var2.setBlockState(var27, ORANGE_TERRACOTTA, false);
                  } else {
                     var2.setBlockState(var27, this.getBand(var4, var28, var5), false);
                  }
               }

               ++var26;
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
