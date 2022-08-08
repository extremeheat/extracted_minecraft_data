package net.minecraft.data;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
   private final Map<DataProvider, ProviderCache> existingCaches;
   private final Map<DataProvider, CacheUpdater> cachesToWrite = new HashMap();
   private final Set<Path> cachePaths = new HashSet();
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

      ProviderCache var9;
      for(Iterator var6 = var2.iterator(); var6.hasNext(); var5 += var9.count()) {
         DataProvider var7 = (DataProvider)var6.next();
         Path var8 = this.getProviderCachePath(var7);
         this.cachePaths.add(var8);
         var9 = readCache(var1, var8);
         var4.put(var7, var9);
      }

      this.existingCaches = var4;
      this.initialCount = var5;
   }

   private static ProviderCache readCache(Path var0, Path var1) {
      if (Files.isReadable(var1)) {
         try {
            return HashCache.ProviderCache.load(var0, var1);
         } catch (Exception var3) {
            LOGGER.warn("Failed to parse cache {}, discarding", var1, var3);
         }
      }

      return new ProviderCache("unknown");
   }

   public boolean shouldRunInThisVersion(DataProvider var1) {
      ProviderCache var2 = (ProviderCache)this.existingCaches.get(var1);
      return var2 == null || !var2.version.equals(this.versionId);
   }

   public CachedOutput getUpdater(DataProvider var1) {
      return (CachedOutput)this.cachesToWrite.computeIfAbsent(var1, (var1x) -> {
         ProviderCache var2 = (ProviderCache)this.existingCaches.get(var1x);
         if (var2 == null) {
            throw new IllegalStateException("Provider not registered: " + var1x.getName());
         } else {
            CacheUpdater var3 = new CacheUpdater(this.versionId, var2);
            this.existingCaches.put(var1x, var3.newCache);
            return var3;
         }
      });
   }

   public void purgeStaleAndWrite() throws IOException {
      MutableInt var1 = new MutableInt();
      this.cachesToWrite.forEach((var2x, var3x) -> {
         Path var4 = this.getProviderCachePath(var2x);
         ProviderCache var10000 = var3x.newCache;
         Path var10001 = this.rootDir;
         String var10003 = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
         var10000.save(var10001, var4, var10003 + "\t" + var2x.getName());
         var1.add(var3x.writes);
      });
      HashSet var2 = new HashSet();
      this.existingCaches.values().forEach((var1x) -> {
         var2.addAll(var1x.data().keySet());
      });
      var2.add(this.rootDir.resolve("version.json"));
      MutableInt var3 = new MutableInt();
      MutableInt var4 = new MutableInt();
      Stream var5 = Files.walk(this.rootDir);

      try {
         var5.forEach((var4x) -> {
            if (!Files.isDirectory(var4x, new LinkOption[0])) {
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
      } catch (Throwable var9) {
         if (var5 != null) {
            try {
               var5.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (var5 != null) {
         var5.close();
      }

      LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{var3, this.initialCount, var2.size(), var4, var1});
   }

   static record ProviderCache(String a, Map<Path, HashCode> b) {
      final String version;
      private final Map<Path, HashCode> data;

      ProviderCache(String var1) {
         this(var1, new HashMap());
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

      public static ProviderCache load(Path var0, Path var1) throws IOException {
         BufferedReader var2 = Files.newBufferedReader(var1, StandardCharsets.UTF_8);

         ProviderCache var7;
         try {
            String var3 = var2.readLine();
            if (!var3.startsWith("// ")) {
               throw new IllegalStateException("Missing cache file header");
            }

            String[] var4 = var3.substring("// ".length()).split("\t", 2);
            String var5 = var4[0];
            HashMap var6 = new HashMap();
            var2.lines().forEach((var2x) -> {
               int var3 = var2x.indexOf(32);
               var6.put(var0.resolve(var2x.substring(var3 + 1)), HashCode.fromString(var2x.substring(0, var3)));
            });
            var7 = new ProviderCache(var5, Map.copyOf(var6));
         } catch (Throwable var9) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var2 != null) {
            var2.close();
         }

         return var7;
      }

      public void save(Path var1, Path var2, String var3) {
         try {
            BufferedWriter var4 = Files.newBufferedWriter(var2, StandardCharsets.UTF_8);

            try {
               var4.write("// ");
               var4.write(this.version);
               var4.write(9);
               var4.write(var3);
               var4.newLine();
               Iterator var5 = this.data.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry var6 = (Map.Entry)var5.next();
                  var4.write(((HashCode)var6.getValue()).toString());
                  var4.write(32);
                  var4.write(var1.relativize((Path)var6.getKey()).toString());
                  var4.newLine();
               }
            } catch (Throwable var8) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (IOException var9) {
            HashCache.LOGGER.warn("Unable write cachefile {}: {}", var2, var9);
         }

      }

      public String version() {
         return this.version;
      }

      public Map<Path, HashCode> data() {
         return this.data;
      }
   }

   static class CacheUpdater implements CachedOutput {
      private final ProviderCache oldCache;
      final ProviderCache newCache;
      int writes;

      CacheUpdater(String var1, ProviderCache var2) {
         super();
         this.oldCache = var2;
         this.newCache = new ProviderCache(var1);
      }

      private boolean shouldWrite(Path var1, HashCode var2) {
         return !Objects.equals(this.oldCache.get(var1), var2) || !Files.exists(var1, new LinkOption[0]);
      }

      public void writeIfNeeded(Path var1, byte[] var2, HashCode var3) throws IOException {
         if (this.shouldWrite(var1, var3)) {
            ++this.writes;
            Files.createDirectories(var1.getParent());
            Files.write(var1, var2, new OpenOption[0]);
         }

         this.newCache.put(var1, var3);
      }
   }
}
