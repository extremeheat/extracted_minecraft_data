package net.minecraft.world.level.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public abstract class BiomeSource {
   private static final List<Biome> PLAYER_SPAWN_BIOMES;
   protected final Map<StructureFeature<?>, Boolean> supportedStructures = Maps.newHashMap();
   protected final Set<BlockState> surfaceBlocks = Sets.newHashSet();

   protected BiomeSource() {
      super();
   }

   public List<Biome> getPlayerSpawnBiomes() {
      return PLAYER_SPAWN_BIOMES;
   }

   public Biome getBiome(BlockPos var1) {
      return this.getBiome(var1.getX(), var1.getZ());
   }

   public abstract Biome getBiome(int var1, int var2);

   public Biome getNoiseBiome(int var1, int var2) {
      return this.getBiome(var1 << 2, var2 << 2);
   }

   public Biome[] getBiomeBlock(int var1, int var2, int var3, int var4) {
      return this.getBiomeBlock(var1, var2, var3, var4, true);
   }

   public abstract Biome[] getBiomeBlock(int var1, int var2, int var3, int var4, boolean var5);

   public abstract Set<Biome> getBiomesWithin(int var1, int var2, int var3);

   @Nullable
   public abstract BlockPos findBiome(int var1, int var2, int var3, List<Biome> var4, Random var5);

   public float getHeightValue(int var1, int var2) {
      return 0.0F;
   }

   public abstract boolean canGenerateStructure(StructureFeature<?> var1);

   public abstract Set<BlockState> getSurfaceBlocks();

   static {
      PLAYER_SPAWN_BIOMES = Lists.newArrayList(new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
   }
}
