package net.minecraft.world.level.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class SwampBiome extends Biome {
   protected SwampBiome() {
      super((new Biome.BiomeBuilder()).surfaceBuilder(SurfaceBuilder.SWAMP, SurfaceBuilder.CONFIG_GRASS).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.SWAMP).depth(-0.2F).scale(0.1F).temperature(0.8F).downfall(0.9F).waterColor(6388580).waterFogColor(2302743).parent((String)null));
      this.addStructureStart(Feature.SWAMP_HUT, FeatureConfiguration.NONE);
      this.addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004D, MineshaftFeature.Type.NORMAL));
      BiomeDefaultFeatures.addDefaultCarvers(this);
      BiomeDefaultFeatures.addStructureFeaturePlacement(this);
      BiomeDefaultFeatures.addDefaultLakes(this);
      BiomeDefaultFeatures.addDefaultMonsterRoom(this);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
      BiomeDefaultFeatures.addDefaultOres(this);
      BiomeDefaultFeatures.addSwampClayDisk(this);
      BiomeDefaultFeatures.addSwampVegetation(this);
      BiomeDefaultFeatures.addDefaultMushrooms(this);
      BiomeDefaultFeatures.addSwampExtraVegetation(this);
      BiomeDefaultFeatures.addDefaultSprings(this);
      this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, makeComposite(Feature.SEAGRASS, new SeagrassFeatureConfiguration(64, 0.6D), FeatureDecorator.TOP_SOLID_HEIGHTMAP, DecoratorConfiguration.NONE));
      BiomeDefaultFeatures.addSwampExtraDecoration(this);
      BiomeDefaultFeatures.addSurfaceFreezing(this);
      this.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.SHEEP, 12, 4, 4));
      this.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.PIG, 10, 4, 4));
      this.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
      this.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.COW, 8, 4, 4));
      this.addSpawn(MobCategory.AMBIENT, new Biome.SpawnerData(EntityType.BAT, 10, 8, 8));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SPIDER, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SKELETON, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.CREEPER, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SLIME, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.WITCH, 5, 1, 1));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SLIME, 1, 1, 1));
   }

   public int getGrassColor(BlockPos var1) {
      double var2 = BIOME_INFO_NOISE.getValue((double)var1.getX() * 0.0225D, (double)var1.getZ() * 0.0225D);
      return var2 < -0.1D ? 5011004 : 6975545;
   }

   public int getFoliageColor(BlockPos var1) {
      return 6975545;
   }
}
