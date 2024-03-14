package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.WorldVersion;
import net.minecraft.server.Bootstrap;
import org.slf4j.Logger;

public class DataGenerator {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path rootOutputFolder;
   private final PackOutput vanillaPackOutput;
   final Set<String> allProviderIds = new HashSet<>();
   final Map<String, DataProvider> providersToRun = new LinkedHashMap<>();
   private final WorldVersion version;
   private final boolean alwaysGenerate;

   public DataGenerator(Path var1, WorldVersion var2, boolean var3) {
      super();
      this.rootOutputFolder = var1;
      this.vanillaPackOutput = new PackOutput(this.rootOutputFolder);
      this.version = var2;
      this.alwaysGenerate = var3;
   }

   public void run() throws IOException {
      HashCache var1 = new HashCache(this.rootOutputFolder, this.allProviderIds, this.version);
      Stopwatch var2 = Stopwatch.createStarted();
      Stopwatch var3 = Stopwatch.createUnstarted();
      this.providersToRun.forEach((var3x, var4) -> {
         if (!this.alwaysGenerate && !var1.shouldRunInThisVersion(var3x)) {
            LOGGER.debug("Generator {} already run for version {}", var3x, this.version.getName());
         } else {
            LOGGER.info("Starting provider: {}", var3x);
            var3.start();
            var1.applyUpdate((HashCache.UpdateResult)var1.generateUpdate(var3x, var4::run).join());
            var3.stop();
            LOGGER.info("{} finished after {} ms", var3x, var3.elapsed(TimeUnit.MILLISECONDS));
            var3.reset();
         }
      });
      LOGGER.info("All providers took: {} ms", var2.elapsed(TimeUnit.MILLISECONDS));
      var1.purgeStaleAndWrite();
   }

   public DataGenerator.PackGenerator getVanillaPack(boolean var1) {
      return new DataGenerator.PackGenerator(var1, "vanilla", this.vanillaPackOutput);
   }

   public DataGenerator.PackGenerator getBuiltinDatapack(boolean var1, String var2) {
      Path var3 = this.vanillaPackOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve("minecraft").resolve("datapacks").resolve(var2);
      return new DataGenerator.PackGenerator(var1, var2, new PackOutput(var3));
   }

   static {
      Bootstrap.bootStrap();
   }

   public class PackGenerator {
      private final boolean toRun;
      private final String providerPrefix;
      private final PackOutput output;

      PackGenerator(boolean var2, String var3, PackOutput var4) {
         super();
         this.toRun = var2;
         this.providerPrefix = var3;
         this.output = var4;
      }

      public <T extends DataProvider> T addProvider(DataProvider.Factory<T> var1) {
         DataProvider var2 = var1.create(this.output);
         String var3 = this.providerPrefix + "/" + var2.getName();
         if (!DataGenerator.this.allProviderIds.add(var3)) {
            throw new IllegalStateException("Duplicate provider: " + var3);
         } else {
            if (this.toRun) {
               DataGenerator.this.providersToRun.put(var3, var2);
            }

            return (T)var2;
         }
      }
   }
}
