package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
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

   public static <S extends Structure> RecordCodecBuilder<S, StructureSettings> settingsCodec(RecordCodecBuilder.Instance<S> var0) {
      return Structure.StructureSettings.CODEC.forGetter((var0x) -> {
         return var0x.settings;
      });
   }

   public static <S extends Structure> MapCodec<S> simpleCodec(Function<StructureSettings, S> var0) {
      return RecordCodecBuilder.mapCodec((var1) -> {
         return var1.group(settingsCodec(var1)).apply(var1, var0);
      });
   }

   protected Structure(StructureSettings var1) {
      super();
      this.settings = var1;
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

   public BoundingBox adjustBoundingBox(BoundingBox var1) {
      return this.terrainAdaptation() != TerrainAdjustment.NONE ? var1.inflatedBy(12) : var1;
   }

   public StructureStart generate(RegistryAccess var1, ChunkGenerator var2, BiomeSource var3, RandomState var4, StructureTemplateManager var5, long var6, ChunkPos var8, int var9, LevelHeightAccessor var10, Predicate<Holder<Biome>> var11) {
      GenerationContext var12 = new GenerationContext(var1, var2, var3, var4, var5, var6, var8, var10, var11);
      Optional var13 = this.findValidGenerationPoint(var12);
      if (var13.isPresent()) {
         StructurePiecesBuilder var14 = ((GenerationStub)var13.get()).getPiecesBuilder();
         StructureStart var15 = new StructureStart(this, var8, var9, var14.build());
         if (var15.isValid()) {
            return var15;
         }
      }

      return StructureStart.INVALID_START;
   }

   protected static Optional<GenerationStub> onTopOfChunkCenter(GenerationContext var0, Heightmap.Types var1, Consumer<StructurePiecesBuilder> var2) {
      ChunkPos var3 = var0.chunkPos();
      int var4 = var3.getMiddleBlockX();
      int var5 = var3.getMiddleBlockZ();
      int var6 = var0.chunkGenerator().getFirstOccupiedHeight(var4, var5, var1, var0.heightAccessor(), var0.randomState());
      return Optional.of(new GenerationStub(new BlockPos(var4, var6, var5), var2));
   }

   private static boolean isValidBiome(GenerationStub var0, GenerationContext var1) {
      BlockPos var2 = var0.position();
      return var1.validBiome.test(var1.chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(var2.getX()), QuartPos.fromBlock(var2.getY()), QuartPos.fromBlock(var2.getZ()), var1.randomState.sampler()));
   }

   public void afterPlace(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, PiecesContainer var7) {
   }

   private static int[] getCornerHeights(GenerationContext var0, int var1, int var2, int var3, int var4) {
      ChunkGenerator var5 = var0.chunkGenerator();
      LevelHeightAccessor var6 = var0.heightAccessor();
      RandomState var7 = var0.randomState();
      return new int[]{var5.getFirstOccupiedHeight(var1, var3, Heightmap.Types.WORLD_SURFACE_WG, var6, var7), var5.getFirstOccupiedHeight(var1, var3 + var4, Heightmap.Types.WORLD_SURFACE_WG, var6, var7), var5.getFirstOccupiedHeight(var1 + var2, var3, Heightmap.Types.WORLD_SURFACE_WG, var6, var7), var5.getFirstOccupiedHeight(var1 + var2, var3 + var4, Heightmap.Types.WORLD_SURFACE_WG, var6, var7)};
   }

   public static int getMeanFirstOccupiedHeight(GenerationContext var0, int var1, int var2, int var3, int var4) {
      int[] var5 = getCornerHeights(var0, var1, var2, var3, var4);
      return (var5[0] + var5[1] + var5[2] + var5[3]) / 4;
   }

   protected static int getLowestY(GenerationContext var0, int var1, int var2) {
      ChunkPos var3 = var0.chunkPos();
      int var4 = var3.getMinBlockX();
      int var5 = var3.getMinBlockZ();
      return getLowestY(var0, var4, var5, var1, var2);
   }

   protected static int getLowestY(GenerationContext var0, int var1, int var2, int var3, int var4) {
      int[] var5 = getCornerHeights(var0, var1, var3, var2, var4);
      return Math.min(Math.min(var5[0], var5[1]), Math.min(var5[2], var5[3]));
   }

   /** @deprecated */
   @Deprecated
   protected BlockPos getLowestYIn5by5BoxOffset7Blocks(GenerationContext var1, Rotation var2) {
      byte var3 = 5;
      byte var4 = 5;
      if (var2 == Rotation.CLOCKWISE_90) {
         var3 = -5;
      } else if (var2 == Rotation.CLOCKWISE_180) {
         var3 = -5;
         var4 = -5;
      } else if (var2 == Rotation.COUNTERCLOCKWISE_90) {
         var4 = -5;
      }

      ChunkPos var5 = var1.chunkPos();
      int var6 = var5.getBlockX(7);
      int var7 = var5.getBlockZ(7);
      return new BlockPos(var6, getLowestY(var1, var6, var7, var3, var4), var7);
   }

   protected abstract Optional<GenerationStub> findGenerationPoint(GenerationContext var1);

   public Optional<GenerationStub> findValidGenerationPoint(GenerationContext var1) {
      return this.findGenerationPoint(var1).filter((var1x) -> {
         return isValidBiome(var1x, var1);
      });
   }

   public abstract StructureType<?> type();

   static {
      DIRECT_CODEC = BuiltInRegistries.STRUCTURE_TYPE.byNameCodec().dispatch(Structure::type, StructureType::codec);
      CODEC = RegistryFileCodec.create(Registries.STRUCTURE, DIRECT_CODEC);
   }

   public static record StructureSettings(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation) {
      final HolderSet<Biome> biomes;
      final Map<MobCategory, StructureSpawnOverride> spawnOverrides;
      final GenerationStep.Decoration step;
      final TerrainAdjustment terrainAdaptation;
      public static final MapCodec<StructureSettings> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(StructureSettings::biomes), Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, StringRepresentable.keys(MobCategory.values())).fieldOf("spawn_overrides").forGetter(StructureSettings::spawnOverrides), GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(StructureSettings::step), TerrainAdjustment.CODEC.optionalFieldOf("terrain_adaptation", TerrainAdjustment.NONE).forGetter(StructureSettings::terrainAdaptation)).apply(var0, StructureSettings::new);
      });

      public StructureSettings(HolderSet<Biome> var1, Map<MobCategory, StructureSpawnOverride> var2, GenerationStep.Decoration var3, TerrainAdjustment var4) {
         super();
         this.biomes = var1;
         this.spawnOverrides = var2;
         this.step = var3;
         this.terrainAdaptation = var4;
      }

      public HolderSet<Biome> biomes() {
         return this.biomes;
      }

      public Map<MobCategory, StructureSpawnOverride> spawnOverrides() {
         return this.spawnOverrides;
      }

      public GenerationStep.Decoration step() {
         return this.step;
      }

      public TerrainAdjustment terrainAdaptation() {
         return this.terrainAdaptation;
      }
   }

   public static record GenerationContext(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, StructureTemplateManager structureTemplateManager, WorldgenRandom random, long seed, ChunkPos chunkPos, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome) {
      final ChunkGenerator chunkGenerator;
      final RandomState randomState;
      final Predicate<Holder<Biome>> validBiome;

      public GenerationContext(RegistryAccess var1, ChunkGenerator var2, BiomeSource var3, RandomState var4, StructureTemplateManager var5, long var6, ChunkPos var8, LevelHeightAccessor var9, Predicate<Holder<Biome>> var10) {
         this(var1, var2, var3, var4, var5, makeRandom(var6, var8), var6, var8, var9, var10);
      }

      public GenerationContext(RegistryAccess var1, ChunkGenerator var2, BiomeSource var3, RandomState var4, StructureTemplateManager var5, WorldgenRandom var6, long var7, ChunkPos var9, LevelHeightAccessor var10, Predicate<Holder<Biome>> var11) {
         super();
         this.registryAccess = var1;
         this.chunkGenerator = var2;
         this.biomeSource = var3;
         this.randomState = var4;
         this.structureTemplateManager = var5;
         this.random = var6;
         this.seed = var7;
         this.chunkPos = var9;
         this.heightAccessor = var10;
         this.validBiome = var11;
      }

      private static WorldgenRandom makeRandom(long var0, ChunkPos var2) {
         WorldgenRandom var3 = new WorldgenRandom(new LegacyRandomSource(0L));
         var3.setLargeFeatureSeed(var0, var2.x, var2.z);
         return var3;
      }

      public RegistryAccess registryAccess() {
         return this.registryAccess;
      }

      public ChunkGenerator chunkGenerator() {
         return this.chunkGenerator;
      }

      public BiomeSource biomeSource() {
         return this.biomeSource;
      }

      public RandomState randomState() {
         return this.randomState;
      }

      public StructureTemplateManager structureTemplateManager() {
         return this.structureTemplateManager;
      }

      public WorldgenRandom random() {
         return this.random;
      }

      public long seed() {
         return this.seed;
      }

      public ChunkPos chunkPos() {
         return this.chunkPos;
      }

      public LevelHeightAccessor heightAccessor() {
         return this.heightAccessor;
      }

      public Predicate<Holder<Biome>> validBiome() {
         return this.validBiome;
      }
   }

   public static record GenerationStub(BlockPos position, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator) {
      public GenerationStub(BlockPos var1, Consumer<StructurePiecesBuilder> var2) {
         this(var1, Either.left(var2));
      }

      public GenerationStub(BlockPos var1, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> var2) {
         super();
         this.position = var1;
         this.generator = var2;
      }

      public StructurePiecesBuilder getPiecesBuilder() {
         return (StructurePiecesBuilder)this.generator.map((var0) -> {
            StructurePiecesBuilder var1 = new StructurePiecesBuilder();
            var0.accept(var1);
            return var1;
         }, (var0) -> {
            return var0;
         });
      }

      public BlockPos position() {
         return this.position;
      }

      public Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator() {
         return this.generator;
      }
   }
}
