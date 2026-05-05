package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.Unit;
import net.minecraft.network.chat.Component;

public class RealmsUploadTooLargeException extends RealmsUploadException {
   private final long sizeLimit;

   public RealmsUploadTooLargeException(final long sizeLimit) {
      super();
      this.sizeLimit = sizeLimit;
   }

   public Component[] getErrorMessages() {
      return new Component[]{Component.translatable("mco.upload.failed.too_big.title"), Component.translatable("mco.upload.failed.too_big.description", Unit.humanReadable(this.sizeLimit, Unit.getLargest(this.sizeLimit)))};
   }
}
