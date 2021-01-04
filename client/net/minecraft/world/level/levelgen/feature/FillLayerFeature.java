package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class FillLayerFeature extends Feature<LayerConfiguration> {
   public FillLayerFeature(Function<Dynamic<?>, ? extends LayerConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, LayerConfiguration var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            int var9 = var4.getX() + var7;
            int var10 = var4.getZ() + var8;
            int var11 = var5.height;
            var6.set(var9, var11, var10);
            if (var1.getBlockState(var6).isAir()) {
               var1.setBlock(var6, var5.state, 2);
            }
         }
      }

      return true;
   }
}
