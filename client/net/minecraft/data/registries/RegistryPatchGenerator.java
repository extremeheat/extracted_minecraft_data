package net.minecraft.data.registries;

import com.mojang.datafixers.DataFixUtils;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Cloner;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;

public class RegistryPatchGenerator {
   public RegistryPatchGenerator() {
      super();
   }

   public static CompletableFuture<RegistrySetBuilder.PatchedRegistries> createLookup(CompletableFuture<HolderLookup.Provider> var0, RegistrySetBuilder var1) {
      return var0.thenApply((var1x) -> {
         RegistryAccess.Frozen var2 = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
         Cloner.Factory var3 = new Cloner.Factory();
         RegistryDataLoader.WORLDGEN_REGISTRIES.forEach((var1xx) -> {
            Objects.requireNonNull(var3);
            var1xx.runWithArguments(var3::addCodec);
         });
         RegistrySetBuilder.PatchedRegistries var4 = var1.buildPatch(var2, var1x, var3);
         HolderLookup.Provider var5 = var4.full();
         Optional var6 = var5.lookup(Registries.BIOME);
         Optional var7 = var5.lookup(Registries.PLACED_FEATURE);
         if (var6.isPresent() || var7.isPresent()) {
            VanillaRegistries.validateThatAllBiomeFeaturesHaveBiomeFilter((HolderGetter)DataFixUtils.orElseGet(var7, () -> var1x.lookupOrThrow(Registries.PLACED_FEATURE)), (HolderLookup)DataFixUtils.orElseGet(var6, () -> var1x.lookupOrThrow(Registries.BIOME)));
         }

         return var4;
      });
   }
}
