package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class CheckerboardBiomeSource extends BiomeSource {
   private final Biome[] allowedBiomes;
   private final int bitShift;

   public CheckerboardBiomeSource(CheckerboardBiomeSourceSettings var1) {
      super();
      this.allowedBiomes = var1.getAllowedBiomes();
      this.bitShift = var1.getSize() + 4;
   }

   public Biome getBiome(int var1, int var2) {
      return this.allowedBiomes[Math.abs(((var1 >> this.bitShift) + (var2 >> this.bitShift)) % this.allowedBiomes.length)];
   }

   public Biome[] getBiomeBlock(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] var6 = new Biome[var3 * var4];

      for(int var7 = 0; var7 < var4; ++var7) {
         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = Math.abs(((var1 + var7 >> this.bitShift) + (var2 + var8 >> this.bitShift)) % this.allowedBiomes.length);
            Biome var10 = this.allowedBiomes[var9];
            var6[var7 * var3 + var8] = var10;
         }
      }

      return var6;
   }

   @Nullable
   public BlockPos findBiome(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      return null;
   }

   public boolean canGenerateStructure(StructureFeature<?> var1) {
      return (Boolean)this.supportedStructures.computeIfAbsent(var1, (var1x) -> {
         Biome[] var2 = this.allowedBiomes;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Biome var5 = var2[var4];
            if (var5.isValidStart(var1x)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<BlockState> getSurfaceBlocks() {
      if (this.surfaceBlocks.isEmpty()) {
         Biome[] var1 = this.allowedBiomes;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Biome var4 = var1[var3];
            this.surfaceBlocks.add(var4.getSurfaceBuilderConfig().getTopMaterial());
         }
      }

      return this.surfaceBlocks;
   }

   public Set<Biome> getBiomesWithin(int var1, int var2, int var3) {
      return Sets.newHashSet(this.allowedBiomes);
   }
}
