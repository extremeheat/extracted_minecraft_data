package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import net.minecraft.network.chat.Component;

public class ResettingGeneratedWorldTask extends ResettingWorldTask {
   private final WorldGenerationInfo generationInfo;

   public ResettingGeneratedWorldTask(WorldGenerationInfo var1, long var2, Component var4, Runnable var5) {
      super(var2, var4, var5);
      this.generationInfo = var1;
   }

   protected void sendResetRequest(RealmsClient var1, long var2) throws RealmsServiceException {
      var1.resetWorldWithSeed(var2, this.generationInfo);
   }
}
