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
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
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
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.HttpUtil;
import org.slf4j.Logger;

public class DownloadedPackSource implements AutoCloseable {
   private static final Component SERVER_NAME = Component.translatable("resourcePack.server.name");
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   static final Logger LOGGER = LogUtils.getLogger();
   private static final RepositorySource EMPTY_SOURCE = var0 -> {
   };
   private static final PackSelectionConfig DOWNLOADED_PACK_SELECTION = new PackSelectionConfig(true, Pack.Position.TOP, true);
   private static final PackLoadFeedback LOG_ONLY_FEEDBACK = new PackLoadFeedback() {
      @Override
      public void reportUpdate(UUID var1, PackLoadFeedback.Update var2) {
         DownloadedPackSource.LOGGER.debug("Downloaded pack {} changed state to {}", var1, var2);
      }

      @Override
      public void reportFinalResult(UUID var1, PackLoadFeedback.FinalResult var2) {
         DownloadedPackSource.LOGGER.debug("Downloaded pack {} finished with state {}", var1, var2);
      }
   };
   final Minecraft minecraft;
   private RepositorySource packSource = EMPTY_SOURCE;
   @Nullable
   private PackReloadConfig.Callbacks pendingReload;
   final ServerPackManager manager;
   private final DownloadQueue downloadQueue;
   private PackSource packType = PackSource.SERVER;
   PackLoadFeedback packFeedback = LOG_ONLY_FEEDBACK;
   private int packIdSerialNumber;

   public DownloadedPackSource(Minecraft var1, Path var2, GameConfig.UserData var3) {
      super();
      this.minecraft = var1;

      try {
         this.downloadQueue = new DownloadQueue(var2);
      } catch (IOException var5) {
         throw new UncheckedIOException("Failed to open download queue in directory " + var2, var5);
      }

      Executor var4 = var1::tell;
      this.manager = new ServerPackManager(this.createDownloader(this.downloadQueue, var4, var3.user, var3.proxy), new PackLoadFeedback() {
         @Override
         public void reportUpdate(UUID var1, PackLoadFeedback.Update var2) {
            DownloadedPackSource.this.packFeedback.reportUpdate(var1, var2);
         }

         @Override
         public void reportFinalResult(UUID var1, PackLoadFeedback.FinalResult var2) {
            DownloadedPackSource.this.packFeedback.reportFinalResult(var1, var2);
         }
      }, this.createReloadConfig(), this.createUpdateScheduler(var4), ServerPackManager.PackPromptStatus.PENDING);
   }

   HttpUtil.DownloadProgressListener createDownloadNotifier(final int var1) {
      return new HttpUtil.DownloadProgressListener() {
         private final SystemToast.SystemToastId toastId = new SystemToast.SystemToastId();
         private Component title = Component.empty();
         @Nullable
         private Component message = null;
         private int count;
         private int failCount;
         private OptionalLong totalBytes = OptionalLong.empty();

         private void updateToast() {
            SystemToast.addOrUpdate(DownloadedPackSource.this.minecraft.getToasts(), this.toastId, this.title, this.message);
         }

         private void updateProgress(long var1x) {
            if (this.totalBytes.isPresent()) {
               this.message = Component.translatable("download.pack.progress.percent", var1x * 100L / this.totalBytes.getAsLong());
            } else {
               this.message = Component.translatable("download.pack.progress.bytes", Unit.humanReadable(var1x));
            }

            this.updateToast();
         }

         @Override
         public void requestStart() {
            this.count++;
            this.title = Component.translatable("download.pack.title", this.count, var1);
            this.updateToast();
            DownloadedPackSource.LOGGER.debug("Starting pack {}/{} download", this.count, var1);
         }

         @Override
         public void downloadStart(OptionalLong var1x) {
            DownloadedPackSource.LOGGER.debug("File size = {} bytes", var1x);
            this.totalBytes = var1x;
            this.updateProgress(0L);
         }

         @Override
         public void downloadedBytes(long var1x) {
            DownloadedPackSource.LOGGER.debug("Progress for pack {}: {} bytes", this.count, var1x);
            this.updateProgress(var1x);
         }

         @Override
         public void requestFinished(boolean var1x) {
            if (!var1x) {
               DownloadedPackSource.LOGGER.info("Pack {} failed to download", this.count);
               this.failCount++;
            } else {
               DownloadedPackSource.LOGGER.debug("Download ended for pack {}", this.count);
            }

            if (this.count == var1) {
               if (this.failCount > 0) {
                  this.title = Component.translatable("download.pack.failed", this.failCount, var1);
                  this.message = null;
                  this.updateToast();
               } else {
                  SystemToast.forceHide(DownloadedPackSource.this.minecraft.getToasts(), this.toastId);
               }
            }
         }
      };
   }

