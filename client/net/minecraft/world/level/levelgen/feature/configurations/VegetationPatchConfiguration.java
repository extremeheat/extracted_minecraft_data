package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VegetationPatchConfiguration implements FeatureConfiguration {
   public static final Codec<VegetationPatchConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(TagKey.hashedCodec(Registries.BLOCK).fieldOf("replaceable").forGetter((c) -> c.replaceable), BlockStateProvider.CODEC.fieldOf("ground_state").forGetter((c) -> c.groundState), PlacedFeature.CODEC.fieldOf("vegetation_feature").forGetter((c) -> c.vegetationFeature), CaveSurface.CODEC.fieldOf("surface").forGetter((c) -> c.surface), IntProviders.codec(1, 128).fieldOf("depth").forGetter((c) -> c.depth), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_bottom_block_chance").forGetter((c) -> c.extraBottomBlockChance), Codec.intRange(1, 256).fieldOf("vertical_range").forGetter((c) -> c.verticalRange), Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter((c) -> c.vegetationChance), IntProviders.CODEC.fieldOf("xz_radius").forGetter((c) -> c.xzRadius), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_edge_column_chance").forGetter((c) -> c.extraEdgeColumnChance)).apply(i, VegetationPatchConfiguration::new));
   public final TagKey<Block> replaceable;
   public final BlockStateProvider groundState;
   public final Holder<PlacedFeature> vegetationFeature;
   public final CaveSurface surface;
   public final IntProvider depth;
   public final float extraBottomBlockChance;
   public final int verticalRange;
   public final float vegetationChance;
   public final IntProvider xzRadius;
   public final float extraEdgeColumnChance;

   public VegetationPatchConfiguration(final TagKey<Block> replaceable, final BlockStateProvider groundState, final Holder<PlacedFeature> vegetationFeature, final CaveSurface surface, final IntProvider depth, final float extraBottomBlockChance, final int verticalRange, final float vegetationChance, final IntProvider xzRadius, final float extraEdgeColumnChance) {
      super();
      this.replaceable = replaceable;
      this.groundState = groundState;
      this.vegetationFeature = vegetationFeature;
      this.surface = surface;
      this.depth = depth;
      this.extraBottomBlockChance = extraBottomBlockChance;
      this.verticalRange = verticalRange;
      this.vegetationChance = vegetationChance;
      this.xzRadius = xzRadius;
      this.extraEdgeColumnChance = extraEdgeColumnChance;
   }
}
