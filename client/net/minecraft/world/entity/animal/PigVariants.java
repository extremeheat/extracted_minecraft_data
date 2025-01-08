package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public class PigVariants {
   public static final ResourceKey<PigVariant> TEMPERATE;
   public static final ResourceKey<PigVariant> WARM;
   public static final ResourceKey<PigVariant> COLD;
   public static final ResourceKey<PigVariant> DEFAULT;

   public PigVariants() {
      super();
   }

   private static ResourceKey<PigVariant> createKey(String var0) {
      return ResourceKey.create(Registries.PIG_VARIANT, ResourceLocation.withDefaultNamespace(var0));
   }

   public static void bootstrap(BootstrapContext<PigVariant> var0) {
      register(var0, TEMPERATE, PigVariant.ModelType.NORMAL, "pig", Optional.empty());
      register(var0, WARM, PigVariant.ModelType.NORMAL, "warm_pig", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
      register(var0, COLD, PigVariant.ModelType.COLD, "cold_pig", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
   }

   static void register(BootstrapContext<PigVariant> var0, ResourceKey<PigVariant> var1, PigVariant.ModelType var2, String var3, TagKey<Biome> var4) {
      register(var0, var1, var2, var3, Optional.of(var0.lookup(Registries.BIOME).getOrThrow(var4)));
   }

   static void register(BootstrapContext<PigVariant> var0, ResourceKey<PigVariant> var1, PigVariant.ModelType var2, String var3, Optional<HolderSet<Biome>> var4) {
      ResourceLocation var5 = ResourceLocation.withDefaultNamespace("entity/pig/" + var3);
      var0.register(var1, new PigVariant(var2, var5, var4));
   }

   public static Optional<Holder.Reference<PigVariant>> selectVariantToSpawn(RandomSource var0, RegistryAccess var1, Holder<Biome> var2) {
      Registry var3 = var1.lookupOrThrow(Registries.PIG_VARIANT);
      List var4 = var3.listElements().filter((var1x) -> ((PigVariant)var1x.value()).biomes().isPresent() && ((HolderSet)((PigVariant)var1x.value()).biomes().get()).contains(var2)).toList();
      if (!var4.isEmpty()) {
         return Util.<Holder.Reference<PigVariant>>getRandomSafe(var4, var0);
      } else {
         List var5 = var3.listElements().filter((var0x) -> ((PigVariant)var0x.value()).biomes().isEmpty()).toList();
         return Util.<Holder.Reference<PigVariant>>getRandomSafe(var5, var0);
      }
   }

   static {
      TEMPERATE = createKey(TemperatureVariant.TEMPERATE.getId());
      WARM = createKey(TemperatureVariant.WARM.getId());
      COLD = createKey(TemperatureVariant.COLD.getId());
      DEFAULT = TEMPERATE;
   }
}
