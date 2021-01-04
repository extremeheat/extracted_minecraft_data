package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.material.Material;

public class LakeFeature extends Feature<LakeConfiguration> {
   private static final BlockState AIR;

   public LakeFeature(Function<Dynamic<?>, ? extends LakeConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, LakeConfiguration var5) {
      while(var4.getY() > 5 && var1.isEmptyBlock(var4)) {
         var4 = var4.below();
      }

      if (var4.getY() <= 4) {
         return false;
      } else {
         var4 = var4.below(4);
         ChunkPos var6 = new ChunkPos(var4);
         if (!var1.getChunk(var6.x, var6.z, ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(Feature.VILLAGE.getFeatureName()).isEmpty()) {
            return false;
         } else {
            boolean[] var7 = new boolean[2048];
            int var8 = var3.nextInt(4) + 4;

            int var9;
            for(var9 = 0; var9 < var8; ++var9) {
               double var10 = var3.nextDouble() * 6.0D + 3.0D;
               double var12 = var3.nextDouble() * 4.0D + 2.0D;
               double var14 = var3.nextDouble() * 6.0D + 3.0D;
               double var16 = var3.nextDouble() * (16.0D - var10 - 2.0D) + 1.0D + var10 / 2.0D;
               double var18 = var3.nextDouble() * (8.0D - var12 - 4.0D) + 2.0D + var12 / 2.0D;
               double var20 = var3.nextDouble() * (16.0D - var14 - 2.0D) + 1.0D + var14 / 2.0D;

               for(int var22 = 1; var22 < 15; ++var22) {
                  for(int var23 = 1; var23 < 15; ++var23) {
                     for(int var24 = 1; var24 < 7; ++var24) {
                        double var25 = ((double)var22 - var16) / (var10 / 2.0D);
                        double var27 = ((double)var24 - var18) / (var12 / 2.0D);
                        double var29 = ((double)var23 - var20) / (var14 / 2.0D);
                        double var31 = var25 * var25 + var27 * var27 + var29 * var29;
                        if (var31 < 1.0D) {
                           var7[(var22 * 16 + var23) * 8 + var24] = true;
                        }
                     }
                  }
               }
            }

            int var11;
            int var33;
            boolean var34;
            for(var9 = 0; var9 < 16; ++var9) {
               for(var33 = 0; var33 < 16; ++var33) {
                  for(var11 = 0; var11 < 8; ++var11) {
                     var34 = !var7[(var9 * 16 + var33) * 8 + var11] && (var9 < 15 && var7[((var9 + 1) * 16 + var33) * 8 + var11] || var9 > 0 && var7[((var9 - 1) * 16 + var33) * 8 + var11] || var33 < 15 && var7[(var9 * 16 + var33 + 1) * 8 + var11] || var33 > 0 && var7[(var9 * 16 + (var33 - 1)) * 8 + var11] || var11 < 7 && var7[(var9 * 16 + var33) * 8 + var11 + 1] || var11 > 0 && var7[(var9 * 16 + var33) * 8 + (var11 - 1)]);
                     if (var34) {
                        Material var13 = var1.getBlockState(var4.offset(var9, var11, var33)).getMaterial();
                        if (var11 >= 4 && var13.isLiquid()) {
                           return false;
                        }

                        if (var11 < 4 && !var13.isSolid() && var1.getBlockState(var4.offset(var9, var11, var33)) != var5.state) {
                           return false;
                        }
                     }
                  }
               }
            }

            for(var9 = 0; var9 < 16; ++var9) {
               for(var33 = 0; var33 < 16; ++var33) {
                  for(var11 = 0; var11 < 8; ++var11) {
                     if (var7[(var9 * 16 + var33) * 8 + var11]) {
                        var1.setBlock(var4.offset(var9, var11, var33), var11 >= 4 ? AIR : var5.state, 2);
                     }
                  }
               }
            }

            BlockPos var35;
            for(var9 = 0; var9 < 16; ++var9) {
               for(var33 = 0; var33 < 16; ++var33) {
                  for(var11 = 4; var11 < 8; ++var11) {
                     if (var7[(var9 * 16 + var33) * 8 + var11]) {
                        var35 = var4.offset(var9, var11 - 1, var33);
                        if (Block.equalsDirt(var1.getBlockState(var35).getBlock()) && var1.getBrightness(LightLayer.SKY, var4.offset(var9, var11, var33)) > 0) {
                           Biome var36 = var1.getBiome(var35);
                           if (var36.getSurfaceBuilderConfig().getTopMaterial().getBlock() == Blocks.MYCELIUM) {
                              var1.setBlock(var35, Blocks.MYCELIUM.defaultBlockState(), 2);
                           } else {
                              var1.setBlock(var35, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                           }
                        }
                     }
                  }
               }
            }

            if (var5.state.getMaterial() == Material.LAVA) {
               for(var9 = 0; var9 < 16; ++var9) {
                  for(var33 = 0; var33 < 16; ++var33) {
                     for(var11 = 0; var11 < 8; ++var11) {
                        var34 = !var7[(var9 * 16 + var33) * 8 + var11] && (var9 < 15 && var7[((var9 + 1) * 16 + var33) * 8 + var11] || var9 > 0 && var7[((var9 - 1) * 16 + var33) * 8 + var11] || var33 < 15 && var7[(var9 * 16 + var33 + 1) * 8 + var11] || var33 > 0 && var7[(var9 * 16 + (var33 - 1)) * 8 + var11] || var11 < 7 && var7[(var9 * 16 + var33) * 8 + var11 + 1] || var11 > 0 && var7[(var9 * 16 + var33) * 8 + (var11 - 1)]);
                        if (var34 && (var11 < 4 || var3.nextInt(2) != 0) && var1.getBlockState(var4.offset(var9, var11, var33)).getMaterial().isSolid()) {
                           var1.setBlock(var4.offset(var9, var11, var33), Blocks.STONE.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            if (var5.state.getMaterial() == Material.WATER) {
               for(var9 = 0; var9 < 16; ++var9) {
                  for(var33 = 0; var33 < 16; ++var33) {
                     boolean var37 = true;
                     var35 = var4.offset(var9, 4, var33);
                     if (var1.getBiome(var35).shouldFreeze(var1, var35, false)) {
                        var1.setBlock(var35, Blocks.ICE.defaultBlockState(), 2);
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   static {
      AIR = Blocks.CAVE_AIR.defaultBlockState();
   }
}
