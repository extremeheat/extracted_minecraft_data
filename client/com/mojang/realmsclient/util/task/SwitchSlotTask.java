package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class SwitchSlotTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long worldId;
   private final int slot;
   private final Runnable callback;

   public SwitchSlotTask(long var1, int var3, Runnable var4) {
      super();
      this.worldId = var1;
      this.slot = var3;
      this.callback = var4;
   }

   public void run() {
      RealmsClient var1 = RealmsClient.create();
      this.setTitle(Component.translatable("mco.minigame.world.slot.screen.title"));

      for(int var2 = 0; var2 < 25; ++var2) {
         try {
            if (this.aborted()) {
               return;
            }

            if (var1.switchSlot(this.worldId, this.slot)) {
               this.callback.run();
               break;
            }
         } catch (RetryCallException var4) {
            if (this.aborted()) {
               return;
            }

            pause((long)var4.delaySeconds);
         } catch (Exception var5) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't switch world!");
            this.error(var5.toString());
         }
      }

   }
}
