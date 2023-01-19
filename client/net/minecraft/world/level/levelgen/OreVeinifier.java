package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class OreVeinifier {
   private static final float VEININESS_THRESHOLD = 0.4F;
   private static final int EDGE_ROUNDOFF_BEGIN = 20;
   private static final double MAX_EDGE_ROUNDOFF = 0.2;
   private static final float VEIN_SOLIDNESS = 0.7F;
   private static final float MIN_RICHNESS = 0.1F;
   private static final float MAX_RICHNESS = 0.3F;
   private static final float MAX_RICHNESS_THRESHOLD = 0.6F;
   private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
   private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;

   private OreVeinifier() {
      super();
   }

   protected static NoiseChunk.BlockStateFiller create(DensityFunction var0, DensityFunction var1, DensityFunction var2, PositionalRandomFactory var3) {
      Object var4 = null;
      return var5 -> {
         double var6 = var0.compute(var5);
         int var8 = var5.blockY();
         OreVeinifier.VeinType var9 = var6 > 0.0 ? OreVeinifier.VeinType.COPPER : OreVeinifier.VeinType.IRON;
         double var10 = Math.abs(var6);
         int var12 = var9.maxY - var8;
         int var13 = var8 - var9.minY;
         if (var13 >= 0 && var12 >= 0) {
            int var14 = Math.min(var12, var13);
            double var15 = Mth.clampedMap((double)var14, 0.0, 20.0, -0.2, 0.0);
            if (var10 + var15 < 0.4000000059604645) {
               return var4;
            } else {
               RandomSource var17 = var3.at(var5.blockX(), var8, var5.blockZ());
               if (var17.nextFloat() > 0.7F) {
                  return var4;
               } else if (var1.compute(var5) >= 0.0) {
                  return var4;
               } else {
                  double var18 = Mth.clampedMap(var10, 0.4000000059604645, 0.6000000238418579, 0.10000000149011612, 0.30000001192092896);
                  if ((double)var17.nextFloat() < var18 && var2.compute(var5) > -0.30000001192092896) {
                     return var17.nextFloat() < 0.02F ? var9.rawOreBlock : var9.ore;
                  } else {
                     return var9.filler;
                  }
               }
            }
         } else {
            return var4;
         }
      };
   }

   protected static enum VeinType {
      COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50),
      IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

      final BlockState ore;
      final BlockState rawOreBlock;
      final BlockState filler;
      protected final int minY;
      protected final int maxY;

      private VeinType(BlockState var3, BlockState var4, BlockState var5, int var6, int var7) {
         this.ore = var3;
         this.rawOreBlock = var4;
         this.filler = var5;
         this.minY = var6;
         this.maxY = var7;
      }
   }
}
