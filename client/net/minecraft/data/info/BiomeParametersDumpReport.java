package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.slf4j.Logger;

public class BiomeParametersDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path topPath;
   private final CompletableFuture<HolderLookup.Provider> registries;
   private static final MapCodec<ResourceKey<Biome>> ENTRY_CODEC;
   private static final Codec<Climate.ParameterList<ResourceKey<Biome>>> CODEC;

   public BiomeParametersDumpReport(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.topPath = var1.getOutputFolder(PackOutput.Target.REPORTS).resolve("biome_parameters");
      this.registries = var2;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      return this.registries.thenCompose((var2) -> {
         RegistryOps var3 = var2.createSerializationContext(JsonOps.INSTANCE);
         ArrayList var4 = new ArrayList();
         MultiNoiseBiomeSourceParameterList.knownPresets().forEach((var4x, var5) -> {
            var4.add(dumpValue(this.createPath(var4x.id()), var1, var3, CODEC, var5));
         });
         return CompletableFuture.allOf((CompletableFuture[])var4.toArray((var0) -> {
            return new CompletableFuture[var0];
         }));
      });
   }

   private static <E> CompletableFuture<?> dumpValue(Path var0, CachedOutput var1, DynamicOps<JsonElement> var2, Encoder<E> var3, E var4) {
      Optional var5 = var3.encodeStart(var2, var4).resultOrPartial((var1x) -> {
         LOGGER.error("Couldn't serialize element {}: {}", var0, var1x);
      });
      return var5.isPresent() ? DataProvider.saveStable(var1, (JsonElement)var5.get(), var0) : CompletableFuture.completedFuture((Object)null);
   }

   private Path createPath(ResourceLocation var1) {
      return this.topPath.resolve(var1.getNamespace()).resolve(var1.getPath() + ".json");
   }

   public final String getName() {
      return "Biome Parameters";
   }

   static {
      ENTRY_CODEC = ResourceKey.codec(Registries.BIOME).fieldOf("biome");
      CODEC = Climate.ParameterList.codec(ENTRY_CODEC).fieldOf("biomes").codec();
   }
}
