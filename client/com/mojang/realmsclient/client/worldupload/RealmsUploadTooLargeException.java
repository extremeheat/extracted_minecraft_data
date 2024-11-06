package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.Unit;
import net.minecraft.network.chat.Component;

public class RealmsUploadTooLargeException extends RealmsUploadException {
   final long sizeLimit;

   public RealmsUploadTooLargeException(long var1) {
      super();
      this.sizeLimit = var1;
   }

   public Component[] getErrorMessages() {
      return new Component[]{Component.translatable("mco.upload.failed.too_big.title"), Component.translatable("mco.upload.failed.too_big.description", Unit.humanReadable(this.sizeLimit, Unit.getLargest(this.sizeLimit)))};
   }
}