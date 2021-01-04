package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class EndIslandFeature extends Feature<NoneFeatureConfiguration> {
   public EndIslandFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      float var6 = (float)(var3.nextInt(3) + 4);

      for(int var7 = 0; var6 > 0.5F; --var7) {
         for(int var8 = Mth.floor(-var6); var8 <= Mth.ceil(var6); ++var8) {
            for(int var9 = Mth.floor(-var6); var9 <= Mth.ceil(var6); ++var9) {
               if ((float)(var8 * var8 + var9 * var9) <= (var6 + 1.0F) * (var6 + 1.0F)) {
                  this.setBlock(var1, var4.offset(var8, var7, var9), Blocks.END_STONE.defaultBlockState());
               }
            }
         }

         var6 = (float)((double)var6 - ((double)var3.nextInt(2) + 0.5D));
      }

      return true;
   }
}
