package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Material;

public class LakeFeature extends Feature<BlockStateConfiguration> {
   private static final BlockState AIR;

   public LakeFeature(Codec<BlockStateConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, BlockStateConfiguration var5) {
      while(var4.getY() > var1.getMinBuildHeight() + 5 && var1.isEmptyBlock(var4)) {
         var4 = var4.below();
      }

      if (var4.getY() <= var1.getMinBuildHeight() + 4) {
         return false;
      } else {
         var4 = var4.below(4);
         if (var1.startsForFeature(SectionPos.of(var4), StructureFeature.VILLAGE).findAny().isPresent()) {
            return false;
         } else {
            boolean[] var6 = new boolean[2048];
            int var7 = var3.nextInt(4) + 4;

            int var8;
            for(var8 = 0; var8 < var7; ++var8) {
               double var9 = var3.nextDouble() * 6.0D + 3.0D;
               double var11 = var3.nextDouble() * 4.0D + 2.0D;
               double var13 = var3.nextDouble() * 6.0D + 3.0D;
               double var15 = var3.nextDouble() * (16.0D - var9 - 2.0D) + 1.0D + var9 / 2.0D;
               double var17 = var3.nextDouble() * (8.0D - var11 - 4.0D) + 2.0D + var11 / 2.0D;
               double var19 = var3.nextDouble() * (16.0D - var13 - 2.0D) + 1.0D + var13 / 2.0D;

               for(int var21 = 1; var21 < 15; ++var21) {
                  for(int var22 = 1; var22 < 15; ++var22) {
                     for(int var23 = 1; var23 < 7; ++var23) {
                        double var24 = ((double)var21 - var15) / (var9 / 2.0D);
                        double var26 = ((double)var23 - var17) / (var11 / 2.0D);
                        double var28 = ((double)var22 - var19) / (var13 / 2.0D);
                        double var30 = var24 * var24 + var26 * var26 + var28 * var28;
                        if (var30 < 1.0D) {
                           var6[(var21 * 16 + var22) * 8 + var23] = true;
                        }
                     }
                  }
               }
            }

            int var10;
            int var32;
            boolean var33;
            for(var8 = 0; var8 < 16; ++var8) {
               for(var32 = 0; var32 < 16; ++var32) {
                  for(var10 = 0; var10 < 8; ++var10) {
                     var33 = !var6[(var8 * 16 + var32) * 8 + var10] && (var8 < 15 && var6[((var8 + 1) * 16 + var32) * 8 + var10] || var8 > 0 && var6[((var8 - 1) * 16 + var32) * 8 + var10] || var32 < 15 && var6[(var8 * 16 + var32 + 1) * 8 + var10] || var32 > 0 && var6[(var8 * 16 + (var32 - 1)) * 8 + var10] || var10 < 7 && var6[(var8 * 16 + var32) * 8 + var10 + 1] || var10 > 0 && var6[(var8 * 16 + var32) * 8 + (var10 - 1)]);
                     if (var33) {
                        Material var12 = var1.getBlockState(var4.offset(var8, var10, var32)).getMaterial();
                        if (var10 >= 4 && var12.isLiquid()) {
                           return false;
                        }

                        if (var10 < 4 && !var12.isSolid() && var1.getBlockState(var4.offset(var8, var10, var32)) != var5.state) {
                           return false;
                        }
                     }
                  }
               }
            }

            for(var8 = 0; var8 < 16; ++var8) {
               for(var32 = 0; var32 < 16; ++var32) {
                  for(var10 = 0; var10 < 8; ++var10) {
                     if (var6[(var8 * 16 + var32) * 8 + var10]) {
                        var1.setBlock(var4.offset(var8, var10, var32), var10 >= 4 ? AIR : var5.state, 2);
                     }
                  }
               }
            }

            BlockPos var34;
            for(var8 = 0; var8 < 16; ++var8) {
               for(var32 = 0; var32 < 16; ++var32) {
                  for(var10 = 4; var10 < 8; ++var10) {
                     if (var6[(var8 * 16 + var32) * 8 + var10]) {
                        var34 = var4.offset(var8, var10 - 1, var32);
                        if (isDirt(var1.getBlockState(var34)) && var1.getBrightness(LightLayer.SKY, var4.offset(var8, var10, var32)) > 0) {
                           Biome var35 = var1.getBiome(var34);
                           if (var35.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial().is(Blocks.MYCELIUM)) {
                              var1.setBlock(var34, Blocks.MYCELIUM.defaultBlockState(), 2);
                           } else {
                              var1.setBlock(var34, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                           }
                        }
                     }
                  }
               }
            }

            if (var5.state.getMaterial() == Material.LAVA) {
               for(var8 = 0; var8 < 16; ++var8) {
                  for(var32 = 0; var32 < 16; ++var32) {
                     for(var10 = 0; var10 < 8; ++var10) {
                        var33 = !var6[(var8 * 16 + var32) * 8 + var10] && (var8 < 15 && var6[((var8 + 1) * 16 + var32) * 8 + var10] || var8 > 0 && var6[((var8 - 1) * 16 + var32) * 8 + var10] || var32 < 15 && var6[(var8 * 16 + var32 + 1) * 8 + var10] || var32 > 0 && var6[(var8 * 16 + (var32 - 1)) * 8 + var10] || var10 < 7 && var6[(var8 * 16 + var32) * 8 + var10 + 1] || var10 > 0 && var6[(var8 * 16 + var32) * 8 + (var10 - 1)]);
                        if (var33 && (var10 < 4 || var3.nextInt(2) != 0) && var1.getBlockState(var4.offset(var8, var10, var32)).getMaterial().isSolid()) {
                           var1.setBlock(var4.offset(var8, var10, var32), Blocks.STONE.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            if (var5.state.getMaterial() == Material.WATER) {
               for(var8 = 0; var8 < 16; ++var8) {
                  for(var32 = 0; var32 < 16; ++var32) {
                     boolean var36 = true;
                     var34 = var4.offset(var8, 4, var32);
                     if (var1.getBiome(var34).shouldFreeze(var1, var34, false)) {
                        var1.setBlock(var34, Blocks.ICE.defaultBlockState(), 2);
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
