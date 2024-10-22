package com.mojang.realmsclient.client;

import net.minecraft.Util;

public class UploadStatus {
   private volatile long bytesWritten;
   private volatile long totalBytes;
   private long previousTimeSnapshot = Util.getMillis();
   private long previousBytesWritten;
   private long bytesPerSecond;

   public UploadStatus() {
      super();
   }

   public void setTotalBytes(long var1) {
      this.totalBytes = var1;
   }

   public long getTotalBytes() {
      return this.totalBytes;
   }

   public long getBytesWritten() {
      return this.bytesWritten;
   }

   public void onWrite(long var1) {
      this.bytesWritten += var1;
   }

   public boolean uploadStarted() {
      return this.bytesWritten != 0L;
   }

   public boolean uploadCompleted() {
      return this.bytesWritten == this.getTotalBytes();
   }

   public double getPercentage() {
      return Math.min((double)this.getBytesWritten() / (double)this.getTotalBytes(), 1.0);
   }

   public void refreshBytesPerSecond() {
      long var1 = Util.getMillis();
      long var3 = var1 - this.previousTimeSnapshot;
      if (var3 >= 1000L) {
         long var5 = this.bytesWritten;
         this.bytesPerSecond = 1000L * (var5 - this.previousBytesWritten) / var3;
         this.previousBytesWritten = var5;
         this.previousTimeSnapshot = var1;
      }
   }

   public long getBytesPerSecond() {
      return this.bytesPerSecond;
   }
}
