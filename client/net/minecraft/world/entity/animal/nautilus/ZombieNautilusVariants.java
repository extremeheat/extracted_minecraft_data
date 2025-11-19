package net.minecraft.world.entity.animal.nautilus;

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

public class ZombieNautilusVariants {
   public static final ResourceKey<ZombieNautilusVariant> TEMPERATE;
   public static final ResourceKey<ZombieNautilusVariant> WARM;
   public static final ResourceKey<ZombieNautilusVariant> DEFAULT;

   public ZombieNautilusVariants() {
      super();
   }

   private static ResourceKey<ZombieNautilusVariant> createKey(Identifier var0) {
      return ResourceKey.create(Registries.ZOMBIE_NAUTILUS_VARIANT, var0);
   }

   public static void bootstrap(BootstrapContext<ZombieNautilusVariant> var0) {
      register(var0, TEMPERATE, ZombieNautilusVariant.ModelType.NORMAL, "zombie_nautilus", SpawnPrioritySelectors.fallback(0));
      register(var0, WARM, ZombieNautilusVariant.ModelType.WARM, "zombie_nautilus_coral", BiomeTags.SPAWNS_CORAL_VARIANT_ZOMBIE_NAUTILUS);
   }

   private static void register(BootstrapContext<ZombieNautilusVariant> var0, ResourceKey<ZombieNautilusVariant> var1, ZombieNautilusVariant.ModelType var2, String var3, TagKey<Biome> var4) {
      HolderSet.Named var5 = var0.lookup(Registries.BIOME).getOrThrow(var4);
      register(var0, var1, var2, var3, SpawnPrioritySelectors.single(new BiomeCheck(var5), 1));
   }

   private static void register(BootstrapContext<ZombieNautilusVariant> var0, ResourceKey<ZombieNautilusVariant> var1, ZombieNautilusVariant.ModelType var2, String var3, SpawnPrioritySelectors var4) {
      Identifier var5 = Identifier.withDefaultNamespace("entity/nautilus/" + var3);
      var0.register(var1, new ZombieNautilusVariant(new ModelAndTexture(var2, var5), var4));
   }

   static {
      TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
      WARM = createKey(TemperatureVariants.WARM);
      DEFAULT = TEMPERATE;
   }
}
