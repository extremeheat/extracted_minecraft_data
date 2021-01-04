package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class FixedBiomeSource extends BiomeSource {
   private final Biome biome;

   public FixedBiomeSource(FixedBiomeSourceSettings var1) {
      super();
      this.biome = var1.getBiome();
   }

   public Biome getBiome(int var1, int var2) {
      return this.biome;
   }

   public Biome[] getBiomeBlock(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] var6 = new Biome[var3 * var4];
      Arrays.fill(var6, 0, var3 * var4, this.biome);
      return var6;
   }

   @Nullable
   public BlockPos findBiome(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      return var4.contains(this.biome) ? new BlockPos(var1 - var3 + var5.nextInt(var3 * 2 + 1), 0, var2 - var3 + var5.nextInt(var3 * 2 + 1)) : null;
   }

   public boolean canGenerateStructure(StructureFeature<?> var1) {
      Map var10000 = this.supportedStructures;
      Biome var10002 = this.biome;
      var10002.getClass();
      return (Boolean)var10000.computeIfAbsent(var1, var10002::isValidStart);
   }

   public Set<BlockState> getSurfaceBlocks() {
      if (this.surfaceBlocks.isEmpty()) {
         this.surfaceBlocks.add(this.biome.getSurfaceBuilderConfig().getTopMaterial());
      }

      return this.surfaceBlocks;
   }

   public Set<Biome> getBiomesWithin(int var1, int var2, int var3) {
      return Sets.newHashSet(new Biome[]{this.biome});
   }
}
