package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

public final class NoiseBasedChunkGenerator extends ChunkGenerator {
   public static final MapCodec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((g) -> g.biomeSource), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((g) -> g.settings)).apply(i, i.stable(NoiseBasedChunkGenerator::new)));
   private static final BlockState AIR;
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

   protected BiomeResolver decorateBiomeResolver(final Blender blender, final ChunkAccess protoChunk, BiomeResolver biomeResolver) {
      biomeResolver = blender.getBiomeResolver(biomeResolver);
      biomeResolver = BelowZeroRetrogen.getBiomeResolver(biomeResolver, protoChunk);
      return biomeResolver;
   }

   private NoiseChunk createNoiseChunk(final ChunkAccess chunk, final StructureManager structureManager, final Blender blender, final RandomState randomState, final NoiseSettings noiseSettings) {
      Beardifier beardifier = Beardifier.forStructuresInChunk(structureManager, chunk.getPos());
      return new NoiseChunk(randomState, beardifier, this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), blender, chunkVolume(chunk, noiseSettings));
   }

   private static DensityVolume chunkVolume(final ChunkAccess chunk, final NoiseSettings noiseSettings) {
      ChunkPos pos = chunk.getPos();
      return new DensityVolume(16, noiseSettings.height(), 16, pos.getMinBlockX(), noiseSettings.minY(), pos.getMinBlockZ());
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

   public int getBaseHeight(final int x, final int z, final Heightmap.Types type, final LevelHeightAccessor heightAccessor, final RandomState randomState) {
      return this.iterateNoiseColumn(heightAccessor, randomState, x, z, (MutableObject)null, type.isOpaque()).orElse(heightAccessor.getMinY());
   }

   public NoiseColumn getBaseColumn(final int x, final int z, final LevelHeightAccessor heightAccessor, final RandomState randomState) {
      MutableObject<NoiseColumn> result = new MutableObject();
      this.iterateNoiseColumn(heightAccessor, randomState, x, z, result, (Predicate)null);
      return (NoiseColumn)result.get();
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

   private OptionalInt iterateNoiseColumn(final LevelHeightAccessor heightAccessor, final RandomState randomState, final int blockX, final int blockZ, final @Nullable MutableObject<NoiseColumn> columnReference, final @Nullable Predicate<BlockState> tester) {
      NoiseSettings noiseSettings = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(heightAccessor);
      if (noiseSettings.height() <= 0) {
         return OptionalInt.empty();
      } else {
         DensityVolume volume = new DensityVolume(1, noiseSettings.height(), 1, blockX, noiseSettings.minY(), blockZ);
         BlockState[] writeTo;
         if (columnReference == null) {
            writeTo = null;
         } else {
            writeTo = new BlockState[volume.sizeY()];
            columnReference.setValue(new NoiseColumn(volume.minBlockY(), writeTo));
         }

         try (NoiseChunk noiseChunk = new NoiseChunk(randomState, (Beardifier)null, this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), Blender.empty(), volume)) {
            Aquifer aquifer = noiseChunk.aquifer();
            BlockState defaultState = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();
            DensitySampler.Bound finalDensity = noiseChunk.cachingSamplers().get(((NoiseGeneratorSettings)this.settings.value()).noiseRouter().finalDensity());

            try (ScopedDensityBuffer densityBuffer = finalDensity.sampleVolume(noiseChunk.volume())) {
               for(int y = volume.sizeY() - 1; y >= 0; --y) {
                  float density = densityBuffer.get(volume.indexUnchecked(0, y, 0));
                  int blockY = volume.blockY(y);
                  BlockState baseState = aquifer.computeSubstance(blockX, blockY, blockZ, (double)density);
                  BlockState state = baseState == null ? defaultState : baseState;
                  if (writeTo != null) {
                     writeTo[y] = state;
                  }

                  if (tester != null && tester.test(state)) {
                     OptionalInt var20 = OptionalInt.of(blockY + 1);
                     return var20;
                  }
               }
            }
         }

         return OptionalInt.empty();
      }
   }

   private void buildSurface(final ChunkAccess protoChunk, final NoiseChunk noiseChunk, final RandomState randomState, final BiomeManager biomeManager, final Set<Holder<Biome>> possibleBiomes, final MaterialRule materialRule) {
      if (!SharedConstants.debugVoidTerrain(protoChunk.getPos()) && !SharedConstants.DEBUG_DISABLE_SURFACE) {
         WorldGenerationContext context = new WorldGenerationContext(this, protoChunk.getHeightAccessorForGeneration());
         randomState.surfaceSystem().buildSurface(randomState, biomeManager, context, protoChunk, noiseChunk, materialRule, possibleBiomes);
      }
   }

   private void generateCarvers(final ChunkAccess chunk, final Blender blender, final NoiseChunk noiseChunk, final RandomState randomState, final BiomeManager biomeManager, final @Nullable WorldGenRegion carverBiomeRegion, final MaterialRule materialRule) {
      if (!SharedConstants.DEBUG_DISABLE_CARVERS && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
         BiomeResolver biomeResolver = this.biomeSource.createUncachedResolver(randomState);
         WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
         int range = 8;
         ChunkPos pos = chunk.getPos();
         WorldGenerationContext context = new WorldGenerationContext(this, chunk.getHeightAccessorForGeneration());
         int protectedBlocksOnTop = chunk.isUpgrading() ? 0 : 7;
         int maxY = context.getMinGenY() + context.getGenDepth() - 1 - protectedBlocksOnTop;
         CarvingMask mask = new CarvingMask(context.getMinGenY() + 1, maxY);

         for(int dx = -8; dx <= 8; ++dx) {
            for(int dz = -8; dz <= 8; ++dz) {
               ChunkPos sourcePos = new ChunkPos(pos.x() + dx, pos.z() + dz);
               BiomeGenerationSettings sourceBiomeGenerationSettings;
               if (carverBiomeRegion != null) {
                  ChunkAccess carverCenterChunk = carverBiomeRegion.getChunk(sourcePos.x(), sourcePos.z());
                  sourceBiomeGenerationSettings = carverCenterChunk.carverBiome(() -> this.getBiomeGenerationSettingsForCarver(biomeResolver, sourcePos));
               } else {
                  sourceBiomeGenerationSettings = this.getBiomeGenerationSettingsForCarver(biomeResolver, sourcePos);
               }

               Iterable<Holder<WorldCarver>> carvers = sourceBiomeGenerationSettings.getCarvers();
               int index = 0;

               for(Holder<WorldCarver> carverHolder : carvers) {
                  WorldCarver carver = carverHolder.value();
                  random.setLargeFeatureSeed(randomState.seed() + (long)index, sourcePos.x(), sourcePos.z());
                  if (carver.isStartChunk(random)) {
                     carver.carve(context, random, chunk.getPos(), sourcePos, mask);
                  }

                  ++index;
               }
            }
         }

         if (!mask.isEmpty()) {
            try (Zone var27 = Profiler.get().zone("applyCarvingMask")) {
               BiomeManager correctBiomeManager = biomeManager.withDifferentSource(biomeResolver);
               Objects.requireNonNull(correctBiomeManager);
               this.applyCarvingMask(chunk, mask, randomState, materialRule, context, noiseChunk, correctBiomeManager::getBiome, blender.getCarvingFilter());
            }
         }

      }
   }

   private void applyCarvingMask(final ChunkAccess chunk, final CarvingMask mask, final RandomState randomState, final MaterialRule materialRule, final WorldGenerationContext context, final NoiseChunk noiseChunk, final Function<BlockPos, Holder<Biome>> biomeGetter, final CarvingMask.@Nullable Filter filter) {
      ChunkPos chunkPos = chunk.getPos();
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos helperPos = new BlockPos.MutableBlockPos();
      Aquifer aquifer = noiseChunk.aquifer();
      mask.visit((x, z, bottomY, topY) -> {
         boolean hasGrass = false;
         int worldX = chunkPos.getBlockX(x);
         int worldZ = chunkPos.getBlockZ(z);

         for(int worldY = topY; worldY >= bottomY; --worldY) {
            if (filter == null || filter.test(x, worldY, z)) {
               blockPos.set(worldX, worldY, worldZ);
               BlockState blockState = chunk.getBlockState(blockPos);
               if (!blockState.is(BlockTags.UNCARVABLE)) {
                  if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.MYCELIUM)) {
                     hasGrass = true;
                  }

                  BlockState state = aquifer.computeSubstance(worldX, worldY, worldZ, 0.0);
                  if (state != null) {
                     chunk.setBlockState(blockPos, state);
                     if (aquifer.shouldScheduleFluidUpdate() && !state.getFluidState().isEmpty()) {
                        chunk.markPosForPostProcessing(blockPos);
                     }

                     if (hasGrass) {
                        helperPos.setWithOffset(blockPos, (Direction)Direction.DOWN);
                        if (chunk.getBlockState(helperPos).is(Blocks.DIRT)) {
                           randomState.surfaceSystem().topMaterial(materialRule, randomState, context, biomeGetter, chunk, noiseChunk.cachingSamplers(), helperPos, !state.getFluidState().isEmpty()).ifPresent((topMaterial) -> {
                              chunk.setBlockState(helperPos, topMaterial);
                              if (!topMaterial.getFluidState().isEmpty()) {
                                 chunk.markPosForPostProcessing(helperPos);
                              }

                           });
                        }
                     }
                  }
               }
            }
         }

      });
   }

   private BiomeGenerationSettings getBiomeGenerationSettingsForCarver(final BiomeResolver biomeResolver, final ChunkPos sourcePos) {
      int quartX = QuartPos.fromBlock(sourcePos.getMinBlockX());
      int quartZ = QuartPos.fromBlock(sourcePos.getMinBlockZ());
      return this.getBiomeGenerationSettings(biomeResolver.getNoiseBiome(quartX, 0, quartZ));
   }

   public CompletableFuture<ChunkAccess> buildTerrain(final ChunkAccess chunk, final Blender blender, final RandomState randomState, final StructureManager structureManager, final BiomeManager biomeManager, final @Nullable WorldGenRegion carverBiomeRegion, final Set<Holder<Biome>> possibleBiomes) {
      NoiseSettings noiseSettings = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(chunk.getHeightAccessorForGeneration());
      return noiseSettings.height() > 0 && !SharedConstants.debugVoidTerrain(chunk.getPos()) ? CompletableFuture.supplyAsync(() -> {
         ProfilerFiller profiler = Profiler.get();

         ChunkAccess var43;
         try (NoiseChunk noiseChunk = this.createNoiseChunk(chunk, structureManager, blender, randomState, noiseSettings)) {
            DensityVolume volume = noiseChunk.volume();
            int topSectionIndex = chunk.getSectionIndex(volume.maxBlockY());
            int bottomSectionIndex = chunk.getSectionIndex(volume.minBlockY());
            Set<LevelChunkSection> sections = Sets.newHashSet();

            for(int sectionIndex = topSectionIndex; sectionIndex >= bottomSectionIndex; --sectionIndex) {
               LevelChunkSection section = chunk.getSection(sectionIndex);
               section.acquire();
               sections.add(section);
            }

            try {
               Zone materialRule = profiler.zone("doFill");

               try {
                  this.doFill(noiseChunk, chunk);
               } catch (Throwable var36) {
                  if (materialRule != null) {
                     try {
                        materialRule.close();
                     } catch (Throwable x2) {
                        var36.addSuppressed(x2);
                     }
                  }

                  throw var36;
               }

               if (materialRule != null) {
                  materialRule.close();
               }
            } finally {
               for(LevelChunkSection section : sections) {
                  section.release();
               }

            }

            MaterialRule materialRule = (MaterialRule)(this.settings.value()).materialRule().value();

            try (Zone t$ = profiler.zone("buildSurface")) {
               this.buildSurface(chunk, noiseChunk, randomState, biomeManager, possibleBiomes, materialRule);
            }

            try (Zone sectionxx = profiler.zone("generateCarvers")) {
               this.generateCarvers(chunk, blender, noiseChunk, randomState, biomeManager, carverBiomeRegion, materialRule);
            }

            var43 = chunk;
         }

         return var43;
      }, Util.backgroundExecutor().forName("buildTerrain")) : CompletableFuture.completedFuture(chunk);
   }

   private void doFill(final NoiseChunk noiseChunk, final ChunkAccess chunk) {
      Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      Aquifer aquifer = noiseChunk.aquifer();
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
      DensityVolume volume = noiseChunk.volume();
      DensitySampler.Bound finalDensity = noiseChunk.cachingSamplers().get(((NoiseGeneratorSettings)this.settings.value()).noiseRouter().finalDensity());

      try (ScopedDensityBuffer densityBuffer = finalDensity.sampleVolume(volume)) {
         for(int z = 0; z < volume.sizeZ(); ++z) {
            int blockZ = volume.blockZ(z);

            for(int x = 0; x < volume.sizeX(); ++x) {
               int blockX = volume.blockX(x);

               for(int y = volume.sizeY() - 1; y >= 0; --y) {
                  int blockY = volume.blockY(y);
                  LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(blockY));
                  float density = densityBuffer.get(volume.indexUnchecked(x, y, z));
                  BlockState state = aquifer.computeSubstance(blockX, blockY, blockZ, (double)density);
                  if (state == null) {
                     state = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();
                  }

                  state = this.debugPreliminarySurfaceLevel(noiseChunk, blockX, blockY, blockZ, state);
                  if (state != AIR) {
                     section.setBlockState(x, SectionPos.sectionRelative(blockY), z, state, false);
                     oceanFloor.update(x, blockY, z, state);
                     worldSurface.update(x, blockY, z, state);
                     if (aquifer.shouldScheduleFluidUpdate() && !state.getFluidState().isEmpty()) {
                        blockPos.set(blockX, blockY, blockZ);
                        chunk.markPosForPostProcessing(blockPos);
                     }
                  }
               }
            }
         }
      }

   }

   private BlockState debugPreliminarySurfaceLevel(final NoiseChunk noiseChunk, final int posX, final int posY, final int posZ, BlockState state) {
      if (SharedConstants.DEBUG_AQUIFERS && posZ >= 0 && posZ % 4 == 0) {
         DensityFunction surfaceLevelFunction = (DensityFunction)((NoiseGeneratorSettings)this.settings.value()).aquifers().map(Aquifer.Config::surfaceLevel).orElse((Object)null);
         if (surfaceLevelFunction == null) {
            return state;
         }

         int preliminarySurfaceLevel = Mth.floor(noiseChunk.cachingSamplers().sampleValue(surfaceLevelFunction, posX, 0, posZ));
         int adjustedSurfaceLevel = preliminarySurfaceLevel + 8;
         if (posY == adjustedSurfaceLevel) {
            state = adjustedSurfaceLevel < this.getSeaLevel() ? Blocks.SLIME_BLOCK.defaultBlockState() : Blocks.HONEY_BLOCK.defaultBlockState();
         }
      }

      return state;
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
      AIR = Blocks.AIR.defaultBlockState();
      DEBUG_DENSITY_FORMAT = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.ROOT));
   }
}
