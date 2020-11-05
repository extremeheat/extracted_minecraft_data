package net.minecraft.data.worldgen.biome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeReport implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BiomeReport(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) {
      Path var2 = this.generator.getOutputFolder();
      Iterator var3 = BuiltinRegistries.BIOME.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Path var5 = createPath(var2, ((ResourceKey)var4.getKey()).location());
         Biome var6 = (Biome)var4.getValue();
         Function var7 = JsonOps.INSTANCE.withEncoder(Biome.CODEC);

         try {
            Optional var8 = ((DataResult)var7.apply(() -> {
               return var6;
            })).result();
            if (var8.isPresent()) {
               DataProvider.save(GSON, var1, (JsonElement)var8.get(), var5);
            } else {
               LOGGER.error("Couldn't serialize biome {}", var5);
            }
         } catch (IOException var9) {
            LOGGER.error("Couldn't save biome {}", var5, var9);
         }
      }

   }

   private static Path createPath(Path var0, ResourceLocation var1) {
      return var0.resolve("reports/biomes/" + var1.getPath() + ".json");
   }

   public String getName() {
      return "Biomes";
   }
}
