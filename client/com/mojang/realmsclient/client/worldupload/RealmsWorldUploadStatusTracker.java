package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.client.UploadStatus;

public interface RealmsWorldUploadStatusTracker {
   UploadStatus getUploadStatus();

   void setUploading();

   static RealmsWorldUploadStatusTracker noOp() {
      return new RealmsWorldUploadStatusTracker() {
         private final UploadStatus uploadStatus = new UploadStatus();

         public UploadStatus getUploadStatus() {
            return this.uploadStatus;
         }

         public void setUploading() {
         }
      };
   }
}
