package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SnowAndFreezeFeature extends Feature<NoneFeatureConfiguration> {
   public SnowAndFreezeFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(int var6 = 0; var6 < 16; ++var6) {
         for(int var7 = 0; var7 < 16; ++var7) {
            int var8 = var3.getX() + var6;
            int var9 = var3.getZ() + var7;
            int var10 = var2.getHeight(Heightmap.Types.MOTION_BLOCKING, var8, var9);
            var4.set(var8, var10, var9);
            var5.set(var4).move(Direction.DOWN, 1);
            Biome var11 = (Biome)var2.getBiome(var4).value();
            if (var11.shouldFreeze(var2, var5, false)) {
               var2.setBlock(var5, Blocks.ICE.defaultBlockState(), 2);
            }

            if (var11.shouldSnow(var2, var4)) {
               var2.setBlock(var4, Blocks.SNOW.defaultBlockState(), 2);
               BlockState var12 = var2.getBlockState(var5);
               if (var12.hasProperty(SnowyDirtBlock.SNOWY)) {
                  var2.setBlock(var5, (BlockState)var12.setValue(SnowyDirtBlock.SNOWY, true), 2);
               }
            }
         }
      }

      return true;
   }
}
