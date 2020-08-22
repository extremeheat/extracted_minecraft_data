package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NetherSurfaceBuilder extends SurfaceBuilder {
   private static final BlockState AIR;
   private static final BlockState NETHERRACK;
   private static final BlockState GRAVEL;
   private static final BlockState SOUL_SAND;
   protected long seed;
   protected PerlinNoise decorationNoise;

   public NetherSurfaceBuilder(Function var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      int var15 = var11 + 1;
      int var16 = var4 & 15;
      int var17 = var5 & 15;
      double var18 = 0.03125D;
      boolean var20 = this.decorationNoise.getValue((double)var4 * 0.03125D, (double)var5 * 0.03125D, 0.0D) * 75.0D + var1.nextDouble() > 0.0D;
      boolean var21 = this.decorationNoise.getValue((double)var4 * 0.03125D, 109.0D, (double)var5 * 0.03125D) * 75.0D + var1.nextDouble() > 0.0D;
      int var22 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      int var24 = -1;
      BlockState var25 = NETHERRACK;
      BlockState var26 = NETHERRACK;

      for(int var27 = 127; var27 >= 0; --var27) {
         var23.set(var16, var27, var17);
         BlockState var28 = var2.getBlockState(var23);
         if (var28.getBlock() != null && !var28.isAir()) {
            if (var28.getBlock() == var9.getBlock()) {
               if (var24 == -1) {
                  if (var22 <= 0) {
                     var25 = AIR;
                     var26 = NETHERRACK;
                  } else if (var27 >= var15 - 4 && var27 <= var15 + 1) {
                     var25 = NETHERRACK;
                     var26 = NETHERRACK;
                     if (var21) {
                        var25 = GRAVEL;
                        var26 = NETHERRACK;
                     }

                     if (var20) {
                        var25 = SOUL_SAND;
                        var26 = SOUL_SAND;
                     }
                  }

                  if (var27 < var15 && (var25 == null || var25.isAir())) {
                     var25 = var10;
                  }

                  var24 = var22;
                  if (var27 >= var15 - 1) {
                     var2.setBlockState(var23, var25, false);
                  } else {
                     var2.setBlockState(var23, var26, false);
                  }
               } else if (var24 > 0) {
                  --var24;
                  var2.setBlockState(var23, var26, false);
               }
            }
         } else {
            var24 = -1;
         }
      }

   }

   public void initNoise(long var1) {
      if (this.seed != var1 || this.decorationNoise == null) {
         this.decorationNoise = new PerlinNoise(new WorldgenRandom(var1), 3, 0);
      }

      this.seed = var1;
   }

   static {
      AIR = Blocks.CAVE_AIR.defaultBlockState();
      NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
      GRAVEL = Blocks.GRAVEL.defaultBlockState();
      SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
   }
}
