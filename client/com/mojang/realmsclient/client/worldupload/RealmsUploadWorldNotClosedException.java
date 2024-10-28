package com.mojang.realmsclient.client.worldupload;

import net.minecraft.network.chat.Component;

public class RealmsUploadWorldNotClosedException extends RealmsUploadException {
   public RealmsUploadWorldNotClosedException() {
      super();
   }

   public Component getStatusMessage() {
      return Component.translatable("mco.upload.close.failure");
   }
}
