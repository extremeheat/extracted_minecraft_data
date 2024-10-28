package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;

public class ItemListReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public ItemListReport(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.output = var1;
      this.registries = var2;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      Path var2 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("items.json");
      return this.registries.thenCompose((var2x) -> {
         JsonObject var3 = new JsonObject();
         RegistryOps var4 = var2x.createSerializationContext(JsonOps.INSTANCE);
         var2x.lookupOrThrow(Registries.ITEM).listElements().forEach((var2xx) -> {
            JsonObject var3x = new JsonObject();
            var3x.add("components", (JsonElement)DataComponentMap.CODEC.encodeStart(var4, ((Item)var2xx.value()).components()).getOrThrow((var0) -> {
               return new IllegalStateException("Failed to encode components: " + var0);
            }));
            var3.add(var2xx.getRegisteredName(), var3x);
         });
         return DataProvider.saveStable(var1, var3, var2);
      });
   }

   public final String getName() {
      return "Item List";
   }
}
