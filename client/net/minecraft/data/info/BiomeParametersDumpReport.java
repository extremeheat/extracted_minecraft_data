package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path topPath;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public BiomeParametersDumpReport(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.topPath = var1.getOutputFolder(PackOutput.Target.REPORTS).resolve("biome_parameters");
      this.registries = var2;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      return this.registries.thenCompose(var2 -> {
         RegistryOps var3 = RegistryOps.create(JsonOps.INSTANCE, var2);
         HolderLookup.RegistryLookup var4 = var2.lookupOrThrow(Registries.BIOME);
         return CompletableFuture.allOf(MultiNoiseBiomeSource.Preset.getPresets().map(var4x -> {
            MultiNoiseBiomeSource var5 = ((MultiNoiseBiomeSource.Preset)var4x.getSecond()).biomeSource(var4, false);
            return dumpValue(this.createPath((ResourceLocation)var4x.getFirst()), var1, var3, MultiNoiseBiomeSource.CODEC, var5);
         }).toArray(var0 -> new CompletableFuture[var0]));
      });
   }

   private static <E> CompletableFuture<?> dumpValue(Path var0, CachedOutput var1, DynamicOps<JsonElement> var2, Encoder<E> var3, E var4) {
      Optional var5 = var3.encodeStart(var2, var4).resultOrPartial(var1x -> LOGGER.error("Couldn't serialize element {}: {}", var0, var1x));
      return var5.isPresent() ? DataProvider.saveStable(var1, (JsonElement)var5.get(), var0) : CompletableFuture.completedFuture(null);
   }

   private Path createPath(ResourceLocation var1) {
      return this.topPath.resolve(var1.getNamespace()).resolve(var1.getPath() + ".json");
   }

   @Override
   public final String getName() {
      return "Biome Parameters";
   }
}
