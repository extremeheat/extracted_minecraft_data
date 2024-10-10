package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
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
   final Set<Path> cachePaths = new HashSet<>();
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

      for (String var7 : var2) {
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
      this.writes = this.writes + var1.writes();
   }

   public void purgeStaleAndWrite() throws IOException {
      final HashSet var1 = new HashSet();
      this.caches.forEach((var2x, var3x) -> {
         if (this.cachesToWrite.contains(var2x)) {
            Path var4 = this.getProviderCachePath(var2x);
            var3x.save(this.rootDir, var4, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + var2x);
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
      });
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

      CacheUpdater(final String nullx, final String nullxx, final HashCache.ProviderCache nullxxx) {
         super();
         this.provider = nullx;
         this.oldCache = nullxxx;
         this.newCache = new HashCache.ProviderCacheBuilder(nullxx);
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   @FunctionalInterface
   public interface UpdateFunction {
      CompletableFuture<?> update(CachedOutput var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
