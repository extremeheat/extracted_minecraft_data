package net.minecraft.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Util;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jspecify.annotations.Nullable;

public class FlatLevelSource extends ChunkGenerator {
   public static final MapCodec<FlatLevelSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply(i, i.stable(FlatLevelSource::new)));
   private final FlatLevelGeneratorSettings settings;

   public FlatLevelSource(final FlatLevelGeneratorSettings generatorSettings) {
      FixedBiomeSource var10001 = new FixedBiomeSource(generatorSettings.getBiome());
      Objects.requireNonNull(generatorSettings);
      super(var10001, Util.memoize(generatorSettings::adjustGenerationSettings));
      this.settings = generatorSettings;
   }

   public ChunkGeneratorStructureState createState(final HolderLookup<StructureSet> structureSets, final RandomState randomState, final long levelSeed) {
      Stream<Holder<StructureSet>> structures = (Stream)this.settings.structureOverrides().map(HolderSet::stream).orElseGet(() -> structureSets.listElements().map((e) -> e));
      return ChunkGeneratorStructureState.createForFlat(randomState, levelSeed, this.getOrigin(randomState), this.biomeSource, structures);
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.settings;
   }

   public int getSpawnHeight(final LevelHeightAccessor heightAccessor) {
      return heightAccessor.getMinY() + Math.min(heightAccessor.getHeight(), this.settings.getLayers().size());
   }

   public CompletableFuture<ChunkAccess> buildTerrain(final ChunkAccess chunk, final Blender blender, final RandomState randomState, final StructureManager structureManager, final @Nullable WorldGenRegion carverBiomeRegion, final Set<Holder<Biome>> possibleBiomes) {
      List<BlockState> layers = this.settings.getLayers();
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

      for(int layerIndex = 0; layerIndex < Math.min(chunk.getHeight(), layers.size()); ++layerIndex) {
         BlockState blockState = (BlockState)layers.get(layerIndex);
         if (blockState != null) {
            int y = chunk.getMinY() + layerIndex;

            for(int x = 0; x < 16; ++x) {
               for(int z = 0; z < 16; ++z) {
                  chunk.setBlockState(blockPos.set(x, y, z), blockState);
               }
            }
         }
      }

      return CompletableFuture.completedFuture(chunk);
   }

   public NoiseColumn getBaseColumn(final int x, final int z, final LevelHeightAccessor heightAccessor, final RandomState randomState) {
      List<BlockState> layers = this.settings.getLayers();
      int minY = heightAccessor.getMinY();
      NoiseColumn noiseColumn = new NoiseColumn(minY, layers.size());

      for(int y = 0; y < layers.size(); ++y) {
         BlockState block = (BlockState)layers.get(y);
         if (!block.isAir()) {
            if (block.getBlock() instanceof LiquidBlock) {
               noiseColumn.setFluid(y + minY, block);
            } else {
               noiseColumn.setSolid(y + minY);
            }
         }
      }

      return noiseColumn;
   }

   public void addDebugScreenInfo(final List<String> result, final RandomState randomState, final BlockPos feetPos, final SamplerContext samplerContext) {
   }

   public void spawnOriginalMobs(final WorldGenRegion worldGenRegion) {
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
}
