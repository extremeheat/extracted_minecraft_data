package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class FlatLevelSource extends ChunkGenerator {
   public static final Codec<FlatLevelSource> CODEC = RecordCodecBuilder.create(
      var0 -> commonCodec(var0)
            .and(FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings))
            .apply(var0, var0.stable(FlatLevelSource::new))
   );
   private final FlatLevelGeneratorSettings settings;

   public FlatLevelSource(Registry<StructureSet> var1, FlatLevelGeneratorSettings var2) {
      super(var1, var2.structureOverrides(), new FixedBiomeSource(var2.getBiome()), Util.memoize(var2::adjustGenerationSettings));
      this.settings = var2;
   }

   @Override
   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.settings;
   }

   @Override
   public void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4) {
   }

   @Override
   public int getSpawnHeight(LevelHeightAccessor var1) {
      return var1.getMinBuildHeight() + Math.min(var1.getHeight(), this.settings.getLayers().size());
   }

   @Override
   public CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, RandomState var3, StructureManager var4, ChunkAccess var5) {
      List var6 = this.settings.getLayers();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      Heightmap var8 = var5.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var9 = var5.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

      for(int var10 = 0; var10 < Math.min(var5.getHeight(), var6.size()); ++var10) {
         BlockState var11 = (BlockState)var6.get(var10);
         if (var11 != null) {
            int var12 = var5.getMinBuildHeight() + var10;

            for(int var13 = 0; var13 < 16; ++var13) {
               for(int var14 = 0; var14 < 16; ++var14) {
                  var5.setBlockState(var7.set(var13, var12, var14), var11, false);
                  var8.update(var13, var12, var14, var11);
                  var9.update(var13, var12, var14, var11);
               }
            }
         }
      }

      return CompletableFuture.completedFuture(var5);
   }

   @Override
   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      List var6 = this.settings.getLayers();

      for(int var7 = Math.min(var6.size(), var4.getMaxBuildHeight()) - 1; var7 >= 0; --var7) {
         BlockState var8 = (BlockState)var6.get(var7);
         if (var8 != null && var3.isOpaque().test(var8)) {
            return var4.getMinBuildHeight() + var7 + 1;
         }
      }

      return var4.getMinBuildHeight();
   }

   @Override
   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4) {
      return new NoiseColumn(
         var3.getMinBuildHeight(),
         this.settings
            .getLayers()
            .stream()
            .limit((long)var3.getHeight())
            .map(var0 -> var0 == null ? Blocks.AIR.defaultBlockState() : var0)
            .toArray(var0 -> new BlockState[var0])
      );
   }

   @Override
   public void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3) {
   }

   @Override
   public void applyCarvers(
      WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7, GenerationStep.Carving var8
   ) {
   }

   @Override
   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   @Override
   public int getMinY() {
      return 0;
   }

   @Override
   public int getGenDepth() {
      return 384;
   }

   @Override
   public int getSeaLevel() {
      return -63;
   }
}
