package com.mojang.realmsclient.client.worldupload;

import net.minecraft.network.chat.Component;

public class RealmsUploadFailedException extends RealmsUploadException {
   private final Component errorMessage;

   public RealmsUploadFailedException(Component var1) {
      super();
      this.errorMessage = var1;
   }

   public RealmsUploadFailedException(String var1) {
      this(Component.literal(var1));
   }

   @Override
   public Component getStatusMessage() {
      return Component.translatable("mco.upload.failed", this.errorMessage);
   }
}
