package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.storage.LevelData;

public class OverworldBiomeSource extends BiomeSource {
   private final Layer noiseBiomeLayer;
   private final Layer blockBiomeLayer;
   private final Biome[] possibleBiomes;

   public OverworldBiomeSource(OverworldBiomeSourceSettings var1) {
      super();
      this.possibleBiomes = new Biome[]{Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU};
      LevelData var2 = var1.getLevelData();
      OverworldGeneratorSettings var3 = var1.getGeneratorSettings();
      Layer[] var4 = Layers.getDefaultLayers(var2.getSeed(), var2.getGeneratorType(), var3);
      this.noiseBiomeLayer = var4[0];
      this.blockBiomeLayer = var4[1];
   }

   public Biome getBiome(int var1, int var2) {
      return this.blockBiomeLayer.get(var1, var2);
   }

   public Biome getNoiseBiome(int var1, int var2) {
      return this.noiseBiomeLayer.get(var1, var2);
   }

   public Biome[] getBiomeBlock(int var1, int var2, int var3, int var4, boolean var5) {
      return this.blockBiomeLayer.getArea(var1, var2, var3, var4);
   }

   public Set<Biome> getBiomesWithin(int var1, int var2, int var3) {
      int var4 = var1 - var3 >> 2;
      int var5 = var2 - var3 >> 2;
      int var6 = var1 + var3 >> 2;
      int var7 = var2 + var3 >> 2;
      int var8 = var6 - var4 + 1;
      int var9 = var7 - var5 + 1;
      HashSet var10 = Sets.newHashSet();
      Collections.addAll(var10, this.noiseBiomeLayer.getArea(var4, var5, var8, var9));
      return var10;
   }

   @Nullable
   public BlockPos findBiome(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      int var6 = var1 - var3 >> 2;
      int var7 = var2 - var3 >> 2;
      int var8 = var1 + var3 >> 2;
      int var9 = var2 + var3 >> 2;
      int var10 = var8 - var6 + 1;
      int var11 = var9 - var7 + 1;
      Biome[] var12 = this.noiseBiomeLayer.getArea(var6, var7, var10, var11);
      BlockPos var13 = null;
      int var14 = 0;

      for(int var15 = 0; var15 < var10 * var11; ++var15) {
         int var16 = var6 + var15 % var10 << 2;
         int var17 = var7 + var15 / var10 << 2;
         if (var4.contains(var12[var15])) {
            if (var13 == null || var5.nextInt(var14 + 1) == 0) {
               var13 = new BlockPos(var16, 0, var17);
            }

            ++var14;
         }
      }

      return var13;
   }

   public boolean canGenerateStructure(StructureFeature<?> var1) {
      return (Boolean)this.supportedStructures.computeIfAbsent(var1, (var1x) -> {
         Biome[] var2 = this.possibleBiomes;
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
         Biome[] var1 = this.possibleBiomes;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Biome var4 = var1[var3];
            this.surfaceBlocks.add(var4.getSurfaceBuilderConfig().getTopMaterial());
         }
      }

      return this.surfaceBlocks;
   }
}
