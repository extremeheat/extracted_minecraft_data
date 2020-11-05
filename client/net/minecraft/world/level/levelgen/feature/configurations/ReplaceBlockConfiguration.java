package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;

public class ReplaceBlockConfiguration implements FeatureConfiguration {
   public static final Codec<ReplaceBlockConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("target").forGetter((var0x) -> {
         return var0x.target;
      }), BlockState.CODEC.fieldOf("state").forGetter((var0x) -> {
         return var0x.state;
      })).apply(var0, ReplaceBlockConfiguration::new);
   });
   public final BlockState target;
   public final BlockState state;

   public ReplaceBlockConfiguration(BlockState var1, BlockState var2) {
      super();
      this.target = var1;
      this.state = var2;
   }
}
