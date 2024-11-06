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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
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
   private final Map<String, ProviderCache> caches;
   private final Set<String> cachesToWrite = new HashSet();
   final Set<Path> cachePaths = new HashSet();
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

      ProviderCache var9;
      for(Iterator var6 = var2.iterator(); var6.hasNext(); var5 += var9.count()) {
         String var7 = (String)var6.next();
         Path var8 = this.getProviderCachePath(var7);
         this.cachePaths.add(var8);
         var9 = readCache(var1, var8);
         var4.put(var7, var9);
      }

      this.caches = var4;
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

      return new ProviderCache("unknown", ImmutableMap.of());
   }

   public boolean shouldRunInThisVersion(String var1) {
      ProviderCache var2 = (ProviderCache)this.caches.get(var1);
      return var2 == null || !var2.version.equals(this.versionId);
   }

   public CompletableFuture<UpdateResult> generateUpdate(String var1, UpdateFunction var2) {
      ProviderCache var3 = (ProviderCache)this.caches.get(var1);
      if (var3 == null) {
         throw new IllegalStateException("Provider not registered: " + var1);
      } else {
         CacheUpdater var4 = new CacheUpdater(var1, this.versionId, var3);
         return var2.update(var4).thenApply((var1x) -> {
            return var4.close();
         });
      }
   }

   public void applyUpdate(UpdateResult var1) {
      this.caches.put(var1.providerId(), var1.cache());
      this.cachesToWrite.add(var1.providerId());
      this.writes += var1.writes();
   }

   public void purgeStaleAndWrite() throws IOException {
      final HashSet var1 = new HashSet();
      this.caches.forEach((var2x, var3x) -> {
         if (this.cachesToWrite.contains(var2x)) {
            Path var4 = this.getProviderCachePath(var2x);
            Path var10001 = this.rootDir;
            String var10003 = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
            var3x.save(var10001, var4, var10003 + "\t" + var2x);
         }

         var1.addAll(var3x.data().keySet());
      });
      var1.add(this.rootDir.resolve("version.json"));
      final MutableInt var2 = new MutableInt();
      final MutableInt var3 = new MutableInt();
      Files.walkFileTree(this.rootDir, new SimpleFileVisitor<Path>() {
         public FileVisitResult visitFile(Path var1x, BasicFileAttributes var2x) {
            if (HashCache.this.cachePaths.contains(var1x)) {
               return FileVisitResult.CONTINUE;
            } else {
               var2.increment();
               if (var1.contains(var1x)) {
                  return FileVisitResult.CONTINUE;
               } else {
                  try {
                     Files.delete(var1x);
                  } catch (IOException var4) {
                     HashCache.LOGGER.warn("Failed to delete file {}", var1x, var4);
                  }

                  var3.increment();
                  return FileVisitResult.CONTINUE;
               }
            }
         }

         // $FF: synthetic method
         public FileVisitResult visitFile(final Object var1x, final BasicFileAttributes var2x) throws IOException {
            return this.visitFile((Path)var1x, var2x);
         }
      });
      LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{var2, this.initialCount, var1.size(), var3, this.writes});
   }

   static record ProviderCache(String version, ImmutableMap<Path, HashCode> data) {
      final String version;

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
            ImmutableMap.Builder var6 = ImmutableMap.builder();
            var2.lines().forEach((var2x) -> {
               int var3 = var2x.indexOf(32);
               var6.put(var0.resolve(var2x.substring(var3 + 1)), HashCode.fromString(var2x.substring(0, var3)));
            });
            var7 = new ProviderCache(var5, var6.build());
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
               UnmodifiableIterator var5 = this.data.entrySet().iterator();

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

      public ImmutableMap<Path, HashCode> data() {
         return this.data;
      }
   }

   static class CacheUpdater implements CachedOutput {
      private final String provider;
      private final ProviderCache oldCache;
      private final ProviderCacheBuilder newCache;
      private final AtomicInteger writes = new AtomicInteger();
      private volatile boolean closed;

      CacheUpdater(String var1, String var2, ProviderCache var3) {
         super();
         this.provider = var1;
         this.oldCache = var3;
         this.newCache = new ProviderCacheBuilder(var2);
      }

      private boolean shouldWrite(Path var1, HashCode var2) {
         return !Objects.equals(this.oldCache.get(var1), var2) || !Files.exists(var1, new LinkOption[0]);
      }

      public void writeIfNeeded(Path var1, byte[] var2, HashCode var3) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("Cannot write to cache as it has already been closed");
         } else {
            if (this.shouldWrite(var1, var3)) {
               this.writes.incrementAndGet();
               Files.createDirectories(var1.getParent());
               Files.write(var1, var2, new OpenOption[0]);
            }

            this.newCache.put(var1, var3);
         }
      }

      public UpdateResult close() {
         this.closed = true;
         return new UpdateResult(this.provider, this.newCache.build(), this.writes.get());
      }
   }

   @FunctionalInterface
   public interface UpdateFunction {
      CompletableFuture<?> update(CachedOutput var1);
   }

   public static record UpdateResult(String providerId, ProviderCache cache, int writes) {
      public UpdateResult(String var1, ProviderCache var2, int var3) {
         super();
         this.providerId = var1;
         this.cache = var2;
         this.writes = var3;
      }

      public String providerId() {
         return this.providerId;
      }

      public ProviderCache cache() {
         return this.cache;
      }

      public int writes() {
         return this.writes;
      }
   }

   static record ProviderCacheBuilder(String version, ConcurrentMap<Path, HashCode> data) {
      ProviderCacheBuilder(String var1) {
         this(var1, new ConcurrentHashMap());
      }

      private ProviderCacheBuilder(String var1, ConcurrentMap<Path, HashCode> var2) {
         super();
         this.version = var1;
         this.data = var2;
      }

      public void put(Path var1, HashCode var2) {
         this.data.put(var1, var2);
      }

      public ProviderCache build() {
         return new ProviderCache(this.version, ImmutableMap.copyOf(this.data));
      }

      public String version() {
         return this.version;
      }

      public ConcurrentMap<Path, HashCode> data() {
         return this.data;
      }
   }
}
