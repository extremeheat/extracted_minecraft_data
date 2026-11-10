package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;

public class LootContextReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public LootContextReport(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
      super();
      this.output = output;
      this.registries = registries;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      return this.registries.thenCompose((registries) -> {
         Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("loot_context_requirements.json");
         return DataProvider.saveStable(cache, dumpLootContexts(registries.lookupOrThrow(Registries.CONTEXT_KEY_SET)), path);
      });
   }

   private static JsonElement dumpLootContexts(final HolderLookup.RegistryLookup<ContextKeySet> sets) {
      JsonObject result = new JsonObject();
      sets.listElements().forEach((set) -> {
         JsonObject setDetails = new JsonObject();
         setDetails.add("allowed", serializeList(((ContextKeySet)set.value()).allowed()));
         setDetails.add("required", serializeList(((ContextKeySet)set.value()).required()));
         result.add(set.key().identifier().toString(), setDetails);
      });
      return result;
   }

   private static JsonElement serializeList(final Set<ContextKey<?>> list) {
      JsonArray result = new JsonArray();

      for(ContextKey<?> contextKey : list) {
         result.add(contextKey.name().toString());
      }

      return result;
   }

   public String getName() {
      return "Loot Context Requirements";
   }
}
