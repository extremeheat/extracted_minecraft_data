package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class CloseServerTask extends LongRunningTask {
   private final RealmsServer serverData;
   private final RealmsConfigureWorldScreen configureScreen;

   public CloseServerTask(RealmsServer var1, RealmsConfigureWorldScreen var2) {
      super();
      this.serverData = var1;
      this.configureScreen = var2;
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.configure.world.closing"));
      RealmsClient var1 = RealmsClient.create();

      for(int var2 = 0; var2 < 25; ++var2) {
         if (this.aborted()) {
            return;
         }

         try {
            boolean var3 = var1.close(this.serverData.id);
            if (var3) {
               this.configureScreen.stateChanged();
               this.serverData.state = RealmsServer.State.CLOSED;
               setScreen(this.configureScreen);
               break;
            }
         } catch (RetryCallException var4) {
            if (this.aborted()) {
               return;
            }

            pause(var4.delaySeconds);
         } catch (Exception var5) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Failed to close server", var5);
            this.error("Failed to close the server");
         }
      }

   }
}
