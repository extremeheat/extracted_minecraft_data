package net.minecraft.server.packs;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;
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

   public static record BatchConfig(
      HashFunction hashFunction, int maxSize, Map<String, String> headers, Proxy proxy, HttpUtil.DownloadProgressListener listener
   ) {

      public BatchConfig(HashFunction hashFunction, int maxSize, Map<String, String> headers, Proxy proxy, HttpUtil.DownloadProgressListener listener) {
         super();
         this.hashFunction = hashFunction;
         this.maxSize = maxSize;
         this.headers = headers;
         this.proxy = proxy;
         this.listener = listener;
      }
   }

   public static record BatchResult(Map<UUID, Path> downloaded, Set<UUID> failed) {

      public BatchResult() {
         this(new HashMap<>(), new HashSet<>());
      }

      public BatchResult(Map<UUID, Path> downloaded, Set<UUID> failed) {
         super();
         this.downloaded = downloaded;
         this.failed = failed;
      }
   }

   public static record DownloadRequest(URL url, @Nullable HashCode hash) {

      public DownloadRequest(URL url, @Nullable HashCode hash) {
         super();
         this.url = url;
         this.hash = hash;
      }
   }

   static record FileInfoEntry(String name, long size) {
      public static final Codec<DownloadQueue.FileInfoEntry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.STRING.fieldOf("name").forGetter(DownloadQueue.FileInfoEntry::name),
                  Codec.LONG.fieldOf("size").forGetter(DownloadQueue.FileInfoEntry::size)
               )
               .apply(var0, DownloadQueue.FileInfoEntry::new)
      );

      FileInfoEntry(String name, long size) {
         super();
         this.name = name;
         this.size = size;
      }
   }

   static record LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, DownloadQueue.FileInfoEntry> errorOrFileInfo) {
      public static final Codec<DownloadQueue.LogEntry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(DownloadQueue.LogEntry::id),
                  Codec.STRING.fieldOf("url").forGetter(DownloadQueue.LogEntry::url),
                  ExtraCodecs.INSTANT_ISO8601.fieldOf("time").forGetter(DownloadQueue.LogEntry::time),
                  Codec.STRING.optionalFieldOf("hash").forGetter(DownloadQueue.LogEntry::hash),
                  Codec.mapEither(Codec.STRING.fieldOf("error"), DownloadQueue.FileInfoEntry.CODEC.fieldOf("file"))
                     .forGetter(DownloadQueue.LogEntry::errorOrFileInfo)
               )
               .apply(var0, DownloadQueue.LogEntry::new)
      );

      LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, DownloadQueue.FileInfoEntry> errorOrFileInfo) {
         super();
         this.id = id;
         this.url = url;
         this.time = time;
         this.hash = hash;
         this.errorOrFileInfo = errorOrFileInfo;
      }
   }
}
