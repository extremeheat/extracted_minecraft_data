package net.minecraft.world.level.levelgen.feature;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FeaturePlaceContext<FC extends FeatureConfiguration> {
   private final Optional<ConfiguredFeature<?, ?>> topFeature;
   private final WorldGenLevel level;
   private final ChunkGenerator chunkGenerator;
   private final RandomSource random;
   private final BlockPos origin;
   private final FC config;

   public FeaturePlaceContext(Optional<ConfiguredFeature<?, ?>> var1, WorldGenLevel var2, ChunkGenerator var3, RandomSource var4, BlockPos var5, FC var6) {
      super();
      this.topFeature = var1;
      this.level = var2;
      this.chunkGenerator = var3;
      this.random = var4;
      this.origin = var5;
      this.config = var6;
   }

   public Optional<ConfiguredFeature<?, ?>> topFeature() {
      return this.topFeature;
   }

   public WorldGenLevel level() {
      return this.level;
   }

   public ChunkGenerator chunkGenerator() {
      return this.chunkGenerator;
   }

   public RandomSource random() {
      return this.random;
   }

   public BlockPos origin() {
      return this.origin;
   }

   public FC config() {
      return this.config;
   }
}
