package net.minecraft.world.level.levelgen.structure;

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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.levelgen.RandomState;
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

   public static <S extends Structure> MapCodec<S> simpleCodec(Function<Structure.StructureSettings, S> var0) {
      return RecordCodecBuilder.mapCodec(var1 -> var1.group(settingsCodec(var1)).apply(var1, var0));
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

   public static int getMeanFirstOccupiedHeight(Structure.GenerationContext var0, int var1, int var2, int var3, int var4) {
      int[] var5 = getCornerHeights(var0, var1, var2, var3, var4);
      return (var5[0] + var5[1] + var5[2] + var5[3]) / 4;
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
