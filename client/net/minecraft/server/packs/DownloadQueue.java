package net.minecraft.server.packs;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
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

   public static record BatchConfig(HashFunction a, int b, Map<String, String> c, Proxy d, HttpUtil.DownloadProgressListener e) {
      final HashFunction hashFunction;
      final int maxSize;
      final Map<String, String> headers;
      final Proxy proxy;
      final HttpUtil.DownloadProgressListener listener;

      public BatchConfig(HashFunction var1, int var2, Map<String, String> var3, Proxy var4, HttpUtil.DownloadProgressListener var5) {
         super();
         this.hashFunction = var1;
         this.maxSize = var2;
         this.headers = var3;
         this.proxy = var4;
         this.listener = var5;
      }
   }

   public static record BatchResult(Map<UUID, Path> a, Set<UUID> b) {
      final Map<UUID, Path> downloaded;
      final Set<UUID> failed;

      public BatchResult() {
         this(new HashMap<>(), new HashSet<>());
      }

      public BatchResult(Map<UUID, Path> var1, Set<UUID> var2) {
         super();
         this.downloaded = var1;
         this.failed = var2;
      }
   }

   public static record DownloadRequest(URL a, @Nullable HashCode b) {
      final URL url;
      @Nullable
      final HashCode hash;

      public DownloadRequest(URL var1, @Nullable HashCode var2) {
         super();
         this.url = var1;
         this.hash = var2;
      }
   }

   static record FileInfoEntry(String b, long c) {
      private final String name;
      private final long size;
      public static final Codec<DownloadQueue.FileInfoEntry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.STRING.fieldOf("name").forGetter(DownloadQueue.FileInfoEntry::name),
                  Codec.LONG.fieldOf("size").forGetter(DownloadQueue.FileInfoEntry::size)
               )
               .apply(var0, DownloadQueue.FileInfoEntry::new)
      );

      FileInfoEntry(String var1, long var2) {
         super();
         this.name = var1;
         this.size = (long)var2;
      }
   }

   static record LogEntry(UUID b, String c, Instant d, Optional<String> e, Either<String, DownloadQueue.FileInfoEntry> f) {
      private final UUID id;
      private final String url;
      private final Instant time;
      private final Optional<String> hash;
      private final Either<String, DownloadQueue.FileInfoEntry> errorOrFileInfo;
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

      LogEntry(UUID var1, String var2, Instant var3, Optional<String> var4, Either<String, DownloadQueue.FileInfoEntry> var5) {
         super();
         this.id = var1;
         this.url = var2;
         this.time = var3;
         this.hash = var4;
         this.errorOrFileInfo = var5;
      }
   }
}
