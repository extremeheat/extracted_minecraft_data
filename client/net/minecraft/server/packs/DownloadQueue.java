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
   private final JsonEventLog<LogEntry> eventLog;
   private final ProcessorMailbox<Runnable> tasks = ProcessorMailbox.create(Util.nonCriticalIoPool(), "download-queue");

   public DownloadQueue(Path var1) throws IOException {
      super();
      this.cacheDir = var1;
      FileUtil.createDirectoriesSafe(var1);
      this.eventLog = JsonEventLog.open(DownloadQueue.LogEntry.CODEC, var1.resolve("log.json"));
      DownloadCacheCleaner.vacuumCacheDir(var1, 20);
   }

   private BatchResult runDownload(BatchConfig var1, Map<UUID, DownloadRequest> var2) {
      BatchResult var3 = new BatchResult();
      var2.forEach((var3x, var4) -> {
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
            this.eventLog.write(new LogEntry(var3x, var4.url.toString(), Instant.now(), Optional.ofNullable(var4.hash).map(HashCode::toString), var6 != null ? this.getFileInfo(var6) : Either.left("download_failed")));
         } catch (Exception var8) {
            LOGGER.error("Failed to log download of {}", var4.url, var8);
         }

      });
      return var3;
   }

   private Either<String, FileInfoEntry> getFileInfo(Path var1) {
      try {
         long var2 = Files.size(var1);
         Path var4 = this.cacheDir.relativize(var1);
         return Either.right(new FileInfoEntry(var4.toString(), var2));
      } catch (IOException var5) {
         LOGGER.error("Failed to get file size of {}", var1, var5);
         return Either.left("no_access");
      }
   }

   public CompletableFuture<BatchResult> downloadBatch(BatchConfig var1, Map<UUID, DownloadRequest> var2) {
      Supplier var10000 = () -> {
         return this.runDownload(var1, var2);
      };
      ProcessorMailbox var10001 = this.tasks;
      Objects.requireNonNull(var10001);
      return CompletableFuture.supplyAsync(var10000, var10001::tell);
   }

   public void close() throws IOException {
      this.tasks.close();
      this.eventLog.close();
   }

   static record LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, FileInfoEntry> errorOrFileInfo) {
      public static final Codec<LogEntry> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(LogEntry::id), Codec.STRING.fieldOf("url").forGetter(LogEntry::url), ExtraCodecs.INSTANT_ISO8601.fieldOf("time").forGetter(LogEntry::time), Codec.STRING.optionalFieldOf("hash").forGetter(LogEntry::hash), Codec.mapEither(Codec.STRING.fieldOf("error"), DownloadQueue.FileInfoEntry.CODEC.fieldOf("file")).forGetter(LogEntry::errorOrFileInfo)).apply(var0, LogEntry::new);
      });

      LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, FileInfoEntry> errorOrFileInfo) {
         super();
         this.id = id;
         this.url = url;
         this.time = time;
         this.hash = hash;
         this.errorOrFileInfo = errorOrFileInfo;
      }

      public UUID id() {
         return this.id;
      }

      public String url() {
         return this.url;
      }

      public Instant time() {
         return this.time;
      }

      public Optional<String> hash() {
         return this.hash;
      }

      public Either<String, FileInfoEntry> errorOrFileInfo() {
         return this.errorOrFileInfo;
      }
   }

   public static record BatchResult(Map<UUID, Path> downloaded, Set<UUID> failed) {
      final Map<UUID, Path> downloaded;
      final Set<UUID> failed;

      public BatchResult() {
         this(new HashMap(), new HashSet());
      }

      public BatchResult(Map<UUID, Path> downloaded, Set<UUID> failed) {
         super();
         this.downloaded = downloaded;
         this.failed = failed;
      }

      public Map<UUID, Path> downloaded() {
         return this.downloaded;
      }

      public Set<UUID> failed() {
         return this.failed;
      }
   }

   public static record BatchConfig(HashFunction hashFunction, int maxSize, Map<String, String> headers, Proxy proxy, HttpUtil.DownloadProgressListener listener) {
      final HashFunction hashFunction;
      final int maxSize;
      final Map<String, String> headers;
      final Proxy proxy;
      final HttpUtil.DownloadProgressListener listener;

      public BatchConfig(HashFunction hashFunction, int maxSize, Map<String, String> headers, Proxy proxy, HttpUtil.DownloadProgressListener listener) {
         super();
         this.hashFunction = hashFunction;
         this.maxSize = maxSize;
         this.headers = headers;
         this.proxy = proxy;
         this.listener = listener;
      }

      public HashFunction hashFunction() {
         return this.hashFunction;
      }

      public int maxSize() {
         return this.maxSize;
      }

      public Map<String, String> headers() {
         return this.headers;
      }

      public Proxy proxy() {
         return this.proxy;
      }

      public HttpUtil.DownloadProgressListener listener() {
         return this.listener;
      }
   }

   private static record FileInfoEntry(String name, long size) {
      public static final Codec<FileInfoEntry> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("name").forGetter(FileInfoEntry::name), Codec.LONG.fieldOf("size").forGetter(FileInfoEntry::size)).apply(var0, FileInfoEntry::new);
      });

      FileInfoEntry(String name, long size) {
         super();
         this.name = name;
         this.size = size;
      }

      public String name() {
         return this.name;
      }

      public long size() {
         return this.size;
      }
   }

   public static record DownloadRequest(URL url, @Nullable HashCode hash) {
      final URL url;
      @Nullable
      final HashCode hash;

      public DownloadRequest(URL url, @Nullable HashCode hash) {
         super();
         this.url = url;
         this.hash = hash;
      }

      public URL url() {
         return this.url;
      }

      @Nullable
      public HashCode hash() {
         return this.hash;
      }
   }
}
