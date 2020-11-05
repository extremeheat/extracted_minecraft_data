package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class OpenServerTask extends LongRunningTask {
   private final RealmsServer serverData;
   private final Screen returnScreen;
   private final boolean join;
   private final RealmsMainScreen mainScreen;

   public OpenServerTask(RealmsServer var1, Screen var2, RealmsMainScreen var3, boolean var4) {
      super();
      this.serverData = var1;
      this.returnScreen = var2;
      this.join = var4;
      this.mainScreen = var3;
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.configure.world.opening"));
      RealmsClient var1 = RealmsClient.create();

      for(int var2 = 0; var2 < 25; ++var2) {
         if (this.aborted()) {
            return;
         }

         try {
            boolean var3 = var1.open(this.serverData.id);
            if (var3) {
               if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                  ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
               }

               this.serverData.state = RealmsServer.State.OPEN;
               if (this.join) {
                  this.mainScreen.play(this.serverData, this.returnScreen);
               } else {
                  setScreen(this.returnScreen);
               }
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

            LOGGER.error("Failed to open server", var5);
            this.error("Failed to open the server");
         }
      }

   }
}
