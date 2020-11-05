package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HashCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path path;
   private final Path cachePath;
   private int hits;
   private final Map<Path, String> oldCache = Maps.newHashMap();
   private final Map<Path, String> newCache = Maps.newHashMap();
   private final Set<Path> keep = Sets.newHashSet();

   public HashCache(Path var1, String var2) throws IOException {
      super();
      this.path = var1;
      Path var3 = var1.resolve(".cache");
      Files.createDirectories(var3);
      this.cachePath = var3.resolve(var2);
      this.walkOutputFiles().forEach((var1x) -> {
         String var10000 = (String)this.oldCache.put(var1x, "");
      });
      if (Files.isReadable(this.cachePath)) {
         IOUtils.readLines(Files.newInputStream(this.cachePath), Charsets.UTF_8).forEach((var2x) -> {
            int var3 = var2x.indexOf(32);
            this.oldCache.put(var1.resolve(var2x.substring(var3 + 1)), var2x.substring(0, var3));
         });
      }

   }

   public void purgeStaleAndWrite() throws IOException {
      this.removeStale();

      BufferedWriter var1;
      try {
         var1 = Files.newBufferedWriter(this.cachePath);
      } catch (IOException var3) {
         LOGGER.warn("Unable write cachefile {}: {}", this.cachePath, var3.toString());
         return;
      }

      IOUtils.writeLines((Collection)this.newCache.entrySet().stream().map((var1x) -> {
         return (String)var1x.getValue() + ' ' + this.path.relativize((Path)var1x.getKey());
      }).collect(Collectors.toList()), System.lineSeparator(), var1);
      var1.close();
      LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.hits, this.newCache.size() - this.hits, this.oldCache.size());
   }

   @Nullable
   public String getHash(Path var1) {
      return (String)this.oldCache.get(var1);
   }

   public void putNew(Path var1, String var2) {
      this.newCache.put(var1, var2);
      if (Objects.equals(this.oldCache.remove(var1), var2)) {
         ++this.hits;
      }

   }

   public boolean had(Path var1) {
      return this.oldCache.containsKey(var1);
   }

   public void keep(Path var1) {
      this.keep.add(var1);
   }

   private void removeStale() throws IOException {
      this.walkOutputFiles().forEach((var1) -> {
         if (this.had(var1) && !this.keep.contains(var1)) {
            try {
               Files.delete(var1);
            } catch (IOException var3) {
               LOGGER.debug("Unable to delete: {} ({})", var1, var3.toString());
            }
         }

      });
   }

   private Stream<Path> walkOutputFiles() throws IOException {
      return Files.walk(this.path).filter((var1) -> {
         return !Objects.equals(this.cachePath, var1) && !Files.isDirectory(var1, new LinkOption[0]);
      });
   }
}
