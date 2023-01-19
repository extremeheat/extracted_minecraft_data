package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
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
   private final Map<String, HashCache.ProviderCache> caches;
   private final Set<String> cachesToWrite = new HashSet<>();
   private final Set<Path> cachePaths = new HashSet<>();
   private final int initialCount;
   private int writes;

   private Path getProviderCachePath(String var1) {
      return this.cacheDir.resolve(Hashing.sha1().hashString(var1, StandardCharsets.UTF_8).toString());
   }

   public HashCache(Path var1, Collection<String> var2, WorldVersion var3) throws IOException {
      super();
      this.versionId = var3.getName();
      this.rootDir = var1;
      this.cacheDir = var1.resolve(".cache");
      Files.createDirectories(this.cacheDir);
      HashMap var4 = new HashMap();
      int var5 = 0;

      for(String var7 : var2) {
         Path var8 = this.getProviderCachePath(var7);
         this.cachePaths.add(var8);
         HashCache.ProviderCache var9 = readCache(var1, var8);
         var4.put(var7, var9);
         var5 += var9.count();
      }

      this.caches = var4;
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

      return new HashCache.ProviderCache("unknown", ImmutableMap.of());
   }

   public boolean shouldRunInThisVersion(String var1) {
      HashCache.ProviderCache var2 = this.caches.get(var1);
      return var2 == null || !var2.version.equals(this.versionId);
   }

   public CompletableFuture<HashCache.UpdateResult> generateUpdate(String var1, HashCache.UpdateFunction var2) {
      HashCache.ProviderCache var3 = this.caches.get(var1);
      if (var3 == null) {
         throw new IllegalStateException("Provider not registered: " + var1);
      } else {
         HashCache.CacheUpdater var4 = new HashCache.CacheUpdater(var1, this.versionId, var3);
         return var2.update(var4).thenApply(var1x -> var4.close());
      }
   }

   public void applyUpdate(HashCache.UpdateResult var1) {
      this.caches.put(var1.providerId(), var1.cache());
      this.cachesToWrite.add(var1.providerId());
      this.writes += var1.writes();
   }

   public void purgeStaleAndWrite() throws IOException {
      HashSet var1 = new HashSet();
      this.caches.forEach((var2x, var3x) -> {
         if (this.cachesToWrite.contains(var2x)) {
            Path var4x = this.getProviderCachePath(var2x);
            var3x.save(this.rootDir, var4x, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + var2x);
         }

         var1.addAll(var3x.data().keySet());
      });
      var1.add(this.rootDir.resolve("version.json"));
      MutableInt var2 = new MutableInt();
      MutableInt var3 = new MutableInt();

      try (Stream var4 = Files.walk(this.rootDir)) {
         var4.forEach(var4x -> {
            if (!Files.isDirectory(var4x)) {
               if (!this.cachePaths.contains(var4x)) {
                  var2.increment();
                  if (!var1.contains(var4x)) {
                     try {
                        Files.delete(var4x);
                     } catch (IOException var6) {
                        LOGGER.warn("Failed to delete file {}", var4x, var6);
                     }

                     var3.increment();
                  }
               }
            }
         });
      }

      LOGGER.info(
         "Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}",
         new Object[]{var2, this.initialCount, var1.size(), var3, this.writes}
      );
   }

   class CacheUpdater implements CachedOutput {
      private final String provider;
      private final HashCache.ProviderCache oldCache;
      private final HashCache.ProviderCacheBuilder newCache;
      private final AtomicInteger writes = new AtomicInteger();
      private volatile boolean closed;

      CacheUpdater(String var2, String var3, HashCache.ProviderCache var4) {
         super();
         this.provider = var2;
         this.oldCache = var4;
         this.newCache = new HashCache.ProviderCacheBuilder(var3);
      }

      private boolean shouldWrite(Path var1, HashCode var2) {
         return !Objects.equals(this.oldCache.get(var1), var2) || !Files.exists(var1);
      }

      @Override
      public void writeIfNeeded(Path var1, byte[] var2, HashCode var3) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("Cannot write to cache as it has already been closed");
         } else {
            if (this.shouldWrite(var1, var3)) {
               this.writes.incrementAndGet();
               Files.createDirectories(var1.getParent());
               Files.write(var1, var2);
            }

            this.newCache.put(var1, var3);
         }
      }

      public HashCache.UpdateResult close() {
         this.closed = true;
         return new HashCache.UpdateResult(this.provider, this.newCache.build(), this.writes.get());
      }
   }

   static record ProviderCache(String a, ImmutableMap<Path, HashCode> b) {
      final String version;
      private final ImmutableMap<Path, HashCode> data;

      ProviderCache(String var1, ImmutableMap<Path, HashCode> var2) {
         super();
         this.version = var1;
         this.data = var2;
      }

      @Nullable
      public HashCode get(Path var1) {
         return (HashCode)this.data.get(var1);
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
            Builder var6 = ImmutableMap.builder();
            var2.lines().forEach(var2x -> {
               int var3x = var2x.indexOf(32);
               var6.put(var0.resolve(var2x.substring(var3x + 1)), HashCode.fromString(var2x.substring(0, var3x)));
            });
            var7 = new HashCache.ProviderCache(var5, var6.build());
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
            UnmodifiableIterator var5 = this.data.entrySet().iterator();

            while(var5.hasNext()) {
               Entry var6 = (Entry)var5.next();
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

   static record ProviderCacheBuilder(String a, ConcurrentMap<Path, HashCode> b) {
      private final String version;
      private final ConcurrentMap<Path, HashCode> data;

      ProviderCacheBuilder(String var1) {
         this(var1, new ConcurrentHashMap<>());
      }

      private ProviderCacheBuilder(String var1, ConcurrentMap<Path, HashCode> var2) {
         super();
         this.version = var1;
         this.data = var2;
      }

      public void put(Path var1, HashCode var2) {
         this.data.put(var1, var2);
      }

      public HashCache.ProviderCache build() {
         return new HashCache.ProviderCache(this.version, ImmutableMap.copyOf(this.data));
      }
   }

   @FunctionalInterface
   public interface UpdateFunction {
      CompletableFuture<?> update(CachedOutput var1);
   }

   public static record UpdateResult(String a, HashCache.ProviderCache b, int c) {
      private final String providerId;
      private final HashCache.ProviderCache cache;
      private final int writes;

      public UpdateResult(String var1, HashCache.ProviderCache var2, int var3) {
         super();
         this.providerId = var1;
         this.cache = var2;
         this.writes = var3;
      }
   }
}
