package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport implements DataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public RegistryDumpReport(DataGenerator var1) {
      this.generator = var1;
   }

   public void run(HashCache var1) throws IOException {
      JsonObject var2 = new JsonObject();
      Registry.REGISTRY.keySet().forEach((var1x) -> {
         var2.add(var1x.toString(), dumpRegistry((WritableRegistry)Registry.REGISTRY.get(var1x)));
      });
      Path var3 = this.generator.getOutputFolder().resolve("reports/registries.json");
      DataProvider.save(GSON, var1, var2, var3);
   }

   private static JsonElement dumpRegistry(WritableRegistry var0) {
      JsonObject var1 = new JsonObject();
      if (var0 instanceof DefaultedRegistry) {
         ResourceLocation var2 = ((DefaultedRegistry)var0).getDefaultKey();
         var1.addProperty("default", var2.toString());
      }

      int var9 = Registry.REGISTRY.getId(var0);
      var1.addProperty("protocol_id", var9);
      JsonObject var3 = new JsonObject();
      Iterator var4 = var0.keySet().iterator();

      while(var4.hasNext()) {
         ResourceLocation var5 = (ResourceLocation)var4.next();
         Object var6 = var0.get(var5);
         int var7 = var0.getId(var6);
         JsonObject var8 = new JsonObject();
         var8.addProperty("protocol_id", var7);
         var3.add(var5.toString(), var8);
      }

      var1.add("entries", var3);
      return var1;
   }

   public String getName() {
      return "Registry Dump";
   }
}
