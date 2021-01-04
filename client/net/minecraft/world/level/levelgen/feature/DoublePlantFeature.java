package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class DoublePlantFeature extends Feature<DoublePlantConfiguration> {
   public DoublePlantFeature(Function<Dynamic<?>, ? extends DoublePlantConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, DoublePlantConfiguration var5) {
      boolean var6 = false;

      for(int var7 = 0; var7 < 64; ++var7) {
         BlockPos var8 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.isEmptyBlock(var8) && var8.getY() < 254 && var5.state.canSurvive(var1, var8)) {
            ((DoublePlantBlock)var5.state.getBlock()).placeAt(var1, var8, 2);
            var6 = true;
         }
      }

      return var6;
   }
}
