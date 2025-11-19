package net.minecraft.world.entity.animal.pig;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public class PigVariants {
   public static final ResourceKey<PigVariant> TEMPERATE;
   public static final ResourceKey<PigVariant> WARM;
   public static final ResourceKey<PigVariant> COLD;
   public static final ResourceKey<PigVariant> DEFAULT;

   public PigVariants() {
      super();
   }

   private static ResourceKey<PigVariant> createKey(Identifier var0) {
      return ResourceKey.create(Registries.PIG_VARIANT, var0);
   }

   public static void bootstrap(BootstrapContext<PigVariant> var0) {
      register(var0, TEMPERATE, PigVariant.ModelType.NORMAL, "temperate_pig", SpawnPrioritySelectors.fallback(0));
      register(var0, WARM, PigVariant.ModelType.NORMAL, "warm_pig", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
      register(var0, COLD, PigVariant.ModelType.COLD, "cold_pig", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
   }

   private static void register(BootstrapContext<PigVariant> var0, ResourceKey<PigVariant> var1, PigVariant.ModelType var2, String var3, TagKey<Biome> var4) {
      HolderSet.Named var5 = var0.lookup(Registries.BIOME).getOrThrow(var4);
      register(var0, var1, var2, var3, SpawnPrioritySelectors.single(new BiomeCheck(var5), 1));
   }

   private static void register(BootstrapContext<PigVariant> var0, ResourceKey<PigVariant> var1, PigVariant.ModelType var2, String var3, SpawnPrioritySelectors var4) {
      Identifier var5 = Identifier.withDefaultNamespace("entity/pig/" + var3);
      var0.register(var1, new PigVariant(new ModelAndTexture(var2, var5), var4));
   }

   static {
      TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
      WARM = createKey(TemperatureVariants.WARM);
      COLD = createKey(TemperatureVariants.COLD);
      DEFAULT = TEMPERATE;
   }
}
