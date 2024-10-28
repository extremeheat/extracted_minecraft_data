package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RootSystemConfiguration implements FeatureConfiguration {
   public static final Codec<RootSystemConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(PlacedFeature.CODEC.fieldOf("feature").forGetter((var0x) -> {
         return var0x.treeFeature;
      }), Codec.intRange(1, 64).fieldOf("required_vertical_space_for_tree").forGetter((var0x) -> {
         return var0x.requiredVerticalSpaceForTree;
      }), Codec.intRange(1, 64).fieldOf("root_radius").forGetter((var0x) -> {
         return var0x.rootRadius;
      }), TagKey.hashedCodec(Registries.BLOCK).fieldOf("root_replaceable").forGetter((var0x) -> {
         return var0x.rootReplaceable;
      }), BlockStateProvider.CODEC.fieldOf("root_state_provider").forGetter((var0x) -> {
         return var0x.rootStateProvider;
      }), Codec.intRange(1, 256).fieldOf("root_placement_attempts").forGetter((var0x) -> {
         return var0x.rootPlacementAttempts;
      }), Codec.intRange(1, 4096).fieldOf("root_column_max_height").forGetter((var0x) -> {
         return var0x.rootColumnMaxHeight;
      }), Codec.intRange(1, 64).fieldOf("hanging_root_radius").forGetter((var0x) -> {
         return var0x.hangingRootRadius;
      }), Codec.intRange(0, 16).fieldOf("hanging_roots_vertical_span").forGetter((var0x) -> {
         return var0x.hangingRootsVerticalSpan;
      }), BlockStateProvider.CODEC.fieldOf("hanging_root_state_provider").forGetter((var0x) -> {
         return var0x.hangingRootStateProvider;
      }), Codec.intRange(1, 256).fieldOf("hanging_root_placement_attempts").forGetter((var0x) -> {
         return var0x.hangingRootPlacementAttempts;
      }), Codec.intRange(1, 64).fieldOf("allowed_vertical_water_for_tree").forGetter((var0x) -> {
         return var0x.allowedVerticalWaterForTree;
      }), BlockPredicate.CODEC.fieldOf("allowed_tree_position").forGetter((var0x) -> {
         return var0x.allowedTreePosition;
      })).apply(var0, RootSystemConfiguration::new);
   });
   public final Holder<PlacedFeature> treeFeature;
   public final int requiredVerticalSpaceForTree;
   public final int rootRadius;
   public final TagKey<Block> rootReplaceable;
   public final BlockStateProvider rootStateProvider;
   public final int rootPlacementAttempts;
   public final int rootColumnMaxHeight;
   public final int hangingRootRadius;
   public final int hangingRootsVerticalSpan;
   public final BlockStateProvider hangingRootStateProvider;
   public final int hangingRootPlacementAttempts;
   public final int allowedVerticalWaterForTree;
   public final BlockPredicate allowedTreePosition;

   public RootSystemConfiguration(Holder<PlacedFeature> var1, int var2, int var3, TagKey<Block> var4, BlockStateProvider var5, int var6, int var7, int var8, int var9, BlockStateProvider var10, int var11, int var12, BlockPredicate var13) {
      super();
      this.treeFeature = var1;
      this.requiredVerticalSpaceForTree = var2;
      this.rootRadius = var3;
      this.rootReplaceable = var4;
      this.rootStateProvider = var5;
      this.rootPlacementAttempts = var6;
      this.rootColumnMaxHeight = var7;
      this.hangingRootRadius = var8;
      this.hangingRootsVerticalSpan = var9;
      this.hangingRootStateProvider = var10;
      this.hangingRootPlacementAttempts = var11;
      this.allowedVerticalWaterForTree = var12;
      this.allowedTreePosition = var13;
   }
}
