package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;

public class BlockColumnFeature extends Feature<BlockColumnConfiguration> {
   public BlockColumnFeature(Codec<BlockColumnConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<BlockColumnConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockColumnConfiguration var3 = (BlockColumnConfiguration)var1.config();
      RandomSource var4 = var1.random();
      int var5 = var3.layers().size();
      int[] var6 = new int[var5];
      int var7 = 0;

      for(int var8 = 0; var8 < var5; ++var8) {
         var6[var8] = ((BlockColumnConfiguration.Layer)var3.layers().get(var8)).height().sample(var4);
         var7 += var6[var8];
      }

      if (var7 == 0) {
         return false;
      } else {
         BlockPos.MutableBlockPos var14 = var1.origin().mutable();
         BlockPos.MutableBlockPos var9 = var14.mutable().move(var3.direction());

         int var10;
         for(var10 = 0; var10 < var7; ++var10) {
            if (!var3.allowedPlacement().test(var2, var9)) {
               truncate(var6, var7, var10, var3.prioritizeTip());
               break;
            }

            var9.move(var3.direction());
         }

         for(var10 = 0; var10 < var5; ++var10) {
            int var11 = var6[var10];
            if (var11 != 0) {
               BlockColumnConfiguration.Layer var12 = (BlockColumnConfiguration.Layer)var3.layers().get(var10);

               for(int var13 = 0; var13 < var11; ++var13) {
                  var2.setBlock(var14, var12.state().getState(var4, var14), 2);
                  var14.move(var3.direction());
               }
            }
         }

         return true;
      }
   }

   private static void truncate(int[] var0, int var1, int var2, boolean var3) {
      int var4 = var1 - var2;
      int var5 = var3 ? 1 : -1;
      int var6 = var3 ? 0 : var0.length - 1;
      int var7 = var3 ? var0.length : -1;

      for(int var8 = var6; var8 != var7 && var4 > 0; var8 += var5) {
         int var9 = var0[var8];
         int var10 = Math.min(var9, var4);
         var4 -= var10;
         var0[var8] -= var10;
      }

   }
}
