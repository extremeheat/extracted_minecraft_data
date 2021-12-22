package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;

public class DebugLevelSource extends ChunkGenerator {
   public static final Codec<DebugLevelSource> CODEC;
   private static final int BLOCK_MARGIN = 2;
   private static final List<BlockState> ALL_BLOCKS;
   private static final int GRID_WIDTH;
   private static final int GRID_HEIGHT;
   protected static final BlockState AIR;
   protected static final BlockState BARRIER;
   public static final int HEIGHT = 70;
   public static final int BARRIER_HEIGHT = 60;
   private final Registry<Biome> biomes;

   public DebugLevelSource(Registry<Biome> var1) {
      super(new FixedBiomeSource((Biome)var1.getOrThrow(Biomes.PLAINS)), new StructureSettings(false));
      this.biomes = var1;
   }

   public Registry<Biome> biomes() {
      return this.biomes;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long var1) {
      return this;
   }

   public void buildSurface(WorldGenRegion var1, StructureFeatureManager var2, ChunkAccess var3) {
   }

   public void applyBiomeDecoration(WorldGenLevel var1, ChunkAccess var2, StructureFeatureManager var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      ChunkPos var5 = var2.getPos();
      int var6 = var5.field_504;
      int var7 = var5.field_505;

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            int var10 = SectionPos.sectionToBlockCoord(var6, var8);
            int var11 = SectionPos.sectionToBlockCoord(var7, var9);
            var1.setBlock(var4.set(var10, 60, var11), BARRIER, 2);
            BlockState var12 = getBlockStateFor(var10, var11);
            var1.setBlock(var4.set(var10, 70, var11), var12, 2);
         }
      }

   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, StructureFeatureManager var3, ChunkAccess var4) {
      return CompletableFuture.completedFuture(var4);
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4) {
      return 0;
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3) {
      return new NoiseColumn(0, new BlockState[0]);
   }

   public static BlockState getBlockStateFor(int var0, int var1) {
      BlockState var2 = AIR;
      if (var0 > 0 && var1 > 0 && var0 % 2 != 0 && var1 % 2 != 0) {
         var0 /= 2;
         var1 /= 2;
         if (var0 <= GRID_WIDTH && var1 <= GRID_HEIGHT) {
            int var3 = Mth.abs(var0 * GRID_WIDTH + var1);
            if (var3 < ALL_BLOCKS.size()) {
               var2 = (BlockState)ALL_BLOCKS.get(var3);
            }
         }
      }

      return var2;
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
      return 63;
   }

   static {
      CODEC = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(DebugLevelSource::new, DebugLevelSource::biomes).stable().codec();
      ALL_BLOCKS = (List)StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((var0) -> {
         return var0.getStateDefinition().getPossibleStates().stream();
      }).collect(Collectors.toList());
      GRID_WIDTH = Mth.ceil(Mth.sqrt((float)ALL_BLOCKS.size()));
      GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
      AIR = Blocks.AIR.defaultBlockState();
      BARRIER = Blocks.BARRIER.defaultBlockState();
   }
}
