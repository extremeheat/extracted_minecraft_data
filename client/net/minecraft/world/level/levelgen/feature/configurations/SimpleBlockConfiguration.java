package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SimpleBlockConfiguration(BlockStateProvider b) implements FeatureConfiguration {
   private final BlockStateProvider toPlace;
   public static final Codec<SimpleBlockConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("to_place").forGetter((var0x) -> {
         return var0x.toPlace;
      })).apply(var0, SimpleBlockConfiguration::new);
   });

   public SimpleBlockConfiguration(BlockStateProvider var1) {
      super();
      this.toPlace = var1;
   }

   public BlockStateProvider toPlace() {
      return this.toPlace;
   }
}
