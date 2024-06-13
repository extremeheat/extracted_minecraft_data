package net.minecraft.data.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class RegistriesDatapackGenerator implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public RegistriesDatapackGenerator(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.registries = var2;
      this.output = var1;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      return this.registries
         .thenCompose(
            var2 -> {
               RegistryOps var3 = var2.createSerializationContext(JsonOps.INSTANCE);
               return CompletableFuture.allOf(
                  RegistryDataLoader.WORLDGEN_REGISTRIES
                     .stream()
                     .flatMap(var4 -> this.dumpRegistryCap(var1, var2, var3, (RegistryDataLoader.RegistryData<?>)var4).stream())
                     .toArray(CompletableFuture[]::new)
               );
            }
         );
   }

   private <T> Optional<CompletableFuture<?>> dumpRegistryCap(
      CachedOutput var1, HolderLookup.Provider var2, DynamicOps<JsonElement> var3, RegistryDataLoader.RegistryData<T> var4
   ) {
      ResourceKey var5 = var4.key();
      return var2.lookup(var5)
         .map(
            var5x -> {
               PackOutput.PathProvider var6 = this.output.createRegistryElementsPathProvider(var5);
               return CompletableFuture.allOf(
                  var5x.listElements()
                     .map(var4xx -> dumpValue(var6.json(var4xx.key().location()), var1, var3, var4.elementCodec(), var4xx.value()))
                     .toArray(CompletableFuture[]::new)
               );
            }
         );
   }

   private static <E> CompletableFuture<?> dumpValue(Path var0, CachedOutput var1, DynamicOps<JsonElement> var2, Encoder<E> var3, E var4) {
      return (CompletableFuture<?>)var3.encodeStart(var2, var4)
         .mapOrElse(
            var2x -> DataProvider.saveStable(var1, var2x, var0),
            var1x -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + var0 + "': " + var1x.message()))
         );
   }

   @Override
   public final String getName() {
      return "Registries";
   }
}
