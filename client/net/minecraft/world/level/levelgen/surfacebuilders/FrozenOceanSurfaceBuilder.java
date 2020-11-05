package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Material;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   protected static final BlockState PACKED_ICE;
   protected static final BlockState SNOW_BLOCK;
   private static final BlockState AIR;
   private static final BlockState GRAVEL;
   private static final BlockState ICE;
   private PerlinSimplexNoise icebergNoise;
   private PerlinSimplexNoise icebergRoofNoise;
   private long seed;

   public FrozenOceanSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      double var15 = 0.0D;
      double var17 = 0.0D;
      BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();
      float var20 = var3.getTemperature(var19.set(var4, 63, var5));
      double var21 = Math.min(Math.abs(var7), this.icebergNoise.getValue((double)var4 * 0.1D, (double)var5 * 0.1D, false) * 15.0D);
      if (var21 > 1.8D) {
         double var23 = 0.09765625D;
         double var25 = Math.abs(this.icebergRoofNoise.getValue((double)var4 * 0.09765625D, (double)var5 * 0.09765625D, false));
         var15 = var21 * var21 * 1.2D;
         double var27 = Math.ceil(var25 * 40.0D) + 14.0D;
         if (var15 > var27) {
            var15 = var27;
         }

         if (var20 > 0.1F) {
            var15 -= 2.0D;
         }

         if (var15 > 2.0D) {
            var17 = (double)var11 - var15 - 7.0D;
            var15 += (double)var11;
         } else {
            var15 = 0.0D;
         }
      }

      int var37 = var4 & 15;
      int var24 = var5 & 15;
      SurfaceBuilderConfiguration var38 = var3.getGenerationSettings().getSurfaceBuilderConfig();
      BlockState var26 = var38.getUnderMaterial();
      BlockState var39 = var38.getTopMaterial();
      BlockState var28 = var26;
      BlockState var29 = var39;
      int var30 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      int var31 = -1;
      int var32 = 0;
      int var33 = 2 + var1.nextInt(4);
      int var34 = var11 + 18 + var1.nextInt(10);

      for(int var35 = Math.max(var6, (int)var15 + 1); var35 >= 0; --var35) {
         var19.set(var37, var35, var24);
         if (var2.getBlockState(var19).isAir() && var35 < (int)var15 && var1.nextDouble() > 0.01D) {
            var2.setBlockState(var19, PACKED_ICE, false);
         } else if (var2.getBlockState(var19).getMaterial() == Material.WATER && var35 > (int)var17 && var35 < var11 && var17 != 0.0D && var1.nextDouble() > 0.15D) {
            var2.setBlockState(var19, PACKED_ICE, false);
         }

         BlockState var36 = var2.getBlockState(var19);
         if (var36.isAir()) {
            var31 = -1;
         } else if (!var36.is(var9.getBlock())) {
            if (var36.is(Blocks.PACKED_ICE) && var32 <= var33 && var35 > var34) {
               var2.setBlockState(var19, SNOW_BLOCK, false);
               ++var32;
            }
         } else if (var31 == -1) {
            if (var30 <= 0) {
               var29 = AIR;
               var28 = var9;
            } else if (var35 >= var11 - 4 && var35 <= var11 + 1) {
               var29 = var39;
               var28 = var26;
            }

            if (var35 < var11 && (var29 == null || var29.isAir())) {
               if (var3.getTemperature(var19.set(var4, var35, var5)) < 0.15F) {
                  var29 = ICE;
               } else {
                  var29 = var10;
               }
            }

            var31 = var30;
            if (var35 >= var11 - 1) {
               var2.setBlockState(var19, var29, false);
            } else if (var35 < var11 - 7 - var30) {
               var29 = AIR;
               var28 = var9;
               var2.setBlockState(var19, GRAVEL, false);
            } else {
               var2.setBlockState(var19, var28, false);
            }
         } else if (var31 > 0) {
            --var31;
            var2.setBlockState(var19, var28, false);
            if (var31 == 0 && var28.is(Blocks.SAND) && var30 > 1) {
               var31 = var1.nextInt(4) + Math.max(0, var35 - 63);
               var28 = var28.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
            }
         }
      }

   }

   public void initNoise(long var1) {
      if (this.seed != var1 || this.icebergNoise == null || this.icebergRoofNoise == null) {
         WorldgenRandom var3 = new WorldgenRandom(var1);
         this.icebergNoise = new PerlinSimplexNoise(var3, IntStream.rangeClosed(-3, 0));
         this.icebergRoofNoise = new PerlinSimplexNoise(var3, ImmutableList.of(0));
      }

      this.seed = var1;
   }

   static {
      PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
      SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
      AIR = Blocks.AIR.defaultBlockState();
      GRAVEL = Blocks.GRAVEL.defaultBlockState();
      ICE = Blocks.ICE.defaultBlockState();
   }
}
