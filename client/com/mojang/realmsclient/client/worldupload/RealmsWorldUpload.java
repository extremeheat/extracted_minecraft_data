package com.mojang.realmsclient.client.worldupload;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.UploadResult;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.SharedConstants;
import net.minecraft.client.User;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RealmsWorldUpload {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int UPLOAD_RETRIES = 20;
   private final RealmsClient client = RealmsClient.getOrCreate();
   private final Path worldFolder;
   private final RealmsSlot realmsSlot;
   private final User user;
   private final long realmId;
   private final RealmsWorldUploadStatusTracker statusCallback;
   private final UploadCancellation cancellation = new UploadCancellation();
   private volatile @Nullable ContentV2WorldUploader contentV2Uploader;

   public RealmsWorldUpload(final Path worldFolder, final RealmsSlot realmsSlot, final User user, final long realmId, final RealmsWorldUploadStatusTracker statusCallback) {
      super();
      this.worldFolder = worldFolder;
      this.realmsSlot = realmsSlot;
      this.user = user;
      this.realmId = realmId;
      this.statusCallback = statusCallback;
   }

   public CompletableFuture<?> packAndUpload() {
      return CompletableFuture.runAsync(() -> {
         File archive = null;

         try {
            this.cancellation.throwIfCancelled();
            boolean useContentV2Upload = this.client.isContentV2UploadEnabled();
            Path var10000 = this.worldFolder;
            UploadCancellation var10001 = this.cancellation;
            Objects.requireNonNull(var10001);
            archive = RealmsUploadWorldPacker.pack(var10000, var10001::isCancelled);
            this.cancellation.throwIfCancelled();
            if (useContentV2Upload) {
               this.uploadContentV2(archive);
            } else {
               this.uploadLegacy(archive, this.requestUploadInfoWithRetries());
            }

            this.client.updateSlot(this.realmId, this.realmsSlot.slotId, this.realmsSlot.options, this.realmsSlot.settings);
         } catch (RealmsServiceException e) {
            throw new RealmsUploadFailedException(e.realmsError.errorMessage());
         } catch (InterruptedException var12) {
            Thread.currentThread().interrupt();
            throw new RealmsUploadCanceledException();
         } catch (CancellationException var13) {
            throw new RealmsUploadCanceledException();
         } catch (RealmsUploadException e) {
            throw e;
         } catch (Throwable e) {
            if (e instanceof Error error) {
               throw error;
            }

            throw new RealmsUploadFailedException(e.getMessage());
         } finally {
            if (archive != null) {
               LOGGER.debug("Deleting file {}", archive.getAbsolutePath());
               if (!archive.delete()) {
                  LOGGER.warn("Could not delete upload archive {}", archive);
               }
            }

         }

      }, Util.nonCriticalIoPool());
   }

   public void cancel() {
      ContentV2WorldUploader uploader = this.contentV2Uploader;
      if (uploader != null) {
         uploader.cancel();
      } else {
         this.cancellation.cancel();
      }

   }

   private void uploadContentV2(final File archive) {
      try {
         ContentV2WorldUploader uploader = new ContentV2WorldUploader(this.client, this.statusCallback, this.cancellation);

         try {
            this.contentV2Uploader = uploader;
            uploader.upload(archive.toPath(), this.realmId, this.realmsSlot.slotId, "tar.gz");
         } catch (Throwable var10) {
            try {
               uploader.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         uploader.close();
      } finally {
         this.contentV2Uploader = null;
      }

   }

   private void uploadLegacy(final File archive, final UploadInfo uploadInfo) throws Throwable {
      this.statusCallback.setUploading();

      try (FileUpload fileUpload = new FileUpload(archive, this.realmId, this.realmsSlot.slotId, uploadInfo, this.user, SharedConstants.getCurrentVersion().name(), this.realmsSlot.options.version, this.statusCallback.getUploadStatus())) {
         UploadResult result;
         try {
            result = (UploadResult)this.cancellation.join(fileUpload.startUpload());
         } catch (CompletionException e) {
            throw e.getCause();
         }

         String errorMessage = result.getSimplifiedErrorMessage();
         if (errorMessage != null) {
            throw new RealmsUploadFailedException(errorMessage);
         }

         UploadTokenCache.invalidate(this.realmId);
      }

   }

   private UploadInfo requestUploadInfoWithRetries() throws RealmsServiceException, InterruptedException {
      for(int i = 0; i < 20; ++i) {
         this.cancellation.throwIfCancelled();

         try {
            UploadInfo uploadInfo = this.client.requestUploadInfo(this.realmId);
            this.cancellation.throwIfCancelled();
            if (uploadInfo != null) {
               if (!uploadInfo.worldClosed()) {
                  throw new RealmsUploadWorldNotClosedException();
               }

               return uploadInfo;
            }
         } catch (RetryCallException e) {
            Thread.sleep((long)e.delaySeconds * 1000L);
         }
      }

      throw new RealmsUploadWorldNotClosedException();
   }
}
