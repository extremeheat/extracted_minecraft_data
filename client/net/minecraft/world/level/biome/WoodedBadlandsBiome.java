package net.minecraft.world.level.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class WoodedBadlandsBiome extends Biome {
   public WoodedBadlandsBiome() {
      super((new Biome.BiomeBuilder()).surfaceBuilder(SurfaceBuilder.WOODED_BADLANDS, SurfaceBuilder.CONFIG_BADLANDS).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.MESA).depth(1.5F).scale(0.025F).temperature(2.0F).downfall(0.0F).waterColor(4159204).waterFogColor(329011).parent((String)null));
      this.addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004D, MineshaftFeature.Type.MESA));
      this.addStructureStart(Feature.STRONGHOLD, FeatureConfiguration.NONE);
      BiomeDefaultFeatures.addDefaultCarvers(this);
      BiomeDefaultFeatures.addStructureFeaturePlacement(this);
      BiomeDefaultFeatures.addDefaultLakes(this);
      BiomeDefaultFeatures.addDefaultMonsterRoom(this);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
      BiomeDefaultFeatures.addDefaultOres(this);
      BiomeDefaultFeatures.addExtraGold(this);
      BiomeDefaultFeatures.addDefaultSoftDisks(this);
      BiomeDefaultFeatures.addBadlandsTrees(this);
      BiomeDefaultFeatures.addBadlandGrass(this);
      BiomeDefaultFeatures.addDefaultMushrooms(this);
      BiomeDefaultFeatures.addBadlandExtraVegetation(this);
      BiomeDefaultFeatures.addDefaultSprings(this);
      BiomeDefaultFeatures.addSurfaceFreezing(this);
      this.addSpawn(MobCategory.AMBIENT, new Biome.SpawnerData(EntityType.BAT, 10, 8, 8));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SPIDER, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SKELETON, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.CREEPER, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SLIME, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.WITCH, 5, 1, 1));
   }

   public int getFoliageColor(BlockPos var1) {
      return 10387789;
   }

   public int getGrassColor(BlockPos var1) {
      return 9470285;
   }
}
