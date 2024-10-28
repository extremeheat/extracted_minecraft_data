package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class GeodeBlockSettings {
   public final BlockStateProvider fillingProvider;
   public final BlockStateProvider innerLayerProvider;
   public final BlockStateProvider alternateInnerLayerProvider;
   public final BlockStateProvider middleLayerProvider;
   public final BlockStateProvider outerLayerProvider;
   public final List<BlockState> innerPlacements;
   public final TagKey<Block> cannotReplace;
   public final TagKey<Block> invalidBlocks;
   public static final Codec<GeodeBlockSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("filling_provider").forGetter((var0x) -> {
         return var0x.fillingProvider;
      }), BlockStateProvider.CODEC.fieldOf("inner_layer_provider").forGetter((var0x) -> {
         return var0x.innerLayerProvider;
      }), BlockStateProvider.CODEC.fieldOf("alternate_inner_layer_provider").forGetter((var0x) -> {
         return var0x.alternateInnerLayerProvider;
      }), BlockStateProvider.CODEC.fieldOf("middle_layer_provider").forGetter((var0x) -> {
         return var0x.middleLayerProvider;
      }), BlockStateProvider.CODEC.fieldOf("outer_layer_provider").forGetter((var0x) -> {
         return var0x.outerLayerProvider;
      }), ExtraCodecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("inner_placements").forGetter((var0x) -> {
         return var0x.innerPlacements;
      }), TagKey.hashedCodec(Registries.BLOCK).fieldOf("cannot_replace").forGetter((var0x) -> {
         return var0x.cannotReplace;
      }), TagKey.hashedCodec(Registries.BLOCK).fieldOf("invalid_blocks").forGetter((var0x) -> {
         return var0x.invalidBlocks;
      })).apply(var0, GeodeBlockSettings::new);
   });

   public GeodeBlockSettings(BlockStateProvider var1, BlockStateProvider var2, BlockStateProvider var3, BlockStateProvider var4, BlockStateProvider var5, List<BlockState> var6, TagKey<Block> var7, TagKey<Block> var8) {
      super();
      this.fillingProvider = var1;
      this.innerLayerProvider = var2;
      this.alternateInnerLayerProvider = var3;
      this.middleLayerProvider = var4;
      this.outerLayerProvider = var5;
      this.innerPlacements = var6;
      this.cannotReplace = var7;
      this.invalidBlocks = var8;
   }
}
