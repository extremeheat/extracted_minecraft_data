package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SimpleBlockConfiguration(BlockStateProvider toPlace, boolean scheduleTick) implements FeatureConfiguration {
   public static final Codec<SimpleBlockConfiguration> CODEC = RecordCodecBuilder.create((var0) -> var0.group(BlockStateProvider.CODEC.fieldOf("to_place").forGetter((var0x) -> var0x.toPlace), Codec.BOOL.optionalFieldOf("schedule_tick", false).forGetter((var0x) -> var0x.scheduleTick)).apply(var0, SimpleBlockConfiguration::new));

   public SimpleBlockConfiguration(BlockStateProvider var1) {
      this(var1, false);
   }

   public SimpleBlockConfiguration(BlockStateProvider var1, boolean var2) {
      super();
      this.toPlace = var1;
      this.scheduleTick = var2;
   }
}
