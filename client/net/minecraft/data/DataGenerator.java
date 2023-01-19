package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.WorldVersion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import org.slf4j.Logger;

public class DataGenerator {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Collection<Path> inputFolders;
   private final Path outputFolder;
   private final List<DataProvider> allProviders = Lists.newArrayList();
   private final List<DataProvider> providersToRun = Lists.newArrayList();
   private final WorldVersion version;
   private final boolean alwaysGenerate;

   public DataGenerator(Path var1, Collection<Path> var2, WorldVersion var3, boolean var4) {
      super();
      this.outputFolder = var1;
      this.inputFolders = var2;
      this.version = var3;
      this.alwaysGenerate = var4;
   }

   public Collection<Path> getInputFolders() {
      return this.inputFolders;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public Path getOutputFolder(DataGenerator.Target var1) {
      return this.getOutputFolder().resolve(var1.directory);
   }

   public void run() throws IOException {
      HashCache var1 = new HashCache(this.outputFolder, this.allProviders, this.version);
      Stopwatch var2 = Stopwatch.createStarted();
      Stopwatch var3 = Stopwatch.createUnstarted();

      for(DataProvider var5 : this.providersToRun) {
         if (!this.alwaysGenerate && !var1.shouldRunInThisVersion(var5)) {
            LOGGER.debug("Generator {} already run for version {}", var5.getName(), this.version.getName());
         } else {
            LOGGER.info("Starting provider: {}", var5.getName());
            var3.start();
            var5.run(var1.getUpdater(var5));
            var3.stop();
            LOGGER.info("{} finished after {} ms", var5.getName(), var3.elapsed(TimeUnit.MILLISECONDS));
            var3.reset();
         }
      }

      LOGGER.info("All providers took: {} ms", var2.elapsed(TimeUnit.MILLISECONDS));
      var1.purgeStaleAndWrite();
   }

   public void addProvider(boolean var1, DataProvider var2) {
      if (var1) {
         this.providersToRun.add(var2);
      }

      this.allProviders.add(var2);
   }

   public DataGenerator.PathProvider createPathProvider(DataGenerator.Target var1, String var2) {
      return new DataGenerator.PathProvider(this, var1, var2);
   }

   static {
      Bootstrap.bootStrap();
   }

   public static class PathProvider {
      private final Path root;
      private final String kind;

      PathProvider(DataGenerator var1, DataGenerator.Target var2, String var3) {
         super();
         this.root = var1.getOutputFolder(var2);
         this.kind = var3;
      }

      public Path file(ResourceLocation var1, String var2) {
         return this.root.resolve(var1.getNamespace()).resolve(this.kind).resolve(var1.getPath() + "." + var2);
      }

      public Path json(ResourceLocation var1) {
         return this.root.resolve(var1.getNamespace()).resolve(this.kind).resolve(var1.getPath() + ".json");
      }
   }

   public static enum Target {
      DATA_PACK("data"),
      RESOURCE_PACK("assets"),
      REPORTS("reports");

      final String directory;

      private Target(String var3) {
         this.directory = var3;
      }
   }
}
