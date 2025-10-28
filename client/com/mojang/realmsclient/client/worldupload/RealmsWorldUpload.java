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
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.User;
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
   private volatile boolean cancelled;
   private volatile @Nullable CompletableFuture<?> uploadTask;

   public RealmsWorldUpload(Path var1, RealmsSlot var2, User var3, long var4, RealmsWorldUploadStatusTracker var6) {
      super();
      this.worldFolder = var1;
      this.realmsSlot = var2;
      this.user = var3;
      this.realmId = var4;
      this.statusCallback = var6;
   }

   public CompletableFuture<?> packAndUpload() {
      return CompletableFuture.runAsync(() -> {
         File var1 = null;

         try {
            UploadInfo var2 = this.requestUploadInfoWithRetries();
            var1 = RealmsUploadWorldPacker.pack(this.worldFolder, () -> this.cancelled);
            this.statusCallback.setUploading();
            FileUpload var24 = new FileUpload(var1, this.realmId, this.realmsSlot.slotId, var2, this.user, SharedConstants.getCurrentVersion().name(), this.realmsSlot.options.version, this.statusCallback.getUploadStatus());

            label163: {
               try {
                  CompletableFuture var4 = var24.startUpload();
                  this.uploadTask = var4;
                  if (this.cancelled) {
                     var4.cancel(true);
                     break label163;
                  }

                  UploadResult var5;
                  try {
                     var5 = (UploadResult)var4.join();
                  } catch (CompletionException var17) {
                     throw var17.getCause();
                  }

                  String var6 = var5.getSimplifiedErrorMessage();
                  if (var6 != null) {
                     throw new RealmsUploadFailedException(var6);
                  }

                  UploadTokenCache.invalidate(this.realmId);
                  this.client.updateSlot(this.realmId, this.realmsSlot.slotId, this.realmsSlot.options, this.realmsSlot.settings);
               } catch (Throwable var18) {
                  try {
                     var24.close();
                  } catch (Throwable var16) {
                     var18.addSuppressed(var16);
                  }

                  throw var18;
               }

               var24.close();
               return;
            }

            var24.close();
         } catch (RealmsServiceException var19) {
            throw new RealmsUploadFailedException(var19.realmsError.errorMessage());
         } catch (CancellationException | InterruptedException var20) {
            throw new RealmsUploadCanceledException();
         } catch (RealmsUploadException var21) {
            throw var21;
         } catch (Throwable var22) {
            if (var22 instanceof Error var3) {
               throw var3;
            }

            throw new RealmsUploadFailedException(var22.getMessage());
         } finally {
            if (var1 != null) {
               LOGGER.debug("Deleting file {}", var1.getAbsolutePath());
               var1.delete();
            }

         }

      }, Util.backgroundExecutor());
   }

   public void cancel() {
      this.cancelled = true;
      CompletableFuture var1 = this.uploadTask;
      if (var1 != null) {
         var1.cancel(true);
      }

   }

   private UploadInfo requestUploadInfoWithRetries() throws RealmsServiceException, InterruptedException {
      for(int var1 = 0; var1 < 20; ++var1) {
         try {
            UploadInfo var2 = this.client.requestUploadInfo(this.realmId);
            if (this.cancelled) {
               throw new RealmsUploadCanceledException();
            }

            if (var2 != null) {
               if (!var2.worldClosed()) {
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
