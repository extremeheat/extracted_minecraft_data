package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record SequenceFeature(HolderSet<PlacedFeature> features) implements Feature {
   public static final MapCodec<SequenceFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.nonEmptyHolderSet(PlacedFeature.LIST_CODEC).fieldOf("features").forGetter(SequenceFeature::features)).apply(i, SequenceFeature::new));

   public SequenceFeature {
      super();
   }

   public MapCodec<SequenceFeature> codec() {
      return CODEC;
   }

   public Stream<Holder<Feature>> getSubFeatures() {
      return this.features.stream().flatMap((f) -> ((PlacedFeature)f.value()).getFeatures());
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      for(Holder<PlacedFeature> feature : this.features) {
         if (!((PlacedFeature)feature.value()).place(level, chunkGenerator, random, origin)) {
            return false;
         }
      }

      return true;
   }
}
