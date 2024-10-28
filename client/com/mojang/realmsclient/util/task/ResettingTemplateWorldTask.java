package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.network.chat.Component;

public class ResettingTemplateWorldTask extends ResettingWorldTask {
   private final WorldTemplate template;

   public ResettingTemplateWorldTask(WorldTemplate var1, long var2, Component var4, Runnable var5) {
      super(var2, var4, var5);
      this.template = var1;
   }

   protected void sendResetRequest(RealmsClient var1, long var2) throws RealmsServiceException {
      var1.resetWorldWithTemplate(var2, this.template.id);
   }
}
