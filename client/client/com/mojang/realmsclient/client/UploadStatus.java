package com.mojang.realmsclient.client;

public class UploadStatus {
   public volatile long bytesWritten;
   public volatile long totalBytes;

   public UploadStatus() {
      super();
   }
}
