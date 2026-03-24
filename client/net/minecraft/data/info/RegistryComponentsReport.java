package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;

public class RegistryComponentsReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public RegistryComponentsReport(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
      super();
      this.output = output;
      this.registries = registries;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      return this.registries.thenCompose((registries) -> {
         RegistryOps<JsonElement> registryOps = registries.<JsonElement>createSerializationContext(JsonOps.INSTANCE);
         List<CompletableFuture<?>> writes = new ArrayList();
         BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries).forEach((pendingComponents) -> {
            PackOutput.PathProvider registryPathProvider = this.output.createRegistryComponentPathProvider(pendingComponents.key());
            pendingComponents.forEach((element, components) -> {
               if (!components.isEmpty()) {
                  Identifier elementId = element.key().identifier();
                  Path elementPath = registryPathProvider.json(elementId);
                  DataComponentPatch patch = DataComponentPatch.builder().set(components).build();
                  JsonObject root = new JsonObject();
                  root.add("components", (JsonElement)DataComponentPatch.CODEC.encodeStart(registryOps, patch).getOrThrow((err) -> {
                     String var10002 = String.valueOf(elementId);
                     return new IllegalStateException("Failed to encode components for item " + var10002 + ": " + err);
                  }));
                  writes.add(DataProvider.saveStable(cache, root, elementPath));
               }
            });
         });
         return CompletableFuture.allOf((CompletableFuture[])writes.toArray((x$0) -> new CompletableFuture[x$0]));
      });
   }

   public final String getName() {
      return "Default Components";
   }
}
