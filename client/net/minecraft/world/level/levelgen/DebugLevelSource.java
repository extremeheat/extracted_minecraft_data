package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class DebugLevelSource extends ChunkGenerator {
   public static final Codec<DebugLevelSource> CODEC;
   private static final List<BlockState> ALL_BLOCKS;
   private static final int GRID_WIDTH;
   private static final int GRID_HEIGHT;
   protected static final BlockState AIR;
   protected static final BlockState BARRIER;
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

   public void buildSurfaceAndBedrock(WorldGenRegion var1, ChunkAccess var2) {
   }

   public void applyCarvers(long var1, BiomeManager var3, ChunkAccess var4, GenerationStep.Carving var5) {
   }

   public void applyBiomeDecoration(WorldGenRegion var1, StructureFeatureManager var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      int var4 = var1.getCenterX();
      int var5 = var1.getCenterZ();

      for(int var6 = 0; var6 < 16; ++var6) {
         for(int var7 = 0; var7 < 16; ++var7) {
            int var8 = SectionPos.sectionToBlockCoord(var4, var6);
            int var9 = SectionPos.sectionToBlockCoord(var5, var7);
            var1.setBlock(var3.set(var8, 60, var9), BARRIER, 2);
            BlockState var10 = getBlockStateFor(var8, var9);
            if (var10 != null) {
               var1.setBlock(var3.set(var8, 70, var9), var10, 2);
            }
         }
      }

   }

   public void fillFromNoise(LevelAccessor var1, StructureFeatureManager var2, ChunkAccess var3) {
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3) {
      return 0;
   }

   public BlockGetter getBaseColumn(int var1, int var2) {
      return new NoiseColumn(new BlockState[0]);
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
