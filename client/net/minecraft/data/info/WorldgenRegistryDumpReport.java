package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldgenRegistryDumpReport implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public WorldgenRegistryDumpReport(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) {
      Path var2 = this.generator.getOutputFolder();
      RegistryAccess.RegistryHolder var3 = RegistryAccess.builtin();
      boolean var4 = false;
      MappedRegistry var5 = DimensionType.defaultDimensions(var3, 0L, false);
      NoiseBasedChunkGenerator var6 = WorldGenSettings.makeDefaultOverworld(var3, 0L, false);
      MappedRegistry var7 = WorldGenSettings.withOverworld((Registry)var3.ownedRegistryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), (MappedRegistry)var5, var6);
      RegistryWriteOps var8 = RegistryWriteOps.create(JsonOps.INSTANCE, var3);
      RegistryAccess.knownRegistries().forEach((var4x) -> {
         dumpRegistryCap(var1, var2, var3, var8, var4x);
      });
      dumpRegistry(var2, var1, var8, Registry.LEVEL_STEM_REGISTRY, var7, LevelStem.CODEC);
   }

   private static <T> void dumpRegistryCap(HashCache var0, Path var1, RegistryAccess var2, DynamicOps<JsonElement> var3, RegistryAccess.RegistryData<T> var4) {
      dumpRegistry(var1, var0, var3, var4.key(), var2.ownedRegistryOrThrow(var4.key()), var4.codec());
   }

   private static <E, T extends Registry<E>> void dumpRegistry(Path var0, HashCache var1, DynamicOps<JsonElement> var2, ResourceKey<? extends T> var3, T var4, Encoder<E> var5) {
      Iterator var6 = var4.entrySet().iterator();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         Path var8 = createPath(var0, var3.location(), ((ResourceKey)var7.getKey()).location());
         dumpValue(var8, var1, var2, var5, var7.getValue());
      }

   }

   private static <E> void dumpValue(Path var0, HashCache var1, DynamicOps<JsonElement> var2, Encoder<E> var3, E var4) {
      try {
         Optional var5 = var3.encodeStart(var2, var4).result();
         if (var5.isPresent()) {
            DataProvider.save(GSON, var1, (JsonElement)var5.get(), var0);
         } else {
            LOGGER.error("Couldn't serialize element {}", var0);
         }
      } catch (IOException var6) {
         LOGGER.error("Couldn't save element {}", var0, var6);
      }

   }

   private static Path createPath(Path var0, ResourceLocation var1, ResourceLocation var2) {
      return resolveTopPath(var0).resolve(var2.getNamespace()).resolve(var1.getPath()).resolve(var2.getPath() + ".json");
   }

   private static Path resolveTopPath(Path var0) {
      return var0.resolve("reports").resolve("worldgen");
   }

   public String getName() {
      return "Worldgen";
   }
}
