package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
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
   public static final Codec<Structure> DIRECT_CODEC = BuiltInRegistries.STRUCTURE_TYPE.byNameCodec().dispatch(Structure::type, StructureType::codec);
   public static final Codec<Holder<Structure>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE, DIRECT_CODEC);
   protected final Structure.StructureSettings settings;

   public static <S extends Structure> RecordCodecBuilder<S, Structure.StructureSettings> settingsCodec(Instance<S> var0) {
      return Structure.StructureSettings.CODEC.forGetter(var0x -> var0x.settings);
   }

   public static <S extends Structure> Codec<S> simpleCodec(Function<Structure.StructureSettings, S> var0) {
      return RecordCodecBuilder.create(var1 -> var1.group(settingsCodec(var1)).apply(var1, var0));
   }

   protected Structure(Structure.StructureSettings var1) {
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

   public StructureStart generate(
      RegistryAccess var1,
      ChunkGenerator var2,
      BiomeSource var3,
      RandomState var4,
      StructureTemplateManager var5,
      long var6,
      ChunkPos var8,
      int var9,
      LevelHeightAccessor var10,
      Predicate<Holder<Biome>> var11
   ) {
      Structure.GenerationContext var12 = new Structure.GenerationContext(var1, var2, var3, var4, var5, var6, var8, var10, var11);
      Optional var13 = this.findValidGenerationPoint(var12);
      if (var13.isPresent()) {
         StructurePiecesBuilder var14 = ((Structure.GenerationStub)var13.get()).getPiecesBuilder();
         StructureStart var15 = new StructureStart(this, var8, var9, var14.build());
         if (var15.isValid()) {
            return var15;
         }
      }

      return StructureStart.INVALID_START;
   }

   protected static Optional<Structure.GenerationStub> onTopOfChunkCenter(
      Structure.GenerationContext var0, Heightmap.Types var1, Consumer<StructurePiecesBuilder> var2
   ) {
      ChunkPos var3 = var0.chunkPos();
      int var4 = var3.getMiddleBlockX();
      int var5 = var3.getMiddleBlockZ();
      int var6 = var0.chunkGenerator().getFirstOccupiedHeight(var4, var5, var1, var0.heightAccessor(), var0.randomState());
      return Optional.of(new Structure.GenerationStub(new BlockPos(var4, var6, var5), var2));
   }

   private static boolean isValidBiome(Structure.GenerationStub var0, Structure.GenerationContext var1) {
      BlockPos var2 = var0.position();
      return var1.validBiome
         .test(
            var1.chunkGenerator
               .getBiomeSource()
               .getNoiseBiome(QuartPos.fromBlock(var2.getX()), QuartPos.fromBlock(var2.getY()), QuartPos.fromBlock(var2.getZ()), var1.randomState.sampler())
         );
   }

   public void afterPlace(
      WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, PiecesContainer var7
   ) {
   }

   private static int[] getCornerHeights(Structure.GenerationContext var0, int var1, int var2, int var3, int var4) {
      ChunkGenerator var5 = var0.chunkGenerator();
      LevelHeightAccessor var6 = var0.heightAccessor();
      RandomState var7 = var0.randomState();
      return new int[]{
         var5.getFirstOccupiedHeight(var1, var3, Heightmap.Types.WORLD_SURFACE_WG, var6, var7),
         var5.getFirstOccupiedHeight(var1, var3 + var4, Heightmap.Types.WORLD_SURFACE_WG, var6, var7),
         var5.getFirstOccupiedHeight(var1 + var2, var3, Heightmap.Types.WORLD_SURFACE_WG, var6, var7),
         var5.getFirstOccupiedHeight(var1 + var2, var3 + var4, Heightmap.Types.WORLD_SURFACE_WG, var6, var7)
      };
   }

   protected static int getLowestY(Structure.GenerationContext var0, int var1, int var2) {
      ChunkPos var3 = var0.chunkPos();
      int var4 = var3.getMinBlockX();
      int var5 = var3.getMinBlockZ();
      return getLowestY(var0, var4, var5, var1, var2);
   }

   protected static int getLowestY(Structure.GenerationContext var0, int var1, int var2, int var3, int var4) {
      int[] var5 = getCornerHeights(var0, var1, var3, var2, var4);
      return Math.min(Math.min(var5[0], var5[1]), Math.min(var5[2], var5[3]));
   }

   @Deprecated
   protected BlockPos getLowestYIn5by5BoxOffset7Blocks(Structure.GenerationContext var1, Rotation var2) {
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

   protected abstract Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1);

   public Optional<Structure.GenerationStub> findValidGenerationPoint(Structure.GenerationContext var1) {
      return this.findGenerationPoint(var1).filter(var1x -> isValidBiome(var1x, var1));
   }

   public abstract StructureType<?> type();

   public static record GenerationContext(
      RegistryAccess a,
      ChunkGenerator b,
      BiomeSource c,
      RandomState d,
      StructureTemplateManager e,
      WorldgenRandom f,
      long g,
      ChunkPos h,
      LevelHeightAccessor i,
      Predicate<Holder<Biome>> j
   ) {
      private final RegistryAccess registryAccess;
      final ChunkGenerator chunkGenerator;
      private final BiomeSource biomeSource;
      final RandomState randomState;
      private final StructureTemplateManager structureTemplateManager;
      private final WorldgenRandom random;
      private final long seed;
      private final ChunkPos chunkPos;
      private final LevelHeightAccessor heightAccessor;
      final Predicate<Holder<Biome>> validBiome;

      public GenerationContext(
         RegistryAccess var1,
         ChunkGenerator var2,
         BiomeSource var3,
         RandomState var4,
         StructureTemplateManager var5,
         long var6,
         ChunkPos var8,
         LevelHeightAccessor var9,
         Predicate<Holder<Biome>> var10
      ) {
         this(var1, var2, var3, var4, var5, makeRandom(var6, var8), var6, var8, var9, var10);
      }

      public GenerationContext(
         RegistryAccess var1,
         ChunkGenerator var2,
         BiomeSource var3,
         RandomState var4,
         StructureTemplateManager var5,
         WorldgenRandom var6,
         long var7,
         ChunkPos var9,
         LevelHeightAccessor var10,
         Predicate<Holder<Biome>> var11
      ) {
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
   }

   public static record GenerationStub(BlockPos a, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> b) {
      private final BlockPos position;
      private final Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator;

      public GenerationStub(BlockPos var1, Consumer<StructurePiecesBuilder> var2) {
         this(var1, Either.left(var2));
      }

      public GenerationStub(BlockPos var1, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> var2) {
         super();
         this.position = var1;
         this.generator = var2;
      }

      public StructurePiecesBuilder getPiecesBuilder() {
         return (StructurePiecesBuilder)this.generator.map(var0 -> {
            StructurePiecesBuilder var1 = new StructurePiecesBuilder();
            var0.accept(var1);
            return var1;
         }, var0 -> var0);
      }
   }

   public static record StructureSettings(HolderSet<Biome> b, Map<MobCategory, StructureSpawnOverride> c, GenerationStep.Decoration d, TerrainAdjustment e) {
      final HolderSet<Biome> biomes;
      final Map<MobCategory, StructureSpawnOverride> spawnOverrides;
      final GenerationStep.Decoration step;
      final TerrainAdjustment terrainAdaptation;
      public static final MapCodec<Structure.StructureSettings> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(Structure.StructureSettings::biomes),
                  Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, StringRepresentable.keys(MobCategory.values()))
                     .fieldOf("spawn_overrides")
                     .forGetter(Structure.StructureSettings::spawnOverrides),
                  GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(Structure.StructureSettings::step),
                  TerrainAdjustment.CODEC
                     .optionalFieldOf("terrain_adaptation", TerrainAdjustment.NONE)
                     .forGetter(Structure.StructureSettings::terrainAdaptation)
               )
               .apply(var0, Structure.StructureSettings::new)
      );

      public StructureSettings(HolderSet<Biome> var1, Map<MobCategory, StructureSpawnOverride> var2, GenerationStep.Decoration var3, TerrainAdjustment var4) {
         super();
         this.biomes = var1;
         this.spawnOverrides = var2;
         this.step = var3;
         this.terrainAdaptation = var4;
      }
   }
}
