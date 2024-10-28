package net.minecraft.client.resources.server;

import com.google.common.hash.HashCode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.packs.DownloadQueue;

public class ServerPackManager {
   private final PackDownloader downloader;
   final PackLoadFeedback packLoadFeedback;
   private final PackReloadConfig reloadConfig;
   private final Runnable updateRequest;
   private PackPromptStatus packPromptStatus;
   final List<ServerPackData> packs = new ArrayList();

   public ServerPackManager(PackDownloader var1, PackLoadFeedback var2, PackReloadConfig var3, Runnable var4, PackPromptStatus var5) {
      super();
      this.downloader = var1;
      this.packLoadFeedback = var2;
      this.reloadConfig = var3;
      this.updateRequest = var4;
      this.packPromptStatus = var5;
   }

   void registerForUpdate() {
      this.updateRequest.run();
   }

   private void markExistingPacksAsRemoved(UUID var1) {
      Iterator var2 = this.packs.iterator();

      while(var2.hasNext()) {
         ServerPackData var3 = (ServerPackData)var2.next();
         if (var3.id.equals(var1)) {
            var3.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.SERVER_REPLACED);
         }
      }

   }

   public void pushPack(UUID var1, URL var2, @Nullable HashCode var3) {
      if (this.packPromptStatus == ServerPackManager.PackPromptStatus.DECLINED) {
         this.packLoadFeedback.reportFinalResult(var1, PackLoadFeedback.FinalResult.DECLINED);
      } else {
         this.pushNewPack(var1, new ServerPackData(var1, var2, var3));
      }
   }

   public void pushLocalPack(UUID var1, Path var2) {
      if (this.packPromptStatus == ServerPackManager.PackPromptStatus.DECLINED) {
         this.packLoadFeedback.reportFinalResult(var1, PackLoadFeedback.FinalResult.DECLINED);
      } else {
         URL var3;
         try {
            var3 = var2.toUri().toURL();
         } catch (MalformedURLException var5) {
            throw new IllegalStateException("Can't convert path to URL " + String.valueOf(var2), var5);
         }

         ServerPackData var4 = new ServerPackData(var1, var3, (HashCode)null);
         var4.downloadStatus = ServerPackManager.PackDownloadStatus.DONE;
         var4.path = var2;
         this.pushNewPack(var1, var4);
      }
   }

   private void pushNewPack(UUID var1, ServerPackData var2) {
      this.markExistingPacksAsRemoved(var1);
      this.packs.add(var2);
      if (this.packPromptStatus == ServerPackManager.PackPromptStatus.ALLOWED) {
         this.acceptPack(var2);
      }

      this.registerForUpdate();
   }

   private void acceptPack(ServerPackData var1) {
      this.packLoadFeedback.reportUpdate(var1.id, PackLoadFeedback.Update.ACCEPTED);
      var1.promptAccepted = true;
   }

   @Nullable
   private ServerPackData findPackInfo(UUID var1) {
      Iterator var2 = this.packs.iterator();

      ServerPackData var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (ServerPackData)var2.next();
      } while(var3.isRemoved() || !var3.id.equals(var1));

      return var3;
   }

   public void popPack(UUID var1) {
      ServerPackData var2 = this.findPackInfo(var1);
      if (var2 != null) {
         var2.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.SERVER_REMOVED);
         this.registerForUpdate();
      }

   }

   public void popAll() {
      Iterator var1 = this.packs.iterator();

      while(var1.hasNext()) {
         ServerPackData var2 = (ServerPackData)var1.next();
         var2.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.SERVER_REMOVED);
      }

      this.registerForUpdate();
   }

   public void allowServerPacks() {
      this.packPromptStatus = ServerPackManager.PackPromptStatus.ALLOWED;
      Iterator var1 = this.packs.iterator();

      while(var1.hasNext()) {
         ServerPackData var2 = (ServerPackData)var1.next();
         if (!var2.promptAccepted && !var2.isRemoved()) {
            this.acceptPack(var2);
         }
      }

      this.registerForUpdate();
   }

   public void rejectServerPacks() {
      this.packPromptStatus = ServerPackManager.PackPromptStatus.DECLINED;
      Iterator var1 = this.packs.iterator();

      while(var1.hasNext()) {
         ServerPackData var2 = (ServerPackData)var1.next();
         if (!var2.promptAccepted) {
            var2.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.DECLINED);
         }
      }

      this.registerForUpdate();
   }

   public void resetPromptStatus() {
      this.packPromptStatus = ServerPackManager.PackPromptStatus.PENDING;
   }

   public void tick() {
      boolean var1 = this.updateDownloads();
      if (!var1) {
         this.triggerReloadIfNeeded();
      }

      this.cleanupRemovedPacks();
   }

   private void cleanupRemovedPacks() {
      this.packs.removeIf((var1) -> {
         if (var1.activationStatus != ServerPackManager.ActivationStatus.INACTIVE) {
            return false;
         } else if (var1.removalReason != null) {
            PackLoadFeedback.FinalResult var2 = var1.removalReason.serverResponse;
            if (var2 != null) {
               this.packLoadFeedback.reportFinalResult(var1.id, var2);
            }

            return true;
         } else {
            return false;
         }
      });
   }

   private void onDownload(Collection<ServerPackData> var1, DownloadQueue.BatchResult var2) {
      Iterator var3;
      ServerPackData var4;
      if (!var2.failed().isEmpty()) {
         var3 = this.packs.iterator();

         while(var3.hasNext()) {
            var4 = (ServerPackData)var3.next();
            if (var4.activationStatus != ServerPackManager.ActivationStatus.ACTIVE) {
               if (var2.failed().contains(var4.id)) {
                  var4.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.DOWNLOAD_FAILED);
               } else {
                  var4.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.DISCARDED);
               }
            }
         }
      }

      var3 = var1.iterator();

      while(var3.hasNext()) {
         var4 = (ServerPackData)var3.next();
         Path var5 = (Path)var2.downloaded().get(var4.id);
         if (var5 != null) {
            var4.downloadStatus = ServerPackManager.PackDownloadStatus.DONE;
            var4.path = var5;
            if (!var4.isRemoved()) {
               this.packLoadFeedback.reportUpdate(var4.id, PackLoadFeedback.Update.DOWNLOADED);
            }
         }
      }

      this.registerForUpdate();
   }

   private boolean updateDownloads() {
      ArrayList var1 = new ArrayList();
      boolean var2 = false;
      Iterator var3 = this.packs.iterator();

      while(var3.hasNext()) {
         ServerPackData var4 = (ServerPackData)var3.next();
         if (!var4.isRemoved() && var4.promptAccepted) {
            if (var4.downloadStatus != ServerPackManager.PackDownloadStatus.DONE) {
               var2 = true;
            }

            if (var4.downloadStatus == ServerPackManager.PackDownloadStatus.REQUESTED) {
               var4.downloadStatus = ServerPackManager.PackDownloadStatus.PENDING;
               var1.add(var4);
            }
         }
      }

      if (!var1.isEmpty()) {
         HashMap var6 = new HashMap();
         Iterator var7 = var1.iterator();

         while(var7.hasNext()) {
            ServerPackData var5 = (ServerPackData)var7.next();
            var6.put(var5.id, new DownloadQueue.DownloadRequest(var5.url, var5.hash));
         }

         this.downloader.download(var6, (var2x) -> {
            this.onDownload(var1, var2x);
         });
      }

      return var2;
   }

   private void triggerReloadIfNeeded() {
      boolean var1 = false;
      final ArrayList var2 = new ArrayList();
      final ArrayList var3 = new ArrayList();
      Iterator var4 = this.packs.iterator();

      ServerPackData var5;
      while(var4.hasNext()) {
         var5 = (ServerPackData)var4.next();
         if (var5.activationStatus == ServerPackManager.ActivationStatus.PENDING) {
            return;
         }

         boolean var6 = var5.promptAccepted && var5.downloadStatus == ServerPackManager.PackDownloadStatus.DONE && !var5.isRemoved();
         if (var6 && var5.activationStatus == ServerPackManager.ActivationStatus.INACTIVE) {
            var2.add(var5);
            var1 = true;
         }

         if (var5.activationStatus == ServerPackManager.ActivationStatus.ACTIVE) {
            if (!var6) {
               var1 = true;
               var3.add(var5);
            } else {
               var2.add(var5);
            }
         }
      }

      if (var1) {
         var4 = var2.iterator();

         while(var4.hasNext()) {
            var5 = (ServerPackData)var4.next();
            if (var5.activationStatus != ServerPackManager.ActivationStatus.ACTIVE) {
               var5.activationStatus = ServerPackManager.ActivationStatus.PENDING;
            }
         }

         for(var4 = var3.iterator(); var4.hasNext(); var5.activationStatus = ServerPackManager.ActivationStatus.PENDING) {
            var5 = (ServerPackData)var4.next();
         }

         this.reloadConfig.scheduleReload(new PackReloadConfig.Callbacks() {
            public void onSuccess() {
               Iterator var1 = var2.iterator();

               ServerPackData var2x;
               while(var1.hasNext()) {
                  var2x = (ServerPackData)var1.next();
                  var2x.activationStatus = ServerPackManager.ActivationStatus.ACTIVE;
                  if (var2x.removalReason == null) {
                     ServerPackManager.this.packLoadFeedback.reportFinalResult(var2x.id, PackLoadFeedback.FinalResult.APPLIED);
                  }
               }

               for(var1 = var3.iterator(); var1.hasNext(); var2x.activationStatus = ServerPackManager.ActivationStatus.INACTIVE) {
                  var2x = (ServerPackData)var1.next();
               }

               ServerPackManager.this.registerForUpdate();
            }

            public void onFailure(boolean var1) {
               Iterator var2x;
               ServerPackData var3x;
               if (!var1) {
                  var2.clear();
                  var2x = ServerPackManager.this.packs.iterator();

                  while(var2x.hasNext()) {
                     var3x = (ServerPackData)var2x.next();
                     switch (var3x.activationStatus.ordinal()) {
                        case 0:
                           var3x.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.DISCARDED);
                           break;
                        case 1:
                           var3x.activationStatus = ServerPackManager.ActivationStatus.INACTIVE;
                           var3x.setRemovalReasonIfNotSet(ServerPackManager.RemovalReason.ACTIVATION_FAILED);
                           break;
                        case 2:
                           var2.add(var3x);
                     }
                  }

                  ServerPackManager.this.registerForUpdate();
               } else {
                  var2x = ServerPackManager.this.packs.iterator();

                  while(var2x.hasNext()) {
                     var3x = (ServerPackData)var2x.next();
                     if (var3x.activationStatus == ServerPackManager.ActivationStatus.PENDING) {
                        var3x.activationStatus = ServerPackManager.ActivationStatus.INACTIVE;
                     }
                  }
               }

            }

            public List<PackReloadConfig.IdAndPath> packsToLoad() {
               return var2.stream().map((var0) -> {
                  return new PackReloadConfig.IdAndPath(var0.id, var0.path);
               }).toList();
            }
         });
      }

   }

   public static enum PackPromptStatus {
      PENDING,
      ALLOWED,
      DECLINED;

      private PackPromptStatus() {
      }

      // $FF: synthetic method
      private static PackPromptStatus[] $values() {
         return new PackPromptStatus[]{PENDING, ALLOWED, DECLINED};
      }
   }

   private static class ServerPackData {
      final UUID id;
      final URL url;
      @Nullable
      final HashCode hash;
      @Nullable
      Path path;
      @Nullable
      RemovalReason removalReason;
      PackDownloadStatus downloadStatus;
      ActivationStatus activationStatus;
      boolean promptAccepted;

      ServerPackData(UUID var1, URL var2, @Nullable HashCode var3) {
         super();
         this.downloadStatus = ServerPackManager.PackDownloadStatus.REQUESTED;
         this.activationStatus = ServerPackManager.ActivationStatus.INACTIVE;
         this.id = var1;
         this.url = var2;
         this.hash = var3;
      }

      public void setRemovalReasonIfNotSet(RemovalReason var1) {
         if (this.removalReason == null) {
            this.removalReason = var1;
         }

      }

      public boolean isRemoved() {
         return this.removalReason != null;
      }
   }

   static enum RemovalReason {
      DOWNLOAD_FAILED(PackLoadFeedback.FinalResult.DOWNLOAD_FAILED),
      ACTIVATION_FAILED(PackLoadFeedback.FinalResult.ACTIVATION_FAILED),
      DECLINED(PackLoadFeedback.FinalResult.DECLINED),
      DISCARDED(PackLoadFeedback.FinalResult.DISCARDED),
      SERVER_REMOVED((PackLoadFeedback.FinalResult)null),
      SERVER_REPLACED((PackLoadFeedback.FinalResult)null);

      @Nullable
      final PackLoadFeedback.FinalResult serverResponse;

      private RemovalReason(@Nullable PackLoadFeedback.FinalResult var3) {
         this.serverResponse = var3;
      }

      // $FF: synthetic method
      private static RemovalReason[] $values() {
         return new RemovalReason[]{DOWNLOAD_FAILED, ACTIVATION_FAILED, DECLINED, DISCARDED, SERVER_REMOVED, SERVER_REPLACED};
      }
   }

   static enum PackDownloadStatus {
      REQUESTED,
      PENDING,
      DONE;

      private PackDownloadStatus() {
      }

      // $FF: synthetic method
      private static PackDownloadStatus[] $values() {
         return new PackDownloadStatus[]{REQUESTED, PENDING, DONE};
      }
   }

   private static enum ActivationStatus {
      INACTIVE,
      PENDING,
      ACTIVE;

      private ActivationStatus() {
      }

      // $FF: synthetic method
      private static ActivationStatus[] $values() {
         return new ActivationStatus[]{INACTIVE, PENDING, ACTIVE};
      }
   }
}