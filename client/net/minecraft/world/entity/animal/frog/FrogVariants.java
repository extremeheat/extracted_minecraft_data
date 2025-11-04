package net.minecraft.world.entity.animal.frog;

import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public interface FrogVariants {
   ResourceKey<FrogVariant> TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
   ResourceKey<FrogVariant> WARM = createKey(TemperatureVariants.WARM);
   ResourceKey<FrogVariant> COLD = createKey(TemperatureVariants.COLD);

   private static ResourceKey<FrogVariant> createKey(Identifier var0) {
      return ResourceKey.create(Registries.FROG_VARIANT, var0);
   }

   static void bootstrap(BootstrapContext<FrogVariant> var0) {
      register(var0, TEMPERATE, "entity/frog/temperate_frog", SpawnPrioritySelectors.fallback(0));
      register(var0, WARM, "entity/frog/warm_frog", BiomeTags.SPAWNS_WARM_VARIANT_FROGS);
      register(var0, COLD, "entity/frog/cold_frog", BiomeTags.SPAWNS_COLD_VARIANT_FROGS);
   }

   private static void register(BootstrapContext<FrogVariant> var0, ResourceKey<FrogVariant> var1, String var2, TagKey<Biome> var3) {
      HolderSet.Named var4 = var0.lookup(Registries.BIOME).getOrThrow(var3);
      register(var0, var1, var2, SpawnPrioritySelectors.single(new BiomeCheck(var4), 1));
   }

   private static void register(BootstrapContext<FrogVariant> var0, ResourceKey<FrogVariant> var1, String var2, SpawnPrioritySelectors var3) {
      var0.register(var1, new FrogVariant(new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace(var2)), var3));
   }
}
