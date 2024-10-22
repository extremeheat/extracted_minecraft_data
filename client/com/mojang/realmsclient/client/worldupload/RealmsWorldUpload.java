package com.mojang.realmsclient.client.worldupload;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.UploadResult;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.User;
import org.slf4j.Logger;

public class RealmsWorldUpload {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int UPLOAD_RETRIES = 20;
   private final RealmsClient client = RealmsClient.create();
   private final Path worldFolder;
   private final RealmsWorldOptions worldOptions;
   private final User user;
   private final long realmId;
   private final int slotId;
   private final RealmsWorldUploadStatusTracker statusCallback;
   private volatile boolean cancelled;
   @Nullable
   private FileUpload uploadTask;

   public RealmsWorldUpload(Path var1, RealmsWorldOptions var2, User var3, long var4, int var6, RealmsWorldUploadStatusTracker var7) {
      super();
      this.worldFolder = var1;
      this.worldOptions = var2;
      this.user = var3;
      this.realmId = var4;
      this.slotId = var6;
      this.statusCallback = var7;
   }

   public CompletableFuture<?> packAndUpload() {
      return CompletableFuture.runAsync(
         () -> {
            File var1 = null;

            try {
               UploadInfo var2 = this.requestUploadInfoWithRetries();
               var1 = RealmsUploadWorldPacker.pack(this.worldFolder, () -> this.cancelled);
               this.statusCallback.setUploading();
               FileUpload var3 = new FileUpload(
                  var1,
                  this.realmId,
                  this.slotId,
                  var2,
                  this.user,
                  SharedConstants.getCurrentVersion().getName(),
                  this.worldOptions.version,
                  this.statusCallback.getUploadStatus()
               );
               this.uploadTask = var3;
               UploadResult var4 = var3.upload();
               String var5 = var4.getSimplifiedErrorMessage();
               if (var5 != null) {
                  throw new RealmsUploadFailedException(var5);
               }

               UploadTokenCache.invalidate(this.realmId);
               this.client.updateSlot(this.realmId, this.slotId, this.worldOptions);
            } catch (IOException var11) {
               throw new RealmsUploadFailedException(var11.getMessage());
            } catch (RealmsServiceException var12) {
               throw new RealmsUploadFailedException(var12.realmsError.errorMessage());
            } catch (CancellationException | InterruptedException var13) {
               throw new RealmsUploadCanceledException();
            } finally {
               if (var1 != null) {
                  LOGGER.debug("Deleting file {}", var1.getAbsolutePath());
                  var1.delete();
               }
            }
         },
         Util.backgroundExecutor()
      );
   }

   public void cancel() {
      this.cancelled = true;
      if (this.uploadTask != null) {
         this.uploadTask.cancel();
         this.uploadTask = null;
      }
   }

   private UploadInfo requestUploadInfoWithRetries() throws RealmsServiceException, InterruptedException {
      for (int var1 = 0; var1 < 20; var1++) {
         try {
            UploadInfo var2 = this.client.requestUploadInfo(this.realmId);
            if (this.cancelled) {
               throw new RealmsUploadCanceledException();
            }

            if (var2 != null) {
               if (!var2.isWorldClosed()) {
                  throw new RealmsUploadWorldNotClosedException();
               }

               return var2;
            }
         } catch (RetryCallException var3) {
            Thread.sleep((long)var3.delaySeconds * 1000L);
         }
      }

      throw new RealmsUploadWorldNotClosedException();
   }
}
