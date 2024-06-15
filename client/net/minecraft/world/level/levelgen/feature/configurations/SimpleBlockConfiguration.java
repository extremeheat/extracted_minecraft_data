package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SimpleBlockConfiguration(BlockStateProvider toPlace) implements FeatureConfiguration {
   public static final Codec<SimpleBlockConfiguration> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(BlockStateProvider.CODEC.fieldOf("to_place").forGetter(var0x -> var0x.toPlace)).apply(var0, SimpleBlockConfiguration::new)
   );

   public SimpleBlockConfiguration(BlockStateProvider toPlace) {
      super();
      this.toPlace = toPlace;
   }
}
