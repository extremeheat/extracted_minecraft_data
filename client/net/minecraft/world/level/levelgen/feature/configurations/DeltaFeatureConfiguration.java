package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.block.state.BlockState;

public class DeltaFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<DeltaFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("contents").forGetter((var0x) -> {
         return var0x.contents;
      }), BlockState.CODEC.fieldOf("rim").forGetter((var0x) -> {
         return var0x.rim;
      }), UniformInt.codec(0, 8, 8).fieldOf("size").forGetter((var0x) -> {
         return var0x.size;
      }), UniformInt.codec(0, 8, 8).fieldOf("rim_size").forGetter((var0x) -> {
         return var0x.rimSize;
      })).apply(var0, DeltaFeatureConfiguration::new);
   });
   private final BlockState contents;
   private final BlockState rim;
   private final UniformInt size;
   private final UniformInt rimSize;

   public DeltaFeatureConfiguration(BlockState var1, BlockState var2, UniformInt var3, UniformInt var4) {
      super();
      this.contents = var1;
      this.rim = var2;
      this.size = var3;
      this.rimSize = var4;
   }

   public BlockState contents() {
      return this.contents;
   }

   public BlockState rim() {
      return this.rim;
   }

   public UniformInt size() {
      return this.size;
   }

   public UniformInt rimSize() {
      return this.rimSize;
   }
}
