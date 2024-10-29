package com.mojang.realmsclient.client.worldupload;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public abstract class RealmsUploadException extends RuntimeException {
   public RealmsUploadException() {
      super();
   }

   @Nullable
   public Component getStatusMessage() {
      return null;
   }

   @Nullable
   public Component[] getErrorMessages() {
      return null;
   }
}