   private PackDownloader createDownloader(final DownloadQueue var1, final Executor var2, final User var3, final Proxy var4) {
      return new PackDownloader() {
         private static final int MAX_PACK_SIZE_BYTES = 262144000;
         private static final HashFunction CACHE_HASHING_FUNCTION = Hashing.sha1();

         private Map<String, String> createDownloadHeaders() {
            WorldVersion var1x = SharedConstants.getCurrentVersion();
            return Map.of(
               "X-Minecraft-Username",
               var3.getName(),
               "X-Minecraft-UUID",
               UndashedUuid.toString(var3.getProfileId()),
               "X-Minecraft-Version",
               var1x.getName(),
               "X-Minecraft-Version-ID",
               var1x.getId(),
               "X-Minecraft-Pack-Format",
               String.valueOf(var1x.getPackVersion(PackType.CLIENT_RESOURCES)),
               "User-Agent",
               "Minecraft Java/" + var1x.getName()
            );
         }

         @Override
         public void download(Map<UUID, DownloadQueue.DownloadRequest> var1x, Consumer<DownloadQueue.BatchResult> var2x) {
            var1.downloadBatch(
                  new DownloadQueue.BatchConfig(
                     CACHE_HASHING_FUNCTION, 262144000, this.createDownloadHeaders(), var4, DownloadedPackSource.this.createDownloadNotifier(var1x.size())
                  ),
                  var1x
               )
               .thenAcceptAsync(var2x, var2);
         }
      };
   }

