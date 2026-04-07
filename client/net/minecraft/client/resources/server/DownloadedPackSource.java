package net.minecraft.client.resources.server;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.util.UndashedUuid;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.main.GameConfig;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.server.packs.DownloadQueue;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.HttpUtil;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DownloadedPackSource implements AutoCloseable {
   private static final Component SERVER_NAME = Component.translatable("resourcePack.server.name");
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RepositorySource EMPTY_SOURCE = (result) -> {
   };
   private static final PackSelectionConfig DOWNLOADED_PACK_SELECTION;
   private static final PackLoadFeedback LOG_ONLY_FEEDBACK;
   private final Minecraft minecraft;
   private RepositorySource packSource;
   private PackReloadConfig.@Nullable Callbacks pendingReload;
   private final ServerPackManager manager;
   private final DownloadQueue downloadQueue;
   private PackSource packType;
   private PackLoadFeedback packFeedback;
   private int packIdSerialNumber;

   public DownloadedPackSource(final Minecraft minecraft, final Path packCache, final GameConfig.UserData user) {
      super();
      this.packSource = EMPTY_SOURCE;
      this.packType = PackSource.SERVER;
      this.packFeedback = LOG_ONLY_FEEDBACK;
      this.minecraft = minecraft;

      try {
         this.downloadQueue = new DownloadQueue(packCache);
      } catch (IOException e) {
         throw new UncheckedIOException("Failed to open download queue in directory " + String.valueOf(packCache), e);
      }

      Objects.requireNonNull(minecraft);
      Executor executor = minecraft::schedule;
      this.manager = new ServerPackManager(this.createDownloader(this.downloadQueue, executor, user.user, user.proxy), new PackLoadFeedback() {
         {
            Objects.requireNonNull(DownloadedPackSource.this);
         }

         public void reportUpdate(final UUID id, final PackLoadFeedback.Update result) {
            DownloadedPackSource.this.packFeedback.reportUpdate(id, result);
         }

         public void reportFinalResult(final UUID id, final PackLoadFeedback.FinalResult result) {
            DownloadedPackSource.this.packFeedback.reportFinalResult(id, result);
         }
      }, this.createReloadConfig(), this.createUpdateScheduler(executor), ServerPackManager.PackPromptStatus.PENDING);
   }

   private HttpUtil.DownloadProgressListener createDownloadNotifier(final int totalCount) {
      return new HttpUtil.DownloadProgressListener() {
         private final SystemToast.SystemToastId toastId;
         private Component title;
         private @Nullable Component message;
         private int count;
         private int failCount;
         private OptionalLong totalBytes;

         {
            Objects.requireNonNull(DownloadedPackSource.this);
            this.toastId = new SystemToast.SystemToastId();
            this.title = Component.empty();
            this.message = null;
            this.totalBytes = OptionalLong.empty();
         }

         private void updateToast() {
            DownloadedPackSource.this.minecraft.execute(() -> SystemToast.addOrUpdate(DownloadedPackSource.this.minecraft.gui.toastManager(), this.toastId, this.title, this.message));
         }

         private void updateProgress(final long bytesSoFar) {
            if (this.totalBytes.isPresent()) {
               this.message = Component.translatable("download.pack.progress.percent", bytesSoFar * 100L / this.totalBytes.getAsLong());
            } else {
               this.message = Component.translatable("download.pack.progress.bytes", Unit.humanReadable(bytesSoFar));
            }

            this.updateToast();
         }

         public void requestStart() {
            ++this.count;
            this.title = Component.translatable("download.pack.title", this.count, totalCount);
            this.updateToast();
            DownloadedPackSource.LOGGER.debug("Starting pack {}/{} download", this.count, totalCount);
         }

         public void downloadStart(final OptionalLong sizeBytes) {
            DownloadedPackSource.LOGGER.debug("File size = {} bytes", sizeBytes);
            this.totalBytes = sizeBytes;
            this.updateProgress(0L);
         }

         public void downloadedBytes(final long bytesSoFar) {
            DownloadedPackSource.LOGGER.debug("Progress for pack {}: {} bytes", this.count, bytesSoFar);
            this.updateProgress(bytesSoFar);
         }

         public void requestFinished(final boolean success) {
            if (!success) {
               DownloadedPackSource.LOGGER.info("Pack {} failed to download", this.count);
               ++this.failCount;
            } else {
               DownloadedPackSource.LOGGER.debug("Download ended for pack {}", this.count);
            }

            if (this.count == totalCount) {
               if (this.failCount > 0) {
                  this.title = Component.translatable("download.pack.failed", this.failCount, totalCount);
                  this.message = null;
                  this.updateToast();
               } else {
                  SystemToast.forceHide(DownloadedPackSource.this.minecraft.gui.toastManager(), this.toastId);
               }
            }

         }
      };
   }

   private PackDownloader createDownloader(final DownloadQueue downloadQueue, final Executor mainThreadExecutor, final User user, final Proxy proxy) {
      return new PackDownloader() {
         private static final int MAX_PACK_SIZE_BYTES = 262144000;
         private static final HashFunction CACHE_HASHING_FUNCTION = Hashing.sha1();

         {
            Objects.requireNonNull(DownloadedPackSource.this);
         }

         private Map<String, String> createDownloadHeaders() {
            WorldVersion version = SharedConstants.getCurrentVersion();
            return Map.of("X-Minecraft-Username", user.getName(), "X-Minecraft-UUID", UndashedUuid.toString(user.getProfileId()), "X-Minecraft-Version", version.name(), "X-Minecraft-Version-ID", version.id(), "X-Minecraft-Pack-Format", String.valueOf(version.packVersion(PackType.CLIENT_RESOURCES)), "User-Agent", "Minecraft Java/" + version.name());
         }

         public void download(final Map<UUID, DownloadQueue.DownloadRequest> requests, final Consumer<DownloadQueue.BatchResult> output) {
            downloadQueue.downloadBatch(new DownloadQueue.BatchConfig(CACHE_HASHING_FUNCTION, 262144000, this.createDownloadHeaders(), proxy, DownloadedPackSource.this.createDownloadNotifier(requests.size())), requests).thenAcceptAsync(output, mainThreadExecutor);
         }
      };
   }

   private Runnable createUpdateScheduler(final Executor mainThreadExecutor) {
      return new Runnable() {
         private boolean scheduledInMainExecutor;
         private boolean hasUpdates;

         {
            Objects.requireNonNull(DownloadedPackSource.this);
         }

         public void run() {
            this.hasUpdates = true;
            if (!this.scheduledInMainExecutor) {
               this.scheduledInMainExecutor = true;
               mainThreadExecutor.execute(this::runAllUpdates);
            }

         }

         private void runAllUpdates() {
            while(this.hasUpdates) {
               this.hasUpdates = false;
               DownloadedPackSource.this.manager.tick();
            }

            this.scheduledInMainExecutor = false;
         }
      };
   }

   private PackReloadConfig createReloadConfig() {
      return this::startReload;
   }

   private @Nullable List<Pack> loadRequestedPacks(final List<PackReloadConfig.IdAndPath> packsToLoad) {
      List<Pack> packs = new ArrayList(packsToLoad.size());

      for(PackReloadConfig.IdAndPath idAndPath : Lists.reverse(packsToLoad)) {
         String name = String.format(Locale.ROOT, "server/%08X/%s", this.packIdSerialNumber++, idAndPath.id());
         Path path = idAndPath.path();
         PackLocationInfo packLocationInfo = new PackLocationInfo(name, SERVER_NAME, this.packType, Optional.empty());
         Pack.ResourcesSupplier resources = new FilePackResources.FileResourcesSupplier(path);
         PackFormat currentPackVersion = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES);
         Pack.Metadata metadata = Pack.readPackMetadata(packLocationInfo, resources, currentPackVersion, PackType.CLIENT_RESOURCES);
         if (metadata == null) {
            LOGGER.warn("Invalid pack metadata in {}, ignoring all", path);
            return null;
         }

         packs.add(new Pack(packLocationInfo, resources, metadata, DOWNLOADED_PACK_SELECTION));
      }

      return packs;
   }

   public RepositorySource createRepositorySource() {
      return (output) -> this.packSource.loadPacks(output);
   }

   private static RepositorySource configureSource(final List<Pack> packs) {
      if (packs.isEmpty()) {
         return EMPTY_SOURCE;
      } else {
         Objects.requireNonNull(packs);
         return packs::forEach;
      }
   }

   private void startReload(final PackReloadConfig.Callbacks callbacks) {
      this.pendingReload = callbacks;
      List<PackReloadConfig.IdAndPath> normalPacks = callbacks.packsToLoad();
      List<Pack> packs = this.loadRequestedPacks(normalPacks);
      if (packs == null) {
         callbacks.onFailure(false);
         List<PackReloadConfig.IdAndPath> recoveryPacks = callbacks.packsToLoad();
         packs = this.loadRequestedPacks(recoveryPacks);
         if (packs == null) {
            LOGGER.warn("Double failure in loading server packs");
            packs = List.of();
         }
      }

      this.packSource = configureSource(packs);
      this.minecraft.reloadResourcePacks();
   }

   public void onRecovery() {
      if (this.pendingReload != null) {
         this.pendingReload.onFailure(false);
         List<Pack> packs = this.loadRequestedPacks(this.pendingReload.packsToLoad());
         if (packs == null) {
            LOGGER.warn("Double failure in loading server packs");
            packs = List.of();
         }

         this.packSource = configureSource(packs);
      }

   }

   public void onRecoveryFailure() {
      if (this.pendingReload != null) {
         this.pendingReload.onFailure(true);
         this.pendingReload = null;
         this.packSource = EMPTY_SOURCE;
      }

   }

   public void onReloadSuccess() {
      if (this.pendingReload != null) {
         this.pendingReload.onSuccess();
         this.pendingReload = null;
      }

   }

   private static @Nullable HashCode tryParseSha1Hash(final @Nullable String hash) {
      return hash != null && SHA1.matcher(hash).matches() ? HashCode.fromString(hash.toLowerCase(Locale.ROOT)) : null;
   }

   public void pushPack(final UUID id, final URL url, final @Nullable String hash) {
      HashCode parsedHash = tryParseSha1Hash(hash);
      this.manager.pushPack(id, url, parsedHash);
   }

   public void pushLocalPack(final UUID id, final Path path) {
      this.manager.pushLocalPack(id, path);
   }

   public void popPack(final UUID id) {
      this.manager.popPack(id);
   }

   public void popAll() {
      this.manager.popAll();
   }

   private static PackLoadFeedback createPackResponseSender(final Connection connection) {
      return new PackLoadFeedback() {
         public void reportUpdate(final UUID id, final PackLoadFeedback.Update result) {
            DownloadedPackSource.LOGGER.debug("Pack {} changed status to {}", id, result);
            ServerboundResourcePackPacket.Action var10000;
            switch (result) {
               case ACCEPTED -> var10000 = ServerboundResourcePackPacket.Action.ACCEPTED;
               case DOWNLOADED -> var10000 = ServerboundResourcePackPacket.Action.DOWNLOADED;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            ServerboundResourcePackPacket.Action response = var10000;
            connection.send(new ServerboundResourcePackPacket(id, response));
         }

         public void reportFinalResult(final UUID id, final PackLoadFeedback.FinalResult result) {
            DownloadedPackSource.LOGGER.debug("Pack {} changed status to {}", id, result);
            ServerboundResourcePackPacket.Action var10000;
            switch (result) {
               case APPLIED -> var10000 = ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED;
               case DOWNLOAD_FAILED -> var10000 = ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD;
               case DECLINED -> var10000 = ServerboundResourcePackPacket.Action.DECLINED;
               case DISCARDED -> var10000 = ServerboundResourcePackPacket.Action.DISCARDED;
               case ACTIVATION_FAILED -> var10000 = ServerboundResourcePackPacket.Action.FAILED_RELOAD;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            ServerboundResourcePackPacket.Action response = var10000;
            connection.send(new ServerboundResourcePackPacket(id, response));
         }
      };
   }

   public void configureForServerControl(final Connection connection, final ServerPackManager.PackPromptStatus packPromptStatus) {
      this.packType = PackSource.SERVER;
      this.packFeedback = createPackResponseSender(connection);
      switch (packPromptStatus) {
         case ALLOWED -> this.manager.allowServerPacks();
         case DECLINED -> this.manager.rejectServerPacks();
         case PENDING -> this.manager.resetPromptStatus();
      }

   }

   public void configureForLocalWorld() {
      this.packType = PackSource.WORLD;
      this.packFeedback = LOG_ONLY_FEEDBACK;
      this.manager.allowServerPacks();
   }

   public void allowServerPacks() {
      this.manager.allowServerPacks();
   }

   public void rejectServerPacks() {
      this.manager.rejectServerPacks();
   }

   public CompletableFuture<Void> waitForPackFeedback(final UUID packId) {
      final CompletableFuture<Void> result = new CompletableFuture();
      final PackLoadFeedback original = this.packFeedback;
      this.packFeedback = new PackLoadFeedback() {
         {
            Objects.requireNonNull(DownloadedPackSource.this);
         }

         public void reportUpdate(final UUID id, final PackLoadFeedback.Update resultx) {
            original.reportUpdate(id, result);
         }

         public void reportFinalResult(final UUID id, final PackLoadFeedback.FinalResult status) {
            if (packId.equals(id)) {
               DownloadedPackSource.this.packFeedback = original;
               if (status == PackLoadFeedback.FinalResult.APPLIED) {
                  result.complete((Object)null);
               } else {
                  CompletableFuture var10000 = result;
                  String var10003 = String.valueOf(id);
                  var10000.completeExceptionally(new IllegalStateException("Failed to apply pack " + var10003 + ", reason: " + String.valueOf(status)));
               }
            }

            original.reportFinalResult(id, status);
         }
      };
      return result;
   }

   public void cleanupAfterDisconnect() {
      this.manager.popAll();
      this.packFeedback = LOG_ONLY_FEEDBACK;
      this.manager.resetPromptStatus();
   }

   public void close() throws IOException {
      this.downloadQueue.close();
   }

   static {
      DOWNLOADED_PACK_SELECTION = new PackSelectionConfig(true, Pack.Position.TOP, true);
      LOG_ONLY_FEEDBACK = new PackLoadFeedback() {
         public void reportUpdate(final UUID id, final PackLoadFeedback.Update update) {
            DownloadedPackSource.LOGGER.debug("Downloaded pack {} changed state to {}", id, update);
         }

         public void reportFinalResult(final UUID id, final PackLoadFeedback.FinalResult result) {
            DownloadedPackSource.LOGGER.debug("Downloaded pack {} finished with state {}", id, result);
         }
      };
   }
}
