package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemListReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public ItemListReport(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.output = var1;
      this.registries = var2;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      Path var2 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("items.json");
      return this.registries.thenCompose(var2x -> {
         JsonObject var3 = new JsonObject();
         RegistryOps var4 = var2x.createSerializationContext(JsonOps.INSTANCE);
         var2x.lookupOrThrow(Registries.ITEM).listElements().forEach(var2xx -> {
            JsonObject var3xx = new JsonObject();
            JsonArray var4xx = new JsonArray();
            ((Item)var2xx.value()).components().forEach(var2xxx -> var4x.add(dumpComponent(var2xxx, var4)));
            var3xx.add("components", var4xx);
            var3.add(var2xx.getRegisteredName(), var3xx);
         });
         return DataProvider.saveStable(var1, var3, var2);
      });
   }

   private static <T> JsonElement dumpComponent(TypedDataComponent<T> var0, DynamicOps<JsonElement> var1) {
      ResourceLocation var2 = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(var0.type());
      JsonElement var3 = (JsonElement)var0.encodeValue(var1)
         .getOrThrow(var1x -> new IllegalStateException("Failed to serialize component " + var2 + ": " + var1x));
      JsonObject var4 = new JsonObject();
      var4.addProperty("type", var2.toString());
      var4.add("value", var3);
      return var4;
   }

   @Override
   public final String getName() {
      return "Item List";
   }
}