   private Runnable createUpdateScheduler(final Executor var1) {
      return new Runnable() {
         private boolean scheduledInMainExecutor;
         private boolean hasUpdates;

         @Override
         public void run() {
            this.hasUpdates = true;
            if (!this.scheduledInMainExecutor) {
               this.scheduledInMainExecutor = true;
               var1.execute(this::runAllUpdates);
            }
         }

         private void runAllUpdates() {
            while (this.hasUpdates) {
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

   @Nullable
   private List<Pack> loadRequestedPacks(List<PackReloadConfig.IdAndPath> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for (PackReloadConfig.IdAndPath var4 : Lists.reverse(var1)) {
         String var5 = String.format(Locale.ROOT, "server/%08X/%s", this.packIdSerialNumber++, var4.id());
         Path var6 = var4.path();
         PackLocationInfo var7 = new PackLocationInfo(var5, SERVER_NAME, this.packType, Optional.empty());
         FilePackResources.FileResourcesSupplier var8 = new FilePackResources.FileResourcesSupplier(var6);
         int var9 = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
         Pack.Metadata var10 = Pack.readPackMetadata(var7, var8, var9);
         if (var10 == null) {
            LOGGER.warn("Invalid pack metadata in {}, ignoring all", var6);
            return null;
         }

         var2.add(new Pack(var7, var8, var10, DOWNLOADED_PACK_SELECTION));
      }

      return var2;
   }

   public RepositorySource createRepositorySource() {
      return var1 -> this.packSource.loadPacks(var1);
   }

   private static RepositorySource configureSource(List<Pack> var0) {
      return var0.isEmpty() ? EMPTY_SOURCE : var0::forEach;
   }

   private void startReload(PackReloadConfig.Callbacks var1) {
      this.pendingReload = var1;
      List var2 = var1.packsToLoad();
      List var3 = this.loadRequestedPacks(var2);
      if (var3 == null) {
         var1.onFailure(false);
         List var4 = var1.packsToLoad();
         var3 = this.loadRequestedPacks(var4);
         if (var3 == null) {
            LOGGER.warn("Double failure in loading server packs");
            var3 = List.of();
         }
      }

      this.packSource = configureSource(var3);
      this.minecraft.reloadResourcePacks();
   }

   public void onRecovery() {
      if (this.pendingReload != null) {
         this.pendingReload.onFailure(false);
         List var1 = this.loadRequestedPacks(this.pendingReload.packsToLoad());
         if (var1 == null) {
            LOGGER.warn("Double failure in loading server packs");
            var1 = List.of();
         }

         this.packSource = configureSource(var1);
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

   @Nullable
   private static HashCode tryParseSha1Hash(@Nullable String var0) {
      return var0 != null && SHA1.matcher(var0).matches() ? HashCode.fromString(var0.toLowerCase(Locale.ROOT)) : null;
   }

   public void pushPack(UUID var1, URL var2, @Nullable String var3) {
      HashCode var4 = tryParseSha1Hash(var3);
      this.manager.pushPack(var1, var2, var4);
   }

   public void pushLocalPack(UUID var1, Path var2) {
      this.manager.pushLocalPack(var1, var2);
   }

   public void popPack(UUID var1) {
      this.manager.popPack(var1);
   }

   public void popAll() {
      this.manager.popAll();
   }

   private static PackLoadFeedback createPackResponseSender(final Connection var0) {
      return new PackLoadFeedback() {
         @Override
         public void reportUpdate(UUID var1, PackLoadFeedback.Update var2) {
            DownloadedPackSource.LOGGER.debug("Pack {} changed status to {}", var1, var2);

            ServerboundResourcePackPacket.Action var3 = switch (var2) {
               case ACCEPTED -> ServerboundResourcePackPacket.Action.ACCEPTED;
               case DOWNLOADED -> ServerboundResourcePackPacket.Action.DOWNLOADED;
               default -> throw new MatchException(null, null);
            };
            var0.send(new ServerboundResourcePackPacket(var1, var3));
         }

         @Override
         public void reportFinalResult(UUID var1, PackLoadFeedback.FinalResult var2) {
            DownloadedPackSource.LOGGER.debug("Pack {} changed status to {}", var1, var2);

            ServerboundResourcePackPacket.Action var3 = switch (var2) {
               case APPLIED -> ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED;
               case DOWNLOAD_FAILED -> ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD;
               case DECLINED -> ServerboundResourcePackPacket.Action.DECLINED;
               case DISCARDED -> ServerboundResourcePackPacket.Action.DISCARDED;
               case ACTIVATION_FAILED -> ServerboundResourcePackPacket.Action.FAILED_RELOAD;
               default -> throw new MatchException(null, null);
            };
            var0.send(new ServerboundResourcePackPacket(var1, var3));
         }
      };
   }

   public void configureForServerControl(Connection var1, ServerPackManager.PackPromptStatus var2) {
      this.packType = PackSource.SERVER;
      this.packFeedback = createPackResponseSender(var1);
      switch (var2) {
         case ALLOWED:
            this.manager.allowServerPacks();
            break;
         case DECLINED:
            this.manager.rejectServerPacks();
            break;
         case PENDING:
            this.manager.resetPromptStatus();
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

   public CompletableFuture<Void> waitForPackFeedback(final UUID var1) {
      final CompletableFuture var2 = new CompletableFuture();
      final PackLoadFeedback var3 = this.packFeedback;
      this.packFeedback = new PackLoadFeedback() {
         @Override
         public void reportUpdate(UUID var1x, PackLoadFeedback.Update var2x) {
            var3.reportUpdate(var1x, var2x);
         }

         @Override
         public void reportFinalResult(UUID var1x, PackLoadFeedback.FinalResult var2x) {
            if (var1.equals(var1x)) {
               DownloadedPackSource.this.packFeedback = var3;
               if (var2x == PackLoadFeedback.FinalResult.APPLIED) {
                  var2.complete(null);
               } else {
                  var2.completeExceptionally(new IllegalStateException("Failed to apply pack " + var1x + ", reason: " + var2x));
               }
            }

            var3.reportFinalResult(var1x, var2x);
         }
      };
      return var2;
   }

   public void cleanupAfterDisconnect() {
      this.manager.popAll();
      this.packFeedback = LOG_ONLY_FEEDBACK;
      this.manager.resetPromptStatus();
   }

   @Override
   public void close() throws IOException {
      this.downloadQueue.close();
   }
}
