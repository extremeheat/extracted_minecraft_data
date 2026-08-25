package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class Structure {
   public static final Codec<Structure> DIRECT_CODEC;
   public static final Codec<Holder<Structure>> CODEC;
   protected final StructureSettings settings;

   public static <S extends Structure> RecordCodecBuilder<S, StructureSettings> settingsCodec(final RecordCodecBuilder.Instance<S> i) {
      return Structure.StructureSettings.CODEC.forGetter((e) -> e.settings);
   }

   public static <S extends Structure> MapCodec<S> simpleCodec(final Function<StructureSettings, S> constructor) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(settingsCodec(i)).apply(i, constructor));
   }

   protected Structure(final StructureSettings settings) {
      super();
      this.settings = settings;
   }

   public HolderSet<Biome> biomes() {
      return this.settings.biomes;
   }

   public Map<MobCategory, StructureSpawnOverride> spawnOverrides() {
      return this.settings.spawnOverrides;
   }

   public GenerationStep.Decoration step() {
      return this.settings.step;
   }

   public TerrainAdjustment terrainAdaptation() {
      return this.settings.terrainAdaptation;
   }

   public BoundingBox adjustBoundingBox(final BoundingBox boundingBox) {
      return this.terrainAdaptation() != TerrainAdjustment.NONE ? boundingBox.inflatedBy(12) : boundingBox;
   }

   public StructureStart generate(final Holder<Structure> selected, final ResourceKey<Level> dimension, final RegistryAccess registryAccess, final ChunkGenerator chunkGenerator, final BiomeSource biomeSource, final Climate.Sampler climateSampler, final RandomState randomState, final StructureTemplateManager structureTemplateManager, final long seed, final ChunkPos sourceChunkPos, final int references, final LevelHeightAccessor heightAccessor, final Predicate<Holder<Biome>> validBiome) {
      ProfiledDuration profiled = JvmProfiler.INSTANCE.onStructureGenerate(sourceChunkPos, dimension, selected);
      GenerationContext context = new GenerationContext(registryAccess, chunkGenerator, biomeSource, climateSampler, randomState, structureTemplateManager, seed, sourceChunkPos, heightAccessor, validBiome);
      Optional<GenerationStub> generation = this.findValidGenerationPoint(context);
      if (generation.isPresent()) {
         StructurePiecesBuilder builder = ((GenerationStub)generation.get()).getPiecesBuilder();
         StructureStart testStart = new StructureStart(this, sourceChunkPos, references, builder.build());
         if (testStart.isValid()) {
            if (profiled != null) {
               profiled.finish(true);
            }

            return testStart;
         }
      }

      if (profiled != null) {
         profiled.finish(false);
      }

      return StructureStart.INVALID_START;
   }

   protected static Optional<GenerationStub> onTopOfChunkCenter(final GenerationContext context, final Heightmap.Types heightmap, final Consumer<StructurePiecesBuilder> generator) {
      return !context.couldValidBiomeExistOnTopOfChunkCenter() ? Optional.empty() : onTopOfChunkCenterWithoutBiomeCheck(context, heightmap, generator);
   }

   protected static Optional<GenerationStub> onTopOfChunkCenterWithoutBiomeCheck(final GenerationContext context, final Heightmap.Types heightmap, final Consumer<StructurePiecesBuilder> generator) {
      ChunkPos chunkPos = context.chunkPos();
      int blockX = chunkPos.getMiddleBlockX();
      int blockZ = chunkPos.getMiddleBlockZ();
      int blockY = context.chunkGenerator().getFirstOccupiedHeight(blockX, blockZ, heightmap, context.heightAccessor(), context.randomState());
      return Optional.of(new GenerationStub(new BlockPos(blockX, blockY, blockZ), generator));
   }

   public void afterPlace(final WorldGenLevel level, final StructureManager structureManager, final ChunkGenerator generator, final RandomSource random, final BoundingBox chunkBB, final ChunkPos chunkPos, final PiecesContainer pieces) {
   }

   private static int[] getCornerHeights(final GenerationContext context, final int minX, final int sizeX, final int minZ, final int sizeZ) {
      ChunkGenerator chunkGenerator = context.chunkGenerator();
      LevelHeightAccessor heightAccessor = context.heightAccessor();
      RandomState randomState = context.randomState();
      return new int[]{chunkGenerator.getFirstOccupiedHeight(minX, minZ, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState), chunkGenerator.getFirstOccupiedHeight(minX, minZ + sizeZ, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState), chunkGenerator.getFirstOccupiedHeight(minX + sizeX, minZ, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState), chunkGenerator.getFirstOccupiedHeight(minX + sizeX, minZ + sizeZ, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState)};
   }

   public static int getMeanFirstOccupiedHeight(final GenerationContext context, final int minX, final int sizeX, final int minZ, final int sizeZ) {
      int[] cornerHeights = getCornerHeights(context, minX, sizeX, minZ, sizeZ);
      return (cornerHeights[0] + cornerHeights[1] + cornerHeights[2] + cornerHeights[3]) / 4;
   }

   protected static int getLowestY(final GenerationContext context, final int sizeX, final int sizeZ) {
      ChunkPos chunkPos = context.chunkPos();
      int minX = chunkPos.getMinBlockX();
      int minZ = chunkPos.getMinBlockZ();
      return getLowestY(context, minX, minZ, sizeX, sizeZ);
   }

   protected static int getLowestY(final GenerationContext context, final int minX, final int minZ, final int sizeX, final int sizeZ) {
      int[] cornerHeights = getCornerHeights(context, minX, sizeX, minZ, sizeZ);
      return Math.min(Math.min(cornerHeights[0], cornerHeights[1]), Math.min(cornerHeights[2], cornerHeights[3]));
   }

   /** @deprecated */
   @Deprecated
   protected BlockPos getLowestYIn5by5Box(final GenerationContext context, final int blockX, final int blockZ, final Rotation rotation) {
      int offsetX = 5;
      int offsetZ = 5;
      if (rotation == Rotation.CLOCKWISE_90) {
         offsetX = -5;
      } else if (rotation == Rotation.CLOCKWISE_180) {
         offsetX = -5;
         offsetZ = -5;
      } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
         offsetZ = -5;
      }

      return new BlockPos(blockX, getLowestY(context, blockX, blockZ, offsetX, offsetZ), blockZ);
   }

   protected abstract Optional<GenerationStub> findGenerationPoint(final GenerationContext context);

   public Optional<GenerationStub> findValidGenerationPoint(final GenerationContext context) {
      Optional var10000 = this.findGenerationPoint(context);
      Objects.requireNonNull(context);
      return var10000.filter(context::isValidBiome);
   }

   public abstract StructureType<?> type();

   static {
      DIRECT_CODEC = BuiltInRegistries.STRUCTURE_TYPE.byNameCodec().dispatch(Structure::type, StructureType::codec);
      CODEC = RegistryCodecs.holder(Registries.STRUCTURE, DIRECT_CODEC);
   }

   public static record StructureSettings(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation) {
      private static final StructureSettings DEFAULT;
      public static final MapCodec<StructureSettings> CODEC;

      public StructureSettings(final HolderSet<Biome> biomes) {
         this(biomes, DEFAULT.spawnOverrides, DEFAULT.step, DEFAULT.terrainAdaptation);
      }

      public StructureSettings {
         super();
      }

      static {
         DEFAULT = new StructureSettings(HolderSet.empty(), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE);
         CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.holderSet(Registries.BIOME).fieldOf("biomes").forGetter(StructureSettings::biomes), Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, StringRepresentable.keys(MobCategory.values())).fieldOf("spawn_overrides").forGetter(StructureSettings::spawnOverrides), GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(StructureSettings::step), TerrainAdjustment.CODEC.optionalFieldOf("terrain_adaptation", DEFAULT.terrainAdaptation).forGetter(StructureSettings::terrainAdaptation)).apply(i, StructureSettings::new));
      }

      public static class Builder {
         private final HolderSet<Biome> biomes;
         private Map<MobCategory, StructureSpawnOverride> spawnOverrides;
         private GenerationStep.Decoration step;
         private TerrainAdjustment terrainAdaption;

         public Builder(final HolderSet<Biome> biomes) {
            super();
            this.spawnOverrides = Structure.StructureSettings.DEFAULT.spawnOverrides;
            this.step = Structure.StructureSettings.DEFAULT.step;
            this.terrainAdaption = Structure.StructureSettings.DEFAULT.terrainAdaptation;
            this.biomes = biomes;
         }

         public Builder spawnOverrides(final Map<MobCategory, StructureSpawnOverride> spawnOverrides) {
            this.spawnOverrides = spawnOverrides;
            return this;
         }

         public Builder generationStep(final GenerationStep.Decoration step) {
            this.step = step;
            return this;
         }

         public Builder terrainAdapation(final TerrainAdjustment terrainAdaption) {
            this.terrainAdaption = terrainAdaption;
            return this;
         }

         public StructureSettings build() {
            return new StructureSettings(this.biomes, this.spawnOverrides, this.step, this.terrainAdaption);
         }
      }
   }

   public static record GenerationContext(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, Climate.Sampler climateSampler, BiomeResolver biomeResolver, RandomState randomState, StructureTemplateManager structureTemplateManager, WorldgenRandom random, long seed, ChunkPos chunkPos, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome) {
      public GenerationContext(final RegistryAccess registryAccess, final ChunkGenerator chunkGenerator, final BiomeSource biomeSource, final Climate.Sampler climateSampler, final RandomState randomState, final StructureTemplateManager structureTemplateManager, final long seed, final ChunkPos chunkPos, final LevelHeightAccessor heightAccessor, final Predicate<Holder<Biome>> validBiome) {
         this(registryAccess, chunkGenerator, biomeSource, climateSampler, biomeSource.createResolver(climateSampler), randomState, structureTemplateManager, makeRandom(seed, chunkPos), seed, chunkPos, heightAccessor, validBiome);
      }

      public GenerationContext {
         super();
      }

      private static WorldgenRandom makeRandom(final long seed, final ChunkPos chunkPos) {
         WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
         random.setLargeFeatureSeed(seed, chunkPos.x(), chunkPos.z());
         return random;
      }

      public boolean isValidBiome(final GenerationStub stub) {
         BlockPos startPos = stub.position();
         return this.validBiome.test(this.biomeResolver.getNoiseBiome(QuartPos.fromBlock(startPos.getX()), QuartPos.fromBlock(startPos.getY()), QuartPos.fromBlock(startPos.getZ())));
      }

      public boolean couldStructureExistInColumn(final int blockX, final int blockZ, final int minBlockY, final int maxBlockY) {
         int quartX = QuartPos.fromBlock(blockX);
         int quartZ = QuartPos.fromBlock(blockZ);
         int minQuartY = QuartPos.fromBlock(minBlockY);
         int maxQuartY = QuartPos.fromBlock(maxBlockY);
         BiomeResolver columnResolver = this.biomeSource.createResolverForChunk(this.climateSampler, quartX, minQuartY, quartZ, 1, maxQuartY - minQuartY + 1, 1);

         for(int quartY = minQuartY; quartY <= maxQuartY; ++quartY) {
            if (this.validBiome.test(columnResolver.getNoiseBiome(quartX, quartY, quartZ))) {
               return true;
            }
         }

         return false;
      }

      public boolean couldValidBiomeExistInTerrainColumn(final int blockX, final int blockZ) {
         return this.couldStructureExistInColumn(blockX, blockZ, this.heightAccessor.getMinY() - 1, this.heightAccessor.getMaxY());
      }

      public boolean couldValidBiomeExistOnTopOfChunkCenter() {
         return this.couldValidBiomeExistInTerrainColumn(this.chunkPos.getMiddleBlockX(), this.chunkPos.getMiddleBlockZ());
      }
   }

   public static record GenerationStub(BlockPos position, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator) {
      public GenerationStub(final BlockPos position, final Consumer<StructurePiecesBuilder> generator) {
         this(position, Either.left(generator));
      }

      public GenerationStub {
         super();
      }

      public StructurePiecesBuilder getPiecesBuilder() {
         return (StructurePiecesBuilder)this.generator.map((pieceGenerator) -> {
            StructurePiecesBuilder newBuilder = new StructurePiecesBuilder();
            pieceGenerator.accept(newBuilder);
            return newBuilder;
         }, (previousBuilder) -> previousBuilder);
      }
   }
}
