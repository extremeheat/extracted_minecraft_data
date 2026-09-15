package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.WorldVersion;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class HashCache {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String HEADER_MARKER = "// ";
   private final Path rootDir;
   private final Path cacheDir;
   private final String versionId;
   private final Map<String, ProviderCache> caches;
   private final Set<String> cachesToWrite = new HashSet();
   private final Set<Path> cachePaths = new HashSet();
   private final int initialCount;
   private int writes;

   private Path getProviderCachePath(final String provider) {
      return this.cacheDir.resolve(Hashing.sha1().hashString(provider, StandardCharsets.UTF_8).toString());
   }

   public HashCache(final Path rootDir, final Collection<String> providerIds, final WorldVersion version) throws IOException {
      super();
      this.versionId = version.id();
      this.rootDir = rootDir;
      this.cacheDir = rootDir.resolve(".cache");
      Files.createDirectories(this.cacheDir);
      Map<String, ProviderCache> loadedCaches = new HashMap();
      int initialCount = 0;

      for(String providerId : providerIds) {
         Path providerCachePath = this.getProviderCachePath(providerId);
         this.cachePaths.add(providerCachePath);
         ProviderCache providerCache = readCache(rootDir, providerCachePath);
         loadedCaches.put(providerId, providerCache);
         initialCount += providerCache.count();
      }

      this.caches = loadedCaches;
      this.initialCount = initialCount;
   }

   private static ProviderCache readCache(final Path rootDir, final Path providerCachePath) {
      if (Files.isReadable(providerCachePath)) {
         try {
            return HashCache.ProviderCache.load(rootDir, providerCachePath);
         } catch (Exception e) {
            LOGGER.warn("Failed to parse cache {}, discarding", providerCachePath, e);
         }
      }

      return new ProviderCache("unknown", ImmutableMap.of());
   }

   public boolean shouldRunInThisVersion(final String providerId) {
      ProviderCache result = (ProviderCache)this.caches.get(providerId);
      return result == null || !result.version.equals(this.versionId);
   }

   public CompletableFuture<UpdateResult> generateUpdate(final String providerId, final UpdateFunction function) {
      ProviderCache existingCache = (ProviderCache)this.caches.get(providerId);
      if (existingCache == null) {
         throw new IllegalStateException("Provider not registered: " + providerId);
      } else {
         CacheUpdater output = new CacheUpdater(providerId, this.versionId, existingCache);
         return function.update(output).thenApply((unused) -> output.close());
      }
   }

   public void applyUpdate(final UpdateResult result) {
      this.caches.put(result.providerId(), result.cache());
      this.cachesToWrite.add(result.providerId());
      this.writes += result.writes();
   }

   public void purgeStaleAndWrite() throws IOException {
      final Set<Path> allowedFiles = new HashSet();
      this.caches.forEach((providerId, cache) -> {
         if (this.cachesToWrite.contains(providerId)) {
            Path cachePath = this.getProviderCachePath(providerId);
            Path var10001 = this.rootDir;
            String var10003 = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now());
            cache.save(var10001, cachePath, var10003 + "\t" + providerId);
         }

         allowedFiles.addAll(cache.data().keySet());
      });
      final MutableInt found = new MutableInt();
      final MutableInt removed = new MutableInt();
      Files.walkFileTree(this.rootDir, new SimpleFileVisitor<Path>() {
         {
            Objects.requireNonNull(HashCache.this);
         }

         public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
            if (HashCache.this.cachePaths.contains(file)) {
               return FileVisitResult.CONTINUE;
            } else {
               found.increment();
               if (allowedFiles.contains(file)) {
                  return FileVisitResult.CONTINUE;
               } else {
                  try {
                     Files.delete(file);
                  } catch (IOException e) {
                     HashCache.LOGGER.warn("Failed to delete file {}", file, e);
                  }

                  removed.increment();
                  return FileVisitResult.CONTINUE;
               }
            }
         }
      });
      LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{found, this.initialCount, allowedFiles.size(), removed, this.writes});
   }

   private static record ProviderCache(String version, ImmutableMap<Path, HashCode> data) {
      private ProviderCache {
         super();
      }

      public @Nullable HashCode get(final Path path) {
         return (HashCode)this.data.get(path);
      }

      public int count() {
         return this.data.size();
      }

      public static ProviderCache load(final Path rootDir, final Path cacheFile) throws IOException {
         BufferedReader reader = Files.newBufferedReader(cacheFile, StandardCharsets.UTF_8);

         ProviderCache var7;
         try {
            String header = reader.readLine();
            if (!header.startsWith("// ")) {
               throw new IllegalStateException("Missing cache file header");
            }

            String[] headerFields = header.substring("// ".length()).split("\t", 2);
            String savedVersionId = headerFields[0];
            ImmutableMap.Builder<Path, HashCode> result = ImmutableMap.builder();
            reader.lines().forEach((s) -> {
               int i = s.indexOf(32);
               result.put(rootDir.resolve(s.substring(i + 1)), HashCode.fromString(s.substring(0, i)));
            });
            var7 = new ProviderCache(savedVersionId, result.build());
         } catch (Throwable var9) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (reader != null) {
            reader.close();
         }

         return var7;
      }

      public void save(final Path rootDir, final Path cacheFile, final String extraHeaderInfo) {
         try {
            BufferedWriter output = Files.newBufferedWriter(cacheFile, StandardCharsets.UTF_8);

            try {
               output.write("// ");
               output.write(this.version);
               output.write(9);
               output.write(extraHeaderInfo);
               output.newLine();
               UnmodifiableIterator var5 = this.data.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry<Path, HashCode> e = (Map.Entry)var5.next();
                  output.write(((HashCode)e.getValue()).toString());
                  output.write(32);
                  output.write(rootDir.relativize((Path)e.getKey()).toString());
                  output.newLine();
               }
            } catch (Throwable var8) {
               if (output != null) {
                  try {
                     output.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (output != null) {
               output.close();
            }
         } catch (IOException e) {
            HashCache.LOGGER.warn("Unable write cachefile {}", cacheFile, e);
         }

      }
   }

   private static record ProviderCacheBuilder(String version, ConcurrentMap<Path, HashCode> data) {
      public ProviderCacheBuilder(final String version) {
         this(version, new ConcurrentHashMap());
      }

      private ProviderCacheBuilder {
         super();
      }

      public void put(final Path path, final HashCode hash) {
         this.data.put(path, hash);
      }

      public ProviderCache build() {
         return new ProviderCache(this.version, ImmutableMap.copyOf(this.data));
      }
   }

   private static class CacheUpdater implements CachedOutput {
      private final String provider;
      private final ProviderCache oldCache;
      private final ProviderCacheBuilder newCache;
      private final AtomicInteger writes = new AtomicInteger();
      private volatile boolean closed;

      private CacheUpdater(final String provider, final String newVersionId, final ProviderCache oldCache) {
         super();
         this.provider = provider;
         this.oldCache = oldCache;
         this.newCache = new ProviderCacheBuilder(newVersionId);
      }

      private boolean shouldWrite(final Path path, final HashCode hash) {
         return !Objects.equals(this.oldCache.get(path), hash) || !Files.exists(path, new LinkOption[0]);
      }

      public void writeIfNeeded(final Path path, final byte[] input, final HashCode hash) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("Cannot write to cache as it has already been closed");
         } else {
            if (this.shouldWrite(path, hash)) {
               this.writes.incrementAndGet();
               Files.createDirectories(path.getParent());
               Files.write(path, input, new OpenOption[0]);
            }

            this.newCache.put(path, hash);
         }
      }

      public UpdateResult close() {
         this.closed = true;
         return new UpdateResult(this.provider, this.newCache.build(), this.writes.get());
      }
   }

   public static record UpdateResult(String providerId, ProviderCache cache, int writes) {
      public UpdateResult {
         super();
      }
   }

   @FunctionalInterface
   public interface UpdateFunction {
      CompletableFuture<?> update(CachedOutput output);
   }
}
