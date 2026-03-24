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
   public static final Codec<GeodeBlockSettings> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateProvider.CODEC.fieldOf("filling_provider").forGetter((c) -> c.fillingProvider), BlockStateProvider.CODEC.fieldOf("inner_layer_provider").forGetter((c) -> c.innerLayerProvider), BlockStateProvider.CODEC.fieldOf("alternate_inner_layer_provider").forGetter((c) -> c.alternateInnerLayerProvider), BlockStateProvider.CODEC.fieldOf("middle_layer_provider").forGetter((c) -> c.middleLayerProvider), BlockStateProvider.CODEC.fieldOf("outer_layer_provider").forGetter((c) -> c.outerLayerProvider), ExtraCodecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("inner_placements").forGetter((c) -> c.innerPlacements), TagKey.hashedCodec(Registries.BLOCK).fieldOf("cannot_replace").forGetter((c) -> c.cannotReplace), TagKey.hashedCodec(Registries.BLOCK).fieldOf("invalid_blocks").forGetter((c) -> c.invalidBlocks)).apply(i, GeodeBlockSettings::new));

   public GeodeBlockSettings(final BlockStateProvider fillingProvider, final BlockStateProvider innerLayerProvider, final BlockStateProvider alternateInnerLayerProvider, final BlockStateProvider middleLayerProvider, final BlockStateProvider outerLayerProvider, final List<BlockState> innerPlacements, final TagKey<Block> cannotReplace, final TagKey<Block> invalidBlocks) {
      super();
      this.fillingProvider = fillingProvider;
      this.innerLayerProvider = innerLayerProvider;
      this.alternateInnerLayerProvider = alternateInnerLayerProvider;
      this.middleLayerProvider = middleLayerProvider;
      this.outerLayerProvider = outerLayerProvider;
      this.innerPlacements = innerPlacements;
      this.cannotReplace = cannotReplace;
      this.invalidBlocks = invalidBlocks;
   }
}
