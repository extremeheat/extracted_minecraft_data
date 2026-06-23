package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record NoOpFeature() implements Feature {
   public static final NoOpFeature INSTANCE = new NoOpFeature();
   public static final MapCodec<NoOpFeature> CODEC;

   public NoOpFeature() {
      super();
   }

   public MapCodec<NoOpFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      return true;
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
