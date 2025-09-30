package net.minecraft.world.entity.animal;

import java.util.List;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.variant.MoonBrightnessCheck;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.entity.variant.StructureCheck;

public interface CatVariants {
   ResourceKey<CatVariant> TABBY = createKey("tabby");
   ResourceKey<CatVariant> BLACK = createKey("black");
   ResourceKey<CatVariant> RED = createKey("red");
   ResourceKey<CatVariant> SIAMESE = createKey("siamese");
   ResourceKey<CatVariant> BRITISH_SHORTHAIR = createKey("british_shorthair");
   ResourceKey<CatVariant> CALICO = createKey("calico");
   ResourceKey<CatVariant> PERSIAN = createKey("persian");
   ResourceKey<CatVariant> RAGDOLL = createKey("ragdoll");
   ResourceKey<CatVariant> WHITE = createKey("white");
   ResourceKey<CatVariant> JELLIE = createKey("jellie");
   ResourceKey<CatVariant> ALL_BLACK = createKey("all_black");

   private static ResourceKey<CatVariant> createKey(String var0) {
      return ResourceKey.create(Registries.CAT_VARIANT, ResourceLocation.withDefaultNamespace(var0));
   }

   static void bootstrap(BootstrapContext<CatVariant> var0) {
      HolderGetter var1 = var0.lookup(Registries.STRUCTURE);
      registerForAnyConditions(var0, TABBY, "entity/cat/tabby");
      registerForAnyConditions(var0, BLACK, "entity/cat/black");
      registerForAnyConditions(var0, RED, "entity/cat/red");
      registerForAnyConditions(var0, SIAMESE, "entity/cat/siamese");
      registerForAnyConditions(var0, BRITISH_SHORTHAIR, "entity/cat/british_shorthair");
      registerForAnyConditions(var0, CALICO, "entity/cat/calico");
      registerForAnyConditions(var0, PERSIAN, "entity/cat/persian");
      registerForAnyConditions(var0, RAGDOLL, "entity/cat/ragdoll");
      registerForAnyConditions(var0, WHITE, "entity/cat/white");
      registerForAnyConditions(var0, JELLIE, "entity/cat/jellie");
      register(var0, ALL_BLACK, "entity/cat/all_black", new SpawnPrioritySelectors(List.of(new PriorityProvider.Selector(new StructureCheck(var1.getOrThrow(StructureTags.CATS_SPAWN_AS_BLACK)), 1), new PriorityProvider.Selector(new MoonBrightnessCheck(MinMaxBounds.Doubles.atLeast(0.9)), 0))));
   }

   private static void registerForAnyConditions(BootstrapContext<CatVariant> var0, ResourceKey<CatVariant> var1, String var2) {
      register(var0, var1, var2, SpawnPrioritySelectors.fallback(0));
   }

   private static void register(BootstrapContext<CatVariant> var0, ResourceKey<CatVariant> var1, String var2, SpawnPrioritySelectors var3) {
      var0.register(var1, new CatVariant(new ClientAsset.ResourceTexture(ResourceLocation.withDefaultNamespace(var2)), var3));
   }
}
