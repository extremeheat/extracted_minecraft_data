package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport implements DataProvider {
   private final PackOutput output;

   public RegistryDumpReport(PackOutput var1) {
      super();
      this.output = var1;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      JsonObject var2 = new JsonObject();
      BuiltInRegistries.REGISTRY.holders().forEach((var1x) -> {
         var2.add(var1x.key().location().toString(), dumpRegistry((Registry)var1x.value()));
      });
      Path var3 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("registries.json");
      return DataProvider.saveStable(var1, var2, var3);
   }

   private static <T> JsonElement dumpRegistry(Registry<T> var0) {
      JsonObject var1 = new JsonObject();
      if (var0 instanceof DefaultedRegistry) {
         ResourceLocation var2 = ((DefaultedRegistry)var0).getDefaultKey();
         var1.addProperty("default", var2.toString());
      }

      int var4 = BuiltInRegistries.REGISTRY.getId(var0);
      var1.addProperty("protocol_id", var4);
      JsonObject var3 = new JsonObject();
      var0.holders().forEach((var2x) -> {
         Object var3x = var2x.value();
         int var4 = var0.getId(var3x);
         JsonObject var5 = new JsonObject();
         var5.addProperty("protocol_id", var4);
         var3.add(var2x.key().location().toString(), var5);
      });
      var1.add("entries", var3);
      return var1;
   }

   public final String getName() {
      return "Registry Dump";
   }
}
