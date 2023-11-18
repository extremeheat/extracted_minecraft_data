package net.minecraft.world.level.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MultiNoiseBiomeSourceParameterLists {
   public static final ResourceKey<MultiNoiseBiomeSourceParameterList> NETHER = register("nether");
   public static final ResourceKey<MultiNoiseBiomeSourceParameterList> OVERWORLD = register("overworld");

   public MultiNoiseBiomeSourceParameterLists() {
      super();
   }

   public static void bootstrap(BootstapContext<MultiNoiseBiomeSourceParameterList> var0) {
      HolderGetter var1 = var0.lookup(Registries.BIOME);
      var0.register(NETHER, new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.NETHER, var1));
      var0.register(OVERWORLD, new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD, var1));
   }

   private static ResourceKey<MultiNoiseBiomeSourceParameterList> register(String var0) {
      return ResourceKey.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, new ResourceLocation(var0));
   }
}
