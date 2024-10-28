package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class UpdateOneTwentyOneBiomeData {
   public UpdateOneTwentyOneBiomeData() {
      super();
   }

   public static void bootstrap(BootstrapContext<Biome> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      HolderGetter var2 = var0.lookup(Registries.CONFIGURED_CARVER);
      MobSpawnSettings.SpawnerData var3 = new MobSpawnSettings.SpawnerData(EntityType.BOGGED, 50, 4, 4);
      var0.register(Biomes.MANGROVE_SWAMP, OverworldBiomes.mangroveSwamp(var1, var2, (var1x) -> {
         var1x.addSpawn(MobCategory.MONSTER, var3);
      }));
      var0.register(Biomes.SWAMP, OverworldBiomes.swamp(var1, var2, (var1x) -> {
         var1x.addSpawn(MobCategory.MONSTER, var3);
      }));
   }
}
