package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public abstract class RandomScatteredFeature<C extends FeatureConfiguration> extends StructureFeature<C> {
   public RandomScatteredFeature(Function<Dynamic<?>, ? extends C> var1) {
      super(var1);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = this.getSpacing(var1);
      int var8 = this.getSeparation(var1);
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)var2).setLargeFeatureWithSalt(var1.getSeed(), var13, var14, this.getRandomSalt());
      var13 *= var7;
      var14 *= var7;
      var13 += var2.nextInt(var7 - var8);
      var14 += var2.nextInt(var7 - var8);
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.x && var4 == var5.z) {
         Biome var6 = var1.getBiomeSource().getBiome(new BlockPos(var3 * 16 + 9, 0, var4 * 16 + 9));
         if (var1.isBiomeValidStartForStructure(var6, this)) {
            return true;
         }
      }

      return false;
   }

   protected int getSpacing(ChunkGenerator<?> var1) {
      return var1.getSettings().getTemplesSpacing();
   }

   protected int getSeparation(ChunkGenerator<?> var1) {
      return var1.getSettings().getTemplesSeparation();
   }

   protected abstract int getRandomSalt();
}
