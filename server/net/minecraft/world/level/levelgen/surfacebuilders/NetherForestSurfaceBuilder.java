package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NetherForestSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   private static final BlockState AIR;
   protected long seed;
   private PerlinNoise decorationNoise;

   public NetherForestSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      int var15 = var11;
      int var16 = var4 & 15;
      int var17 = var5 & 15;
      double var18 = this.decorationNoise.getValue((double)var4 * 0.1D, (double)var11, (double)var5 * 0.1D);
      boolean var20 = var18 > 0.15D + var1.nextDouble() * 0.35D;
      double var21 = this.decorationNoise.getValue((double)var4 * 0.1D, 109.0D, (double)var5 * 0.1D);
      boolean var23 = var21 > 0.25D + var1.nextDouble() * 0.9D;
      int var24 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos var25 = new BlockPos.MutableBlockPos();
      int var26 = -1;
      BlockState var27 = var14.getUnderMaterial();

      for(int var28 = 127; var28 >= 0; --var28) {
         var25.set(var16, var28, var17);
         BlockState var29 = var14.getTopMaterial();
         BlockState var30 = var2.getBlockState(var25);
         if (var30.isAir()) {
            var26 = -1;
         } else if (var30.is(var9.getBlock())) {
            if (var26 == -1) {
               boolean var31 = false;
               if (var24 <= 0) {
                  var31 = true;
                  var27 = var14.getUnderMaterial();
               }

               if (var20) {
                  var29 = var14.getUnderMaterial();
               } else if (var23) {
                  var29 = var14.getUnderwaterMaterial();
               }

               if (var28 < var15 && var31) {
                  var29 = var10;
               }

               var26 = var24;
               if (var28 >= var15 - 1) {
                  var2.setBlockState(var25, var29, false);
               } else {
                  var2.setBlockState(var25, var27, false);
               }
            } else if (var26 > 0) {
               --var26;
               var2.setBlockState(var25, var27, false);
            }
         }
      }

   }

   public void initNoise(long var1) {
      if (this.seed != var1 || this.decorationNoise == null) {
         this.decorationNoise = new PerlinNoise(new WorldgenRandom(var1), ImmutableList.of(0));
      }

      this.seed = var1;
   }

   static {
      AIR = Blocks.CAVE_AIR.defaultBlockState();
   }
}
