package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndPlatformFeature extends Feature<NoneFeatureConfiguration> {
   public EndPlatformFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      createEndPlatform(var1.level(), var1.origin(), false);
      return true;
   }

   public static void createEndPlatform(ServerLevelAccessor var0, BlockPos var1, boolean var2) {
      BlockPos.MutableBlockPos var3 = var1.mutable();

      for(int var4 = -2; var4 <= 2; ++var4) {
         for(int var5 = -2; var5 <= 2; ++var5) {
            for(int var6 = -1; var6 < 3; ++var6) {
               BlockPos.MutableBlockPos var7 = var3.set(var1).move(var5, var6, var4);
               Block var8 = var6 == -1 ? Blocks.OBSIDIAN : Blocks.AIR;
               if (!var0.getBlockState(var7).is(var8)) {
                  if (var2) {
                     var0.destroyBlock(var7, true, (Entity)null);
                  }

                  var0.setBlock(var7, var8.defaultBlockState(), 3);
               }
            }
         }
      }

   }
}
