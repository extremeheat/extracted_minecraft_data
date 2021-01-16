package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SnowAndFreezeFeature extends Feature<NoneFeatureConfiguration> {
   public SnowAndFreezeFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            int var10 = var4.getX() + var8;
            int var11 = var4.getZ() + var9;
            int var12 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var10, var11);
            var6.set(var10, var12, var11);
            var7.set(var6).move(Direction.DOWN, 1);
            Biome var13 = var1.getBiome(var6);
            if (var13.shouldFreeze(var1, var7, false)) {
               var1.setBlock(var7, Blocks.ICE.defaultBlockState(), 2);
            }

            if (var13.shouldSnow(var1, var6)) {
               var1.setBlock(var6, Blocks.SNOW.defaultBlockState(), 2);
               BlockState var14 = var1.getBlockState(var7);
               if (var14.hasProperty(SnowyDirtBlock.SNOWY)) {
                  var1.setBlock(var7, (BlockState)var14.setValue(SnowyDirtBlock.SNOWY, true), 2);
               }
            }
         }
      }

      return true;
   }
}
