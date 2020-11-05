package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.block.state.BlockState;

public class ReplaceSphereConfiguration implements FeatureConfiguration {
   public static final Codec<ReplaceSphereConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("target").forGetter((var0x) -> {
         return var0x.targetState;
      }), BlockState.CODEC.fieldOf("state").forGetter((var0x) -> {
         return var0x.replaceState;
      }), UniformInt.CODEC.fieldOf("radius").forGetter((var0x) -> {
         return var0x.radius;
      })).apply(var0, ReplaceSphereConfiguration::new);
   });
   public final BlockState targetState;
   public final BlockState replaceState;
   private final UniformInt radius;

   public ReplaceSphereConfiguration(BlockState var1, BlockState var2, UniformInt var3) {
      super();
      this.targetState = var1;
      this.replaceState = var2;
      this.radius = var3;
   }

   public UniformInt radius() {
      return this.radius;
   }
}
