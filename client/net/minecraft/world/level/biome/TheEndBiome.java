package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class TheEndBiome extends Biome {
   public TheEndBiome() {
      super((new Biome.BiomeBuilder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_THEEND).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).waterColor(4159204).waterFogColor(329011).parent((String)null));
      this.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, makeComposite(Feature.END_SPIKE, new SpikeConfiguration(false, ImmutableList.of(), (BlockPos)null), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
      BiomeDefaultFeatures.addEndCity(this);
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ENDERMAN, 10, 4, 4));
   }

   public int getSkyColor(float var1) {
      return 0;
   }
}
