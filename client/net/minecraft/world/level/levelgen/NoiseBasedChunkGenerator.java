package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextMap;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.NoiseBiomeResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.jspecify.annotations.Nullable;

public final class NoiseBasedChunkGenerator extends ChunkGenerator {
   public static final MapCodec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((g) -> g.biomeSource), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((g) -> g.settings)).apply(i, i.stable(NoiseBasedChunkGenerator::new)));
   private static final DecimalFormat DEBUG_DENSITY_FORMAT;
   private final Holder<NoiseGeneratorSettings> settings;
   private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

   public NoiseBasedChunkGenerator(final BiomeSource biomeSource, final Holder<NoiseGeneratorSettings> settings) {
      super(biomeSource);
      this.settings = settings;
      this.globalFluidPicker = Suppliers.memoize(() -> createFluidPicker(settings.value()));
   }

   private static Aquifer.FluidPicker createFluidPicker(final NoiseGeneratorSettings settings) {
      Aquifer.FluidStatus lavaStatus = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
      int seaLevel = settings.seaLevel();
      Aquifer.FluidStatus seaStatus = new Aquifer.FluidStatus(seaLevel, settings.defaultFluid());
      Aquifer.FluidStatus emptyStatus = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
      return (x, y, z) -> {
         if (SharedConstants.DEBUG_DISABLE_FLUID_GENERATION) {
            return emptyStatus;
         } else {
            return y < Math.min(-54, seaLevel) ? lavaStatus : seaStatus;
         }
      };
   }

   protected NoiseBiomeResolver decorateBiomeResolver(final Blender blender, final ProtoChunk protoChunk, NoiseBiomeResolver noiseBiomeResolver) {
      noiseBiomeResolver = blender.getBiomeResolver(noiseBiomeResolver);
      noiseBiomeResolver = RetroGen.getBiomeResolver(noiseBiomeResolver, protoChunk);
      return noiseBiomeResolver;
   }

   private NoiseChunk createNoiseChunk(final RandomState randomState, final DensityVolume chunkVolume, final ContextMap chunkSamplerFields) {
      NoiseGeneratorSettings settings = this.settings.value();
      DensityFunction finalDensity = settings.noiseRouter().finalDensity();
      Aquifer.Config aquifers = (Aquifer.Config)settings.aquifers().orElse((Object)null);
      return new NoiseChunk(randomState, chunkVolume, chunkSamplerFields, finalDensity, aquifers, (Aquifer.FluidPicker)this.globalFluidPicker.get());
   }

   private static DensityVolume chunkVolume(final ChunkAccess chunk, final NoiseSettings noiseSettings) {
      ChunkPos pos = chunk.getPos();
      return new DensityVolume(16, noiseSettings.height(), 16, pos.getMinBlockX(), noiseSettings.minY(), pos.getMinBlockZ());
   }

   private static ContextMap chunkSamplerFields(final ChunkAccess chunk, final Blender blender, final StructureManager structureManager, final DensityVolume volume) {
      ContextMap.Builder userFields = ContextMap.builder().set(Beardifier.CONTEXT_KEY, Beardifier.forStructuresInChunk(structureManager, chunk.getPos()));
      if (!blender.isEmpty()) {
         Blender.OutputBuffer blendBuffer = blender.blendOffsetAndFactor(volume);
         userFields.set(Blender.CONTEXT_KEY, blender).set(Blender.ALPHA_KEY, blendBuffer.createAlphaSampler()).set(Blender.OFFSET_KEY, blendBuffer.createOffsetSampler());
      }

      return userFields.build();
   }

   public ChunkPos getOrigin(final RandomState randomState) {
      List<SpawnTargetPoint> spawnTarget = ((NoiseGeneratorSettings)this.settings.value()).spawnTarget();
      if (spawnTarget.isEmpty()) {
         return super.getOrigin(randomState);
      } else {
         SamplerContext samplerContext = SamplerContext.builder().enableCaches().build();
         return ChunkPos.containing(NoiseSpawnFinder.findSpawnPosition(spawnTarget, randomState.samplersWithContext(samplerContext)));
      }
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public Holder<NoiseGeneratorSettings> generatorSettings() {
      return this.settings;
   }

   public boolean stable(final ResourceKey<NoiseGeneratorSettings> expectedPreset) {
      return this.settings.is(expectedPreset);
   }

   public void addDebugScreenInfo(final List<String> result, final RandomState randomState, final BlockPos feetPos, final SamplerContext samplerContext) {
      List<NoiseGeneratorSettings.DebugFunctionEntry> functions = ((NoiseGeneratorSettings)this.settings.value()).debugFunctions().functions();
      if (!functions.isEmpty()) {
         DensitySamplerSet samplers = randomState.samplersWithContext(samplerContext);
         StringBuilder builder = new StringBuilder("Density ");

         for(NoiseGeneratorSettings.DebugFunctionEntry entry : functions) {
            builder.append(entry.label()).append(": ");
            builder.append(DEBUG_DENSITY_FORMAT.format((double)samplers.sampleValue(entry.function(), feetPos.getX(), feetPos.getY(), feetPos.getZ())));
            builder.append(' ');
         }

         builder.deleteCharAt(builder.length() - 1);
         result.add(builder.toString());
      }
   }

   public NoiseColumn getBaseColumn(final int blockX, final int blockZ, final LevelHeightAccessor heightAccessor, final RandomState randomState) {
      NoiseSettings noiseSettings = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(heightAccessor);
      if (noiseSettings.height() <= 0) {
         return new NoiseColumn(noiseSettings.minY(), 0);
      } else {
         DensityVolume volume = new DensityVolume(1, noiseSettings.height(), 1, blockX, noiseSettings.minY(), blockZ);

         try (NoiseChunk noiseChunk = this.createNoiseChunk(randomState, volume, ContextMap.EMPTY)) {
            return noiseChunk.prepareColumn(0, 0);
         }
      }
   }

   private @Nullable CarvingMask generateCarvingMask(final ChunkAccess chunk, final Blender blender, final RandomState randomState, final @Nullable WorldGenRegion carverBiomeRegion, final NoiseBiomeResolver noiseBiomeResolver, final VerticalAnchor.Context verticalAnchorContext) {
      if (SharedConstants.DEBUG_DISABLE_CARVERS) {
         return null;
      } else {
         WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
         int range = 8;
         ChunkPos pos = chunk.getPos();
         int protectedBlocksOnTop = chunk.isUpgrading() ? 0 : 7;
         int maxY = verticalAnchorContext.maxY() - protectedBlocksOnTop;
         CarvingMask mask = new CarvingMask(verticalAnchorContext.minY() + 1, maxY);

         for(int dx = -8; dx <= 8; ++dx) {
            for(int dz = -8; dz <= 8; ++dz) {
               ChunkPos sourcePos = new ChunkPos(pos.x() + dx, pos.z() + dz);
               BiomeGenerationSettings sourceBiomeGenerationSettings;
               if (carverBiomeRegion != null) {
                  ChunkAccess carverCenterChunk = carverBiomeRegion.getChunk(sourcePos.x(), sourcePos.z());
                  sourceBiomeGenerationSettings = carverCenterChunk.carverBiome(() -> this.getBiomeGenerationSettingsForCarver(noiseBiomeResolver, sourcePos));
               } else {
                  sourceBiomeGenerationSettings = this.getBiomeGenerationSettingsForCarver(noiseBiomeResolver, sourcePos);
               }

               Iterable<Holder<WorldCarver>> carvers = sourceBiomeGenerationSettings.getCarvers();
               int index = 0;

               for(Holder<WorldCarver> carverHolder : carvers) {
                  WorldCarver carver = carverHolder.value();
                  random.setLargeFeatureSeed(randomState.seed() + (long)index, sourcePos.x(), sourcePos.z());
                  if (carver.isStartChunk(random)) {
                     carver.carve(verticalAnchorContext, random, chunk.getPos(), sourcePos, mask);
                  }

                  ++index;
               }
            }
         }

         CarvingMask.Filter blenderFilter = blender.getCarvingFilter();
         if (blenderFilter != null) {
            mask.applyFilter(blenderFilter);
         }

         return mask.isEmpty() ? null : mask;
      }
   }

   private BiomeGenerationSettings getBiomeGenerationSettingsForCarver(final NoiseBiomeResolver noiseBiomeResolver, final ChunkPos sourcePos) {
      int quartX = QuartPos.fromBlock(sourcePos.getMinBlockX());
      int quartZ = QuartPos.fromBlock(sourcePos.getMinBlockZ());
      return this.getBiomeGenerationSettings(noiseBiomeResolver.getNoiseBiome(quartX, 0, quartZ));
   }

   public CompletableFuture<ChunkAccess> buildTerrain(final ChunkAccess chunk, final Blender blender, final RandomState randomState, final StructureManager structureManager, final @Nullable WorldGenRegion carverBiomeRegion, final Set<Holder<Biome>> possibleBiomes) {
      NoiseSettings noiseSettings = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(chunk.getHeightAccessorForGeneration());
      return noiseSettings.height() > 0 && !SharedConstants.debugVoidTerrain(chunk.getPos()) ? CompletableFuture.supplyAsync(() -> this.buildTerrain(chunk, blender, randomState, structureManager, carverBiomeRegion, possibleBiomes, noiseSettings), Util.backgroundExecutor().forName("buildTerrain")) : CompletableFuture.completedFuture(chunk);
   }

   private ChunkAccess buildTerrain(final ChunkAccess chunk, final Blender blender, final RandomState randomState, final StructureManager structureManager, final @Nullable WorldGenRegion carverBiomeRegion, final Set<Holder<Biome>> possibleBiomes, final NoiseSettings noiseSettings) {
      ProfilerFiller profiler = Profiler.get();
      DensityVolume volume = chunkVolume(chunk, noiseSettings);
      ContextMap chunkSamplerFields = chunkSamplerFields(chunk, blender, structureManager, volume);

      ChunkAccess var27;
      try (NoiseChunk noiseChunk = this.createNoiseChunk(randomState, volume, chunkSamplerFields)) {
         Climate.Sampler noiseClimateSampler = ((NoiseGeneratorSettings)this.settings.value()).noiseRouter().createClimateSampler(noiseChunk.cachingSamplers());
         NoiseBiomeResolver noiseBiomeResolver = this.biomeSource.createResolver(noiseClimateSampler);
         VerticalAnchor.Context verticalAnchorContext = VerticalAnchor.Context.from(this, chunk.getHeightAccessorForGeneration());

         CarvingMask carvingMask;
         try (Zone var16 = profiler.zone("generateCarvingMask")) {
            carvingMask = this.generateCarvingMask(chunk, blender, randomState, carverBiomeRegion, noiseBiomeResolver, verticalAnchorContext);
         }

         ChunkTerrainBuilder terrainBuilder = new ChunkTerrainBuilder(randomState, noiseChunk.cachingSamplers(), noiseChunk.volumeWithBlocks(), verticalAnchorContext, (MaterialRule)(this.settings.value()).materialRule().value(), chunk, possibleBiomes);

         try (Zone var17 = profiler.zone("fillChunk")) {
            terrainBuilder.fillChunk(chunk, noiseChunk, carvingMask);
         }

         var27 = chunk;
      }

      return var27;
   }

   public int getGenDepth() {
      return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().height();
   }

   public int getSeaLevel() {
      return ((NoiseGeneratorSettings)this.settings.value()).seaLevel();
   }

   public int getMinY() {
      return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().minY();
   }

   public void spawnOriginalMobs(final WorldGenRegion worldGenRegion) {
      if (!((NoiseGeneratorSettings)this.settings.value()).disableMobGeneration()) {
         ChunkPos center = worldGenRegion.getCenter();
         BlockPos sourcePos = center.getWorldPosition().atY(worldGenRegion.getMaxY());
         WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
         random.setDecorationSeed(worldGenRegion.getSeed(), center.getMinBlockX(), center.getMinBlockZ());
         NaturalSpawner.spawnMobsForChunkGeneration(worldGenRegion, sourcePos, center, random);
      }
   }

   static {
      DEBUG_DENSITY_FORMAT = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.ROOT));
   }
}
