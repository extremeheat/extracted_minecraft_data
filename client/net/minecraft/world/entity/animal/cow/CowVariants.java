package net.minecraft.world.entity.animal.cow;

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

public class CowVariants {
   public static final ResourceKey<CowVariant> TEMPERATE;
   public static final ResourceKey<CowVariant> WARM;
   public static final ResourceKey<CowVariant> COLD;
   public static final ResourceKey<CowVariant> DEFAULT;

   public CowVariants() {
      super();
   }

   private static ResourceKey<CowVariant> createKey(Identifier var0) {
      return ResourceKey.create(Registries.COW_VARIANT, var0);
   }

   public static void bootstrap(BootstrapContext<CowVariant> var0) {
      register(var0, TEMPERATE, CowVariant.ModelType.NORMAL, "temperate_cow", SpawnPrioritySelectors.fallback(0));
      register(var0, WARM, CowVariant.ModelType.WARM, "warm_cow", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
      register(var0, COLD, CowVariant.ModelType.COLD, "cold_cow", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
   }

   private static void register(BootstrapContext<CowVariant> var0, ResourceKey<CowVariant> var1, CowVariant.ModelType var2, String var3, TagKey<Biome> var4) {
      HolderSet.Named var5 = var0.lookup(Registries.BIOME).getOrThrow(var4);
      register(var0, var1, var2, var3, SpawnPrioritySelectors.single(new BiomeCheck(var5), 1));
   }

   private static void register(BootstrapContext<CowVariant> var0, ResourceKey<CowVariant> var1, CowVariant.ModelType var2, String var3, SpawnPrioritySelectors var4) {
      Identifier var5 = Identifier.withDefaultNamespace("entity/cow/" + var3);
      var0.register(var1, new CowVariant(new ModelAndTexture(var2, var5), var4));
   }

   static {
      TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
      WARM = createKey(TemperatureVariants.WARM);
      COLD = createKey(TemperatureVariants.COLD);
      DEFAULT = TEMPERATE;
   }
}
