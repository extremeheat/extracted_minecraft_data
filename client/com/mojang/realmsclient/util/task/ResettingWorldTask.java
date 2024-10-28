package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public abstract class ResettingWorldTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long serverId;
   private final Component title;
   private final Runnable callback;

   public ResettingWorldTask(long var1, Component var3, Runnable var4) {
      super();
      this.serverId = var1;
      this.title = var3;
      this.callback = var4;
   }

   protected abstract void sendResetRequest(RealmsClient var1, long var2) throws RealmsServiceException;

   public void run() {
      RealmsClient var1 = RealmsClient.create();
      int var2 = 0;

      while(var2 < 25) {
         try {
            if (this.aborted()) {
               return;
            }

            this.sendResetRequest(var1, this.serverId);
            if (this.aborted()) {
               return;
            }

            this.callback.run();
            return;
         } catch (RetryCallException var4) {
            if (this.aborted()) {
               return;
            }

            pause((long)var4.delaySeconds);
            ++var2;
         } catch (Exception var5) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't reset world");
            this.error(var5);
            return;
         }
      }

   }

   public Component getTitle() {
      return this.title;
   }
}
