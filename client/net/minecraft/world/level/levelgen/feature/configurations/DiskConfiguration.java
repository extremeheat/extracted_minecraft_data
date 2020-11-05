package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.block.state.BlockState;

public class DiskConfiguration implements FeatureConfiguration {
   public static final Codec<DiskConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("state").forGetter((var0x) -> {
         return var0x.state;
      }), UniformInt.codec(0, 4, 4).fieldOf("radius").forGetter((var0x) -> {
         return var0x.radius;
      }), Codec.intRange(0, 4).fieldOf("half_height").forGetter((var0x) -> {
         return var0x.halfHeight;
      }), BlockState.CODEC.listOf().fieldOf("targets").forGetter((var0x) -> {
         return var0x.targets;
      })).apply(var0, DiskConfiguration::new);
   });
   public final BlockState state;
   public final UniformInt radius;
   public final int halfHeight;
   public final List<BlockState> targets;

   public DiskConfiguration(BlockState var1, UniformInt var2, int var3, List<BlockState> var4) {
      super();
      this.state = var1;
      this.radius = var2;
      this.halfHeight = var3;
      this.targets = var4;
   }
}
