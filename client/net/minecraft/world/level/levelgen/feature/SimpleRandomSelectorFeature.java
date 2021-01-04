package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class SimpleRandomSelectorFeature extends Feature<SimpleRandomFeatureConfig> {
   public SimpleRandomSelectorFeature(Function<Dynamic<?>, ? extends SimpleRandomFeatureConfig> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, SimpleRandomFeatureConfig var5) {
      int var6 = var3.nextInt(var5.features.size());
      ConfiguredFeature var7 = (ConfiguredFeature)var5.features.get(var6);
      return var7.place(var1, var2, var3, var4);
   }
}
