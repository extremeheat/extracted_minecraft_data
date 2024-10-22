package net.minecraft.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class WinterDropBiomes {
   public static final ResourceKey<Biome> PALE_GARDEN = createKey("pale_garden");

   public WinterDropBiomes() {
      super();
   }

   public static ResourceKey<Biome> createKey(String var0) {
      return ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace(var0));
   }

   public static void register(BootstrapContext<Biome> var0, String var1, Biome var2) {
      var0.register(createKey(var1), var2);
   }

   public static void bootstrap(BootstrapContext<Biome> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      HolderGetter var2 = var0.lookup(Registries.CONFIGURED_CARVER);
      var0.register(PALE_GARDEN, OverworldBiomes.darkForest(var1, var2, true));
   }
}
