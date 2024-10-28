package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VegetationPatchConfiguration implements FeatureConfiguration {
   public static final Codec<VegetationPatchConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(TagKey.hashedCodec(Registries.BLOCK).fieldOf("replaceable").forGetter((var0x) -> {
         return var0x.replaceable;
      }), BlockStateProvider.CODEC.fieldOf("ground_state").forGetter((var0x) -> {
         return var0x.groundState;
      }), PlacedFeature.CODEC.fieldOf("vegetation_feature").forGetter((var0x) -> {
         return var0x.vegetationFeature;
      }), CaveSurface.CODEC.fieldOf("surface").forGetter((var0x) -> {
         return var0x.surface;
      }), IntProvider.codec(1, 128).fieldOf("depth").forGetter((var0x) -> {
         return var0x.depth;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_bottom_block_chance").forGetter((var0x) -> {
         return var0x.extraBottomBlockChance;
      }), Codec.intRange(1, 256).fieldOf("vertical_range").forGetter((var0x) -> {
         return var0x.verticalRange;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter((var0x) -> {
         return var0x.vegetationChance;
      }), IntProvider.CODEC.fieldOf("xz_radius").forGetter((var0x) -> {
         return var0x.xzRadius;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_edge_column_chance").forGetter((var0x) -> {
         return var0x.extraEdgeColumnChance;
      })).apply(var0, VegetationPatchConfiguration::new);
   });
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

   public VegetationPatchConfiguration(TagKey<Block> var1, BlockStateProvider var2, Holder<PlacedFeature> var3, CaveSurface var4, IntProvider var5, float var6, int var7, float var8, IntProvider var9, float var10) {
      super();
      this.replaceable = var1;
      this.groundState = var2;
      this.vegetationFeature = var3;
      this.surface = var4;
      this.depth = var5;
      this.extraBottomBlockChance = var6;
      this.verticalRange = var7;
      this.vegetationChance = var8;
      this.xzRadius = var9;
      this.extraEdgeColumnChance = var10;
   }
}
