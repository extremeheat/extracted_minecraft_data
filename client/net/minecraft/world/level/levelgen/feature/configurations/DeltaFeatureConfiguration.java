package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public class DeltaFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<DeltaFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("contents").forGetter((var0x) -> {
         return var0x.contents;
      }), BlockState.CODEC.fieldOf("rim").forGetter((var0x) -> {
         return var0x.rim;
      }), IntProvider.codec(0, 16).fieldOf("size").forGetter((var0x) -> {
         return var0x.size;
      }), IntProvider.codec(0, 16).fieldOf("rim_size").forGetter((var0x) -> {
         return var0x.rimSize;
      })).apply(var0, DeltaFeatureConfiguration::new);
   });
   private final BlockState contents;
   private final BlockState rim;
   private final IntProvider size;
   private final IntProvider rimSize;

   public DeltaFeatureConfiguration(BlockState var1, BlockState var2, IntProvider var3, IntProvider var4) {
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

   public IntProvider size() {
      return this.size;
   }

   public IntProvider rimSize() {
      return this.rimSize;
   }
}
