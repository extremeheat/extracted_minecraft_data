package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record BlockColumnConfiguration(List<BlockColumnConfiguration.Layer> b, Direction c, BlockPredicate d, boolean e) implements FeatureConfiguration {
   private final List<BlockColumnConfiguration.Layer> layers;
   private final Direction direction;
   private final BlockPredicate allowedPlacement;
   private final boolean prioritizeTip;
   public static final Codec<BlockColumnConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockColumnConfiguration.Layer.CODEC.listOf().fieldOf("layers").forGetter(BlockColumnConfiguration::layers), Direction.CODEC.fieldOf("direction").forGetter(BlockColumnConfiguration::direction), BlockPredicate.CODEC.fieldOf("allowed_placement").forGetter(BlockColumnConfiguration::allowedPlacement), Codec.BOOL.fieldOf("prioritize_tip").forGetter(BlockColumnConfiguration::prioritizeTip)).apply(var0, BlockColumnConfiguration::new);
   });

   public BlockColumnConfiguration(List<BlockColumnConfiguration.Layer> var1, Direction var2, BlockPredicate var3, boolean var4) {
      super();
      this.layers = var1;
      this.direction = var2;
      this.allowedPlacement = var3;
      this.prioritizeTip = var4;
   }

   public static BlockColumnConfiguration.Layer layer(IntProvider var0, BlockStateProvider var1) {
      return new BlockColumnConfiguration.Layer(var0, var1);
   }

   public static BlockColumnConfiguration simple(IntProvider var0, BlockStateProvider var1) {
      return new BlockColumnConfiguration(List.of(layer(var0, var1)), Direction.field_526, BlockPredicate.matchesBlock(Blocks.AIR, BlockPos.ZERO), false);
   }

   public List<BlockColumnConfiguration.Layer> layers() {
      return this.layers;
   }

   public Direction direction() {
      return this.direction;
   }

   public BlockPredicate allowedPlacement() {
      return this.allowedPlacement;
   }

   public boolean prioritizeTip() {
      return this.prioritizeTip;
   }

   public static record Layer(IntProvider b, BlockStateProvider c) {
      private final IntProvider height;
      private final BlockStateProvider state;
      public static final Codec<BlockColumnConfiguration.Layer> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(IntProvider.NON_NEGATIVE_CODEC.fieldOf("height").forGetter(BlockColumnConfiguration.Layer::height), BlockStateProvider.CODEC.fieldOf("provider").forGetter(BlockColumnConfiguration.Layer::state)).apply(var0, BlockColumnConfiguration.Layer::new);
      });

      public Layer(IntProvider var1, BlockStateProvider var2) {
         super();
         this.height = var1;
         this.state = var2;
      }

      public IntProvider height() {
         return this.height;
      }

      public BlockStateProvider state() {
         return this.state;
      }
   }
}
