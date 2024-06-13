package net.minecraft.server.packs;

import com.google.common.hash.HashCode;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.eventlog.JsonEventLog;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class DownloadQueue implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_KEPT_PACKS = 20;
   private final Path cacheDir;
   private final JsonEventLog<DownloadQueue.LogEntry> eventLog;
   private final ProcessorMailbox<Runnable> tasks = ProcessorMailbox.create(Util.nonCriticalIoPool(), "download-queue");

   public DownloadQueue(Path var1) throws IOException {
      super();
      this.cacheDir = var1;
      FileUtil.createDirectoriesSafe(var1);
      this.eventLog = JsonEventLog.open(DownloadQueue.LogEntry.CODEC, var1.resolve("log.json"));
      DownloadCacheCleaner.vacuumCacheDir(var1, 20);
   }

   private DownloadQueue.BatchResult runDownload(DownloadQueue.BatchConfig var1, Map<UUID, DownloadQueue.DownloadRequest> var2) {
      DownloadQueue.BatchResult var3 = new DownloadQueue.BatchResult();
      var2.forEach(
         (var3x, var4) -> {
            Path var5 = this.cacheDir.resolve(var3x.toString());
            Path var6 = null;

            try {
               var6 = HttpUtil.downloadFile(var5, var4.url, var1.headers, var1.hashFunction, var4.hash, var1.maxSize, var1.proxy, var1.listener);
               var3.downloaded.put(var3x, var6);
            } catch (Exception var9) {
               LOGGER.error("Failed to download {}", var4.url, var9);
               var3.failed.add(var3x);
            }

            try {
               this.eventLog
                  .write(
                     new DownloadQueue.LogEntry(
                        var3x,
                        var4.url.toString(),
                        Instant.now(),
                        Optional.ofNullable(var4.hash).map(HashCode::toString),
                        var6 != null ? this.getFileInfo(var6) : Either.left("download_failed")
                     )
                  );
            } catch (Exception var8) {
               LOGGER.error("Failed to log download of {}", var4.url, var8);
            }
         }
      );
      return var3;
   }

   private Either<String, DownloadQueue.FileInfoEntry> getFileInfo(Path var1) {
      try {
         long var2 = Files.size(var1);
         Path var4 = this.cacheDir.relativize(var1);
         return Either.right(new DownloadQueue.FileInfoEntry(var4.toString(), var2));
      } catch (IOException var5) {
         LOGGER.error("Failed to get file size of {}", var1, var5);
         return Either.left("no_access");
      }
   }

   public CompletableFuture<DownloadQueue.BatchResult> downloadBatch(DownloadQueue.BatchConfig var1, Map<UUID, DownloadQueue.DownloadRequest> var2) {
      return CompletableFuture.supplyAsync(() -> this.runDownload(var1, var2), this.tasks::tell);
   }

   @Override
   public void close() throws IOException {
      this.tasks.close();
      this.eventLog.close();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
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
// java.lang.NullPointerException
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
// java.lang.NullPointerException
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
// java.lang.NullPointerException
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
// java.lang.NullPointerException
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
