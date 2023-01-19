package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public class WorldgenRegistryDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataGenerator generator;

   public WorldgenRegistryDumpReport(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   @Override
   public void run(CachedOutput var1) {
      RegistryAccess var2 = RegistryAccess.BUILTIN.get();
      RegistryOps var3 = RegistryOps.create(JsonOps.INSTANCE, var2);
      RegistryAccess.knownRegistries().forEach(var4 -> this.dumpRegistryCap(var1, var2, var3, var4));
   }

   private <T> void dumpRegistryCap(CachedOutput var1, RegistryAccess var2, DynamicOps<JsonElement> var3, RegistryAccess.RegistryData<T> var4) {
      ResourceKey var5 = var4.key();
      Registry var6 = var2.ownedRegistryOrThrow(var5);
      DataGenerator.PathProvider var7 = this.generator.createPathProvider(DataGenerator.Target.REPORTS, var5.location().getPath());

      for(Entry var9 : var6.entrySet()) {
         dumpValue(var7.json(((ResourceKey)var9.getKey()).location()), var1, var3, var4.codec(), var9.getValue());
      }
   }

   private static <E> void dumpValue(Path var0, CachedOutput var1, DynamicOps<JsonElement> var2, Encoder<E> var3, E var4) {
      try {
         Optional var5 = var3.encodeStart(var2, var4).resultOrPartial(var1x -> LOGGER.error("Couldn't serialize element {}: {}", var0, var1x));
         if (var5.isPresent()) {
            DataProvider.saveStable(var1, (JsonElement)var5.get(), var0);
         }
      } catch (IOException var6) {
         LOGGER.error("Couldn't save element {}", var0, var6);
      }
   }

   @Override
   public String getName() {
      return "Worldgen";
   }
}
