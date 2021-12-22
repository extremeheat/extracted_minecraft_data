package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Collection<Path> inputFolders;
   private final Path outputFolder;
   private final List<DataProvider> providers = Lists.newArrayList();

   public DataGenerator(Path var1, Collection<Path> var2) {
      super();
      this.outputFolder = var1;
      this.inputFolders = var2;
   }

   public Collection<Path> getInputFolders() {
      return this.inputFolders;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public void run() throws IOException {
      HashCache var1 = new HashCache(this.outputFolder, "cache");
      var1.keep(this.getOutputFolder().resolve("version.json"));
      Stopwatch var2 = Stopwatch.createStarted();
      Stopwatch var3 = Stopwatch.createUnstarted();
      Iterator var4 = this.providers.iterator();

      while(var4.hasNext()) {
         DataProvider var5 = (DataProvider)var4.next();
         LOGGER.info("Starting provider: {}", var5.getName());
         var3.start();
         var5.run(var1);
         var3.stop();
         LOGGER.info("{} finished after {} ms", var5.getName(), var3.elapsed(TimeUnit.MILLISECONDS));
         var3.reset();
      }

      LOGGER.info("All providers took: {} ms", var2.elapsed(TimeUnit.MILLISECONDS));
      var1.purgeStaleAndWrite();
   }

   public void addProvider(DataProvider var1) {
      this.providers.add(var1);
   }

   static {
      Bootstrap.bootStrap();
   }
}
