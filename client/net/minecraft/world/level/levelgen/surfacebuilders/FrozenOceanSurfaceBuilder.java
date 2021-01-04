package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
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

   public FrozenOceanSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      double var15 = 0.0D;
      double var17 = 0.0D;
      BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();
      float var20 = var3.getTemperature(var19.set(var4, 63, var5));
      double var21 = Math.min(Math.abs(var7), this.icebergNoise.getValue((double)var4 * 0.1D, (double)var5 * 0.1D));
      if (var21 > 1.8D) {
         double var23 = 0.09765625D;
         double var25 = Math.abs(this.icebergRoofNoise.getValue((double)var4 * 0.09765625D, (double)var5 * 0.09765625D));
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

      int var34 = var4 & 15;
      int var24 = var5 & 15;
      BlockState var35 = var3.getSurfaceBuilderConfig().getUnderMaterial();
      BlockState var26 = var3.getSurfaceBuilderConfig().getTopMaterial();
      int var36 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      int var28 = -1;
      int var29 = 0;
      int var30 = 2 + var1.nextInt(4);
      int var31 = var11 + 18 + var1.nextInt(10);

      for(int var32 = Math.max(var6, (int)var15 + 1); var32 >= 0; --var32) {
         var19.set(var34, var32, var24);
         if (var2.getBlockState(var19).isAir() && var32 < (int)var15 && var1.nextDouble() > 0.01D) {
            var2.setBlockState(var19, PACKED_ICE, false);
         } else if (var2.getBlockState(var19).getMaterial() == Material.WATER && var32 > (int)var17 && var32 < var11 && var17 != 0.0D && var1.nextDouble() > 0.15D) {
            var2.setBlockState(var19, PACKED_ICE, false);
         }

         BlockState var33 = var2.getBlockState(var19);
         if (var33.isAir()) {
            var28 = -1;
         } else if (var33.getBlock() != var9.getBlock()) {
            if (var33.getBlock() == Blocks.PACKED_ICE && var29 <= var30 && var32 > var31) {
               var2.setBlockState(var19, SNOW_BLOCK, false);
               ++var29;
            }
         } else if (var28 == -1) {
            if (var36 <= 0) {
               var26 = AIR;
               var35 = var9;
            } else if (var32 >= var11 - 4 && var32 <= var11 + 1) {
               var26 = var3.getSurfaceBuilderConfig().getTopMaterial();
               var35 = var3.getSurfaceBuilderConfig().getUnderMaterial();
            }

            if (var32 < var11 && (var26 == null || var26.isAir())) {
               if (var3.getTemperature(var19.set(var4, var32, var5)) < 0.15F) {
                  var26 = ICE;
               } else {
                  var26 = var10;
               }
            }

            var28 = var36;
            if (var32 >= var11 - 1) {
               var2.setBlockState(var19, var26, false);
            } else if (var32 < var11 - 7 - var36) {
               var26 = AIR;
               var35 = var9;
               var2.setBlockState(var19, GRAVEL, false);
            } else {
               var2.setBlockState(var19, var35, false);
            }
         } else if (var28 > 0) {
            --var28;
            var2.setBlockState(var19, var35, false);
            if (var28 == 0 && var35.getBlock() == Blocks.SAND && var36 > 1) {
               var28 = var1.nextInt(4) + Math.max(0, var32 - 63);
               var35 = var35.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
            }
         }
      }

   }

   public void initNoise(long var1) {
      if (this.seed != var1 || this.icebergNoise == null || this.icebergRoofNoise == null) {
         WorldgenRandom var3 = new WorldgenRandom(var1);
         this.icebergNoise = new PerlinSimplexNoise(var3, 4);
         this.icebergRoofNoise = new PerlinSimplexNoise(var3, 1);
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
