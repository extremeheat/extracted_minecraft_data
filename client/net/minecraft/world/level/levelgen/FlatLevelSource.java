package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class FlatLevelSource extends ChunkGenerator {
   public static final Codec<FlatLevelSource> CODEC;
   private final FlatLevelGeneratorSettings settings;

   public FlatLevelSource(FlatLevelGeneratorSettings var1) {
      super(new FixedBiomeSource(var1.getBiomeFromSettings()), new FixedBiomeSource(var1.getBiome()), var1.structureSettings(), 0L);
      this.settings = var1;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long var1) {
      return this;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.settings;
   }

   public void buildSurface(WorldGenRegion var1, StructureFeatureManager var2, ChunkAccess var3) {
   }

   public int getSpawnHeight(LevelHeightAccessor var1) {
      return var1.getMinBuildHeight() + Math.min(var1.getHeight(), this.settings.getLayers().size());
   }

   protected boolean validBiome(Registry<Biome> var1, Predicate<ResourceKey<Biome>> var2, Biome var3) {
      return var1.getResourceKey(this.settings.getBiome()).filter(var2).isPresent();
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, StructureFeatureManager var3, ChunkAccess var4) {
      List var5 = this.settings.getLayers();
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      Heightmap var7 = var4.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var8 = var4.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

      for(int var9 = 0; var9 < Math.min(var4.getHeight(), var5.size()); ++var9) {
         BlockState var10 = (BlockState)var5.get(var9);
         if (var10 != null) {
            int var11 = var4.getMinBuildHeight() + var9;

            for(int var12 = 0; var12 < 16; ++var12) {
               for(int var13 = 0; var13 < 16; ++var13) {
                  var4.setBlockState(var6.set(var12, var11, var13), var10, false);
                  var7.update(var12, var11, var13, var10);
                  var8.update(var12, var11, var13, var10);
               }
            }
         }
      }

      return CompletableFuture.completedFuture(var4);
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4) {
      List var5 = this.settings.getLayers();

      for(int var6 = Math.min(var5.size(), var4.getMaxBuildHeight()) - 1; var6 >= 0; --var6) {
         BlockState var7 = (BlockState)var5.get(var6);
         if (var7 != null && var3.isOpaque().test(var7)) {
            return var4.getMinBuildHeight() + var6 + 1;
         }
      }

      return var4.getMinBuildHeight();
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3) {
      return new NoiseColumn(var3.getMinBuildHeight(), (BlockState[])this.settings.getLayers().stream().limit((long)var3.getHeight()).map((var0) -> {
         return var0 == null ? Blocks.AIR.defaultBlockState() : var0;
      }).toArray((var0) -> {
         return new BlockState[var0];
      }));
   }

   public Climate.Sampler climateSampler() {
      return (var0, var1, var2) -> {
         return Climate.target(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      };
   }

   public void applyCarvers(WorldGenRegion var1, long var2, BiomeManager var4, StructureFeatureManager var5, ChunkAccess var6, GenerationStep.Carving var7) {
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   public int getMinY() {
      return 0;
   }

   public int getGenDepth() {
      return 384;
   }

   public int getSeaLevel() {
      return -63;
   }

   static {
      CODEC = FlatLevelGeneratorSettings.CODEC.fieldOf("settings").xmap(FlatLevelSource::new, FlatLevelSource::settings).codec();
   }
}
