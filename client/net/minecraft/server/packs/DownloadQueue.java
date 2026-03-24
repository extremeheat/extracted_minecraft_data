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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FileUtil;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.util.eventlog.JsonEventLog;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DownloadQueue implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_KEPT_PACKS = 20;
   private final Path cacheDir;
   private final JsonEventLog<LogEntry> eventLog;
   private final ConsecutiveExecutor tasks = new ConsecutiveExecutor(Util.nonCriticalIoPool(), "download-queue");

   public DownloadQueue(final Path cacheDir) throws IOException {
      super();
      this.cacheDir = cacheDir;
      FileUtil.createDirectoriesSafe(cacheDir);
      this.eventLog = JsonEventLog.<LogEntry>open(DownloadQueue.LogEntry.CODEC, cacheDir.resolve("log.json"));
      DownloadCacheCleaner.vacuumCacheDir(cacheDir, 20);
   }

   private BatchResult runDownload(final BatchConfig config, final Map<UUID, DownloadRequest> requests) {
      BatchResult result = new BatchResult();
      requests.forEach((id, request) -> {
         Path targetDir = this.cacheDir.resolve(id.toString());
         Path downloadedFile = null;

         try {
            downloadedFile = HttpUtil.downloadFile(targetDir, request.url, config.headers, config.hashFunction, request.hash, config.maxSize, config.proxy, config.listener);
            result.downloaded.put(id, downloadedFile);
         } catch (Exception e) {
            LOGGER.error("Failed to download {}", request.url, e);
            result.failed.add(id);
         }

         try {
            this.eventLog.write(new LogEntry(id, request.url.toString(), Instant.now(), Optional.ofNullable(request.hash).map(HashCode::toString), downloadedFile != null ? this.getFileInfo(downloadedFile) : Either.left("download_failed")));
         } catch (Exception e) {
            LOGGER.error("Failed to log download of {}", request.url, e);
         }

      });
      return result;
   }

   private Either<String, FileInfoEntry> getFileInfo(final Path downloadedFile) {
      try {
         long size = Files.size(downloadedFile);
         Path relativePath = this.cacheDir.relativize(downloadedFile);
         return Either.right(new FileInfoEntry(relativePath.toString(), size));
      } catch (IOException e) {
         LOGGER.error("Failed to get file size of {}", downloadedFile, e);
         return Either.left("no_access");
      }
   }

   public CompletableFuture<BatchResult> downloadBatch(final BatchConfig config, final Map<UUID, DownloadRequest> requests) {
      Supplier var10000 = () -> this.runDownload(config, requests);
      ConsecutiveExecutor var10001 = this.tasks;
      Objects.requireNonNull(var10001);
      return CompletableFuture.supplyAsync(var10000, var10001::schedule);
   }

   public void close() throws IOException {
      this.tasks.close();
      this.eventLog.close();
   }

   private static record FileInfoEntry(String name, long size) {
      public static final Codec<FileInfoEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("name").forGetter(FileInfoEntry::name), Codec.LONG.fieldOf("size").forGetter(FileInfoEntry::size)).apply(i, FileInfoEntry::new));

      private FileInfoEntry {
         super();
      }
   }

   private static record LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, FileInfoEntry> errorOrFileInfo) {
      public static final Codec<LogEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(LogEntry::id), Codec.STRING.fieldOf("url").forGetter(LogEntry::url), ExtraCodecs.INSTANT_ISO8601.fieldOf("time").forGetter(LogEntry::time), Codec.STRING.optionalFieldOf("hash").forGetter(LogEntry::hash), Codec.mapEither(Codec.STRING.fieldOf("error"), DownloadQueue.FileInfoEntry.CODEC.fieldOf("file")).forGetter(LogEntry::errorOrFileInfo)).apply(i, LogEntry::new));

      private LogEntry {
         super();
      }
   }

   public static record BatchResult(Map<UUID, Path> downloaded, Set<UUID> failed) {
      public BatchResult() {
         this(new HashMap(), new HashSet());
      }

      public BatchResult {
         super();
      }
   }

   public static record DownloadRequest(URL url, @Nullable HashCode hash) {
      public DownloadRequest {
         super();
      }
   }

   public static record BatchConfig(HashFunction hashFunction, int maxSize, Map<String, String> headers, Proxy proxy, HttpUtil.DownloadProgressListener listener) {
      public BatchConfig {
         super();
      }
   }
}
