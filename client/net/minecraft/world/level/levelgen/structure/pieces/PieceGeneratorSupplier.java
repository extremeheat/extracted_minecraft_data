package net.minecraft.world.level.levelgen.structure.pieces;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

@FunctionalInterface
public interface PieceGeneratorSupplier<C extends FeatureConfiguration> {
   Optional<PieceGenerator<C>> createGenerator(PieceGeneratorSupplier.Context<C> var1);

   static <C extends FeatureConfiguration> PieceGeneratorSupplier<C> simple(Predicate<PieceGeneratorSupplier.Context<C>> var0, PieceGenerator<C> var1) {
      Optional var2 = Optional.of(var1);
      return (var2x) -> {
         return var0.test(var2x) ? var2 : Optional.empty();
      };
   }

   static <C extends FeatureConfiguration> Predicate<PieceGeneratorSupplier.Context<C>> checkForBiomeOnTop(Heightmap.Types var0) {
      return (var1) -> {
         return var1.validBiomeOnTop(var0);
      };
   }

   public static record Context<C extends FeatureConfiguration>(ChunkGenerator a, BiomeSource b, long c, ChunkPos d, C e, LevelHeightAccessor f, Predicate<Biome> g, StructureManager h, RegistryAccess i) {
      private final ChunkGenerator chunkGenerator;
      private final BiomeSource biomeSource;
      private final long seed;
      private final ChunkPos chunkPos;
      private final C config;
      private final LevelHeightAccessor heightAccessor;
      private final Predicate<Biome> validBiome;
      private final StructureManager structureManager;
      private final RegistryAccess registryAccess;

      public Context(ChunkGenerator var1, BiomeSource var2, long var3, ChunkPos var5, C var6, LevelHeightAccessor var7, Predicate<Biome> var8, StructureManager var9, RegistryAccess var10) {
         super();
         this.chunkGenerator = var1;
         this.biomeSource = var2;
         this.seed = var3;
         this.chunkPos = var5;
         this.config = var6;
         this.heightAccessor = var7;
         this.validBiome = var8;
         this.structureManager = var9;
         this.registryAccess = var10;
      }

      public boolean validBiomeOnTop(Heightmap.Types var1) {
         int var2 = this.chunkPos.getMiddleBlockX();
         int var3 = this.chunkPos.getMiddleBlockZ();
         int var4 = this.chunkGenerator.getFirstOccupiedHeight(var2, var3, var1, this.heightAccessor);
         Biome var5 = this.chunkGenerator.getNoiseBiome(QuartPos.fromBlock(var2), QuartPos.fromBlock(var4), QuartPos.fromBlock(var3));
         return this.validBiome.test(var5);
      }

      public int[] getCornerHeights(int var1, int var2, int var3, int var4) {
         return new int[]{this.chunkGenerator.getFirstOccupiedHeight(var1, var3, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(var1, var3 + var4, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(var1 + var2, var3, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(var1 + var2, var3 + var4, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor)};
      }

      public int getLowestY(int var1, int var2) {
         int var3 = this.chunkPos.getMinBlockX();
         int var4 = this.chunkPos.getMinBlockZ();
         int[] var5 = this.getCornerHeights(var3, var1, var4, var2);
         return Math.min(Math.min(var5[0], var5[1]), Math.min(var5[2], var5[3]));
      }

      public ChunkGenerator chunkGenerator() {
         return this.chunkGenerator;
      }

      public BiomeSource biomeSource() {
         return this.biomeSource;
      }

      public long seed() {
         return this.seed;
      }

      public ChunkPos chunkPos() {
         return this.chunkPos;
      }

      public C config() {
         return this.config;
      }

      public LevelHeightAccessor heightAccessor() {
         return this.heightAccessor;
      }

      public Predicate<Biome> validBiome() {
         return this.validBiome;
      }

      public StructureManager structureManager() {
         return this.structureManager;
      }

      public RegistryAccess registryAccess() {
         return this.registryAccess;
      }
   }
}
