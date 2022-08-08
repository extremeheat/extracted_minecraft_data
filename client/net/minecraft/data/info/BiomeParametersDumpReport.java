package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path topPath;

   public BiomeParametersDumpReport(DataGenerator var1) {
      super();
      this.topPath = var1.getOutputFolder(DataGenerator.Target.REPORTS).resolve("biome_parameters");
   }

   public void run(CachedOutput var1) {
      RegistryAccess.Frozen var2 = (RegistryAccess.Frozen)RegistryAccess.BUILTIN.get();
      RegistryOps var3 = RegistryOps.create(JsonOps.INSTANCE, var2);
      Registry var4 = var2.registryOrThrow(Registry.BIOME_REGISTRY);
      MultiNoiseBiomeSource.Preset.getPresets().forEach((var4x) -> {
         MultiNoiseBiomeSource var5 = ((MultiNoiseBiomeSource.Preset)var4x.getSecond()).biomeSource(var4, false);
         dumpValue(this.createPath((ResourceLocation)var4x.getFirst()), var1, var3, MultiNoiseBiomeSource.CODEC, var5);
      });
   }

   private static <E> void dumpValue(Path var0, CachedOutput var1, DynamicOps<JsonElement> var2, Encoder<E> var3, E var4) {
      try {
         Optional var5 = var3.encodeStart(var2, var4).resultOrPartial((var1x) -> {
            LOGGER.error("Couldn't serialize element {}: {}", var0, var1x);
         });
         if (var5.isPresent()) {
            DataProvider.saveStable(var1, (JsonElement)var5.get(), var0);
         }
      } catch (IOException var6) {
         LOGGER.error("Couldn't save element {}", var0, var6);
      }

   }

   private Path createPath(ResourceLocation var1) {
      return this.topPath.resolve(var1.getNamespace()).resolve(var1.getPath() + ".json");
   }

   public String getName() {
      return "Biome Parameters";
   }
}
