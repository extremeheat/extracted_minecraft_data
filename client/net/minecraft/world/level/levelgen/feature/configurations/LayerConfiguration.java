package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public class LayerConfiguration implements FeatureConfiguration {
   public static final Codec<LayerConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(0, DimensionType.Y_SIZE).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      }), BlockState.CODEC.fieldOf("state").forGetter((var0x) -> {
         return var0x.state;
      })).apply(var0, LayerConfiguration::new);
   });
   public final int height;
   public final BlockState state;

   public LayerConfiguration(int var1, BlockState var2) {
      super();
      this.height = var1;
      this.state = var2;
   }
}
