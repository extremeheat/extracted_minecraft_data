package net.minecraft.data;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.WorldVersion;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class HashCache {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String HEADER_MARKER = "// ";
   private final Path rootDir;
   private final Path cacheDir;
   private final String versionId;
   private final Map<DataProvider, HashCache.ProviderCache> existingCaches;
   private final Map<DataProvider, HashCache.CacheUpdater> cachesToWrite = new HashMap<>();
   private final Set<Path> cachePaths = new HashSet<>();
   private final int initialCount;

   private Path getProviderCachePath(DataProvider var1) {
      return this.cacheDir.resolve(Hashing.sha1().hashString(var1.getName(), StandardCharsets.UTF_8).toString());
   }

   public HashCache(Path var1, List<DataProvider> var2, WorldVersion var3) throws IOException {
      super();
      this.versionId = var3.getName();
      this.rootDir = var1;
      this.cacheDir = var1.resolve(".cache");
      Files.createDirectories(this.cacheDir);
      HashMap var4 = new HashMap();
      int var5 = 0;

      for(DataProvider var7 : var2) {
         Path var8 = this.getProviderCachePath(var7);
         this.cachePaths.add(var8);
         HashCache.ProviderCache var9 = readCache(var1, var8);
         var4.put(var7, var9);
         var5 += var9.count();
      }

      this.existingCaches = var4;
      this.initialCount = var5;
   }

   private static HashCache.ProviderCache readCache(Path var0, Path var1) {
      if (Files.isReadable(var1)) {
         try {
            return HashCache.ProviderCache.load(var0, var1);
         } catch (Exception var3) {
            LOGGER.warn("Failed to parse cache {}, discarding", var1, var3);
         }
      }

      return new HashCache.ProviderCache("unknown");
   }

   public boolean shouldRunInThisVersion(DataProvider var1) {
      HashCache.ProviderCache var2 = this.existingCaches.get(var1);
      return var2 == null || !var2.version.equals(this.versionId);
   }

   public CachedOutput getUpdater(DataProvider var1) {
      return this.cachesToWrite.computeIfAbsent(var1, var1x -> {
         HashCache.ProviderCache var2 = this.existingCaches.get(var1x);
         if (var2 == null) {
            throw new IllegalStateException("Provider not registered: " + var1x.getName());
         } else {
            HashCache.CacheUpdater var3 = new HashCache.CacheUpdater(this.versionId, var2);
            this.existingCaches.put(var1x, var3.newCache);
            return var3;
         }
      });
   }

   public void purgeStaleAndWrite() throws IOException {
      MutableInt var1 = new MutableInt();
      this.cachesToWrite.forEach((var2x, var3x) -> {
         Path var4x = this.getProviderCachePath(var2x);
         var3x.newCache.save(this.rootDir, var4x, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + var2x.getName());
         var1.add(var3x.writes);
      });
      HashSet var2 = new HashSet();
      this.existingCaches.values().forEach(var1x -> var2.addAll(var1x.data().keySet()));
      var2.add(this.rootDir.resolve("version.json"));
      MutableInt var3 = new MutableInt();
      MutableInt var4 = new MutableInt();

      try (Stream var5 = Files.walk(this.rootDir)) {
         var5.forEach(var4x -> {
            if (!Files.isDirectory(var4x)) {
               if (!this.cachePaths.contains(var4x)) {
                  var3.increment();
                  if (!var2.contains(var4x)) {
                     try {
                        Files.delete(var4x);
                     } catch (IOException var6) {
                        LOGGER.warn("Failed to delete file {}", var4x, var6);
                     }

                     var4.increment();
                  }
               }
            }
         });
      }

      LOGGER.info(
         "Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}",
         new Object[]{var3, this.initialCount, var2.size(), var4, var1}
      );
   }

   static class CacheUpdater implements CachedOutput {
      private final HashCache.ProviderCache oldCache;
      final HashCache.ProviderCache newCache;
      int writes;

      CacheUpdater(String var1, HashCache.ProviderCache var2) {
         super();
         this.oldCache = var2;
         this.newCache = new HashCache.ProviderCache(var1);
      }

      private boolean shouldWrite(Path var1, HashCode var2) {
         return !Objects.equals(this.oldCache.get(var1), var2) || !Files.exists(var1);
      }

      @Override
      public void writeIfNeeded(Path var1, byte[] var2, HashCode var3) throws IOException {
         if (this.shouldWrite(var1, var3)) {
            ++this.writes;
            Files.createDirectories(var1.getParent());
            Files.write(var1, var2);
         }

         this.newCache.put(var1, var3);
      }
   }

   static record ProviderCache(String a, Map<Path, HashCode> b) {
      final String version;
      private final Map<Path, HashCode> data;

      ProviderCache(String var1) {
         this(var1, new HashMap<>());
      }

      private ProviderCache(String var1, Map<Path, HashCode> var2) {
         super();
         this.version = var1;
         this.data = var2;
      }

      @Nullable
      public HashCode get(Path var1) {
         return (HashCode)this.data.get(var1);
      }

      public void put(Path var1, HashCode var2) {
         this.data.put(var1, var2);
      }

      public int count() {
         return this.data.size();
      }

      public static HashCache.ProviderCache load(Path var0, Path var1) throws IOException {
         HashCache.ProviderCache var7;
         try (BufferedReader var2 = Files.newBufferedReader(var1, StandardCharsets.UTF_8)) {
            String var3 = var2.readLine();
            if (!var3.startsWith("// ")) {
               throw new IllegalStateException("Missing cache file header");
            }

            String[] var4 = var3.substring("// ".length()).split("\t", 2);
            String var5 = var4[0];
            HashMap var6 = new HashMap();
            var2.lines().forEach(var2x -> {
               int var3x = var2x.indexOf(32);
               var6.put(var0.resolve(var2x.substring(var3x + 1)), HashCode.fromString(var2x.substring(0, var3x)));
            });
            var7 = new HashCache.ProviderCache(var5, Map.copyOf(var6));
         }

         return var7;
      }

      public void save(Path var1, Path var2, String var3) {
         try (BufferedWriter var4 = Files.newBufferedWriter(var2, StandardCharsets.UTF_8)) {
            var4.write("// ");
            var4.write(this.version);
            var4.write(9);
            var4.write(var3);
            var4.newLine();

            for(Entry var6 : this.data.entrySet()) {
               var4.write(((HashCode)var6.getValue()).toString());
               var4.write(32);
               var4.write(var1.relativize((Path)var6.getKey()).toString());
               var4.newLine();
            }
         } catch (IOException var9) {
            HashCache.LOGGER.warn("Unable write cachefile {}: {}", var2, var9);
         }
      }
   }
}
