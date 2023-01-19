package net.minecraft.world.level.levelgen.structure.pieces;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@FunctionalInterface
public interface PieceGeneratorSupplier<C extends FeatureConfiguration> {
   Optional<PieceGenerator<C>> createGenerator(PieceGeneratorSupplier.Context<C> var1);

   static <C extends FeatureConfiguration> PieceGeneratorSupplier<C> simple(Predicate<PieceGeneratorSupplier.Context<C>> var0, PieceGenerator<C> var1) {
      Optional var2 = Optional.of(var1);
      return var2x -> var0.test(var2x) ? var2 : Optional.empty();
   }

   static <C extends FeatureConfiguration> Predicate<PieceGeneratorSupplier.Context<C>> checkForBiomeOnTop(Heightmap.Types var0) {
      return var1 -> var1.validBiomeOnTop(var0);
   }

   public static record Context<C extends FeatureConfiguration>(
      ChunkGenerator a,
      BiomeSource b,
      RandomState c,
      long d,
      ChunkPos e,
      C f,
      LevelHeightAccessor g,
      Predicate<Holder<Biome>> h,
      StructureTemplateManager i,
      RegistryAccess j
   ) {
      private final ChunkGenerator chunkGenerator;
      private final BiomeSource biomeSource;
      private final RandomState randomState;
      private final long seed;
      private final ChunkPos chunkPos;
      private final C config;
      private final LevelHeightAccessor heightAccessor;
      private final Predicate<Holder<Biome>> validBiome;
      private final StructureTemplateManager structureTemplateManager;
      private final RegistryAccess registryAccess;

      public Context(
         ChunkGenerator var1,
         BiomeSource var2,
         RandomState var3,
         long var4,
         ChunkPos var6,
         C var7,
         LevelHeightAccessor var8,
         Predicate<Holder<Biome>> var9,
         StructureTemplateManager var10,
         RegistryAccess var11
      ) {
         super();
         this.chunkGenerator = var1;
         this.biomeSource = var2;
         this.randomState = var3;
         this.seed = var4;
         this.chunkPos = var6;
         this.config = var7;
         this.heightAccessor = var8;
         this.validBiome = var9;
         this.structureTemplateManager = var10;
         this.registryAccess = var11;
      }

      public boolean validBiomeOnTop(Heightmap.Types var1) {
         int var2 = this.chunkPos.getMiddleBlockX();
         int var3 = this.chunkPos.getMiddleBlockZ();
         int var4 = this.chunkGenerator.getFirstOccupiedHeight(var2, var3, var1, this.heightAccessor, this.randomState);
         Holder var5 = this.chunkGenerator
            .getBiomeSource()
            .getNoiseBiome(QuartPos.fromBlock(var2), QuartPos.fromBlock(var4), QuartPos.fromBlock(var3), this.randomState.sampler());
         return this.validBiome.test(var5);
      }
   }
}
