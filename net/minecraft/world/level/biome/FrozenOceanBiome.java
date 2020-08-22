package net.minecraft.world.level.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public final class FrozenOceanBiome extends Biome {
   protected static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(3456L), 2, 0);

   public FrozenOceanBiome() {
      super((new Biome.BiomeBuilder()).surfaceBuilder(SurfaceBuilder.FROZEN_OCEAN, SurfaceBuilder.CONFIG_GRASS).precipitation(Biome.Precipitation.SNOW).biomeCategory(Biome.BiomeCategory.OCEAN).depth(-1.0F).scale(0.1F).temperature(0.0F).downfall(0.5F).waterColor(3750089).waterFogColor(329011).parent((String)null));
      this.addStructureStart(Feature.OCEAN_RUIN.configured(new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.9F)));
      this.addStructureStart(Feature.MINESHAFT.configured(new MineshaftConfiguration(0.004D, MineshaftFeature.Type.NORMAL)));
      this.addStructureStart(Feature.SHIPWRECK.configured(new ShipwreckConfiguration(false)));
      BiomeDefaultFeatures.addOceanCarvers(this);
      BiomeDefaultFeatures.addStructureFeaturePlacement(this);
      BiomeDefaultFeatures.addDefaultLakes(this);
      BiomeDefaultFeatures.addIcebergs(this);
      BiomeDefaultFeatures.addDefaultMonsterRoom(this);
      BiomeDefaultFeatures.addBlueIce(this);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
      BiomeDefaultFeatures.addDefaultOres(this);
      BiomeDefaultFeatures.addDefaultSoftDisks(this);
      BiomeDefaultFeatures.addWaterTrees(this);
      BiomeDefaultFeatures.addDefaultFlowers(this);
      BiomeDefaultFeatures.addDefaultGrass(this);
      BiomeDefaultFeatures.addDefaultMushrooms(this);
      BiomeDefaultFeatures.addDefaultExtraVegetation(this);
      BiomeDefaultFeatures.addDefaultSprings(this);
      BiomeDefaultFeatures.addSurfaceFreezing(this);
      this.addSpawn(MobCategory.WATER_CREATURE, new Biome.SpawnerData(EntityType.SQUID, 1, 1, 4));
      this.addSpawn(MobCategory.WATER_CREATURE, new Biome.SpawnerData(EntityType.SALMON, 15, 1, 5));
      this.addSpawn(MobCategory.CREATURE, new Biome.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
      this.addSpawn(MobCategory.AMBIENT, new Biome.SpawnerData(EntityType.BAT, 10, 8, 8));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SPIDER, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.DROWNED, 5, 1, 1));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SKELETON, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.CREEPER, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.SLIME, 100, 4, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
      this.addSpawn(MobCategory.MONSTER, new Biome.SpawnerData(EntityType.WITCH, 5, 1, 1));
   }

   protected float getTemperatureNoCache(BlockPos var1) {
      float var2 = this.getTemperature();
      double var3 = FROZEN_TEMPERATURE_NOISE.getValue((double)var1.getX() * 0.05D, (double)var1.getZ() * 0.05D, false) * 7.0D;
      double var5 = BIOME_INFO_NOISE.getValue((double)var1.getX() * 0.2D, (double)var1.getZ() * 0.2D, false);
      double var7 = var3 + var5;
      if (var7 < 0.3D) {
         double var9 = BIOME_INFO_NOISE.getValue((double)var1.getX() * 0.09D, (double)var1.getZ() * 0.09D, false);
         if (var9 < 0.8D) {
            var2 = 0.2F;
         }
      }

      if (var1.getY() > 64) {
         float var11 = (float)(TEMPERATURE_NOISE.getValue((double)((float)var1.getX() / 8.0F), (double)((float)var1.getZ() / 8.0F), false) * 4.0D);
         return var2 - (var11 + (float)var1.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return var2;
      }
   }
}
