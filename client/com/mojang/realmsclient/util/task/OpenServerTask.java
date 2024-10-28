package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class OpenServerTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.configure.world.opening");
   private final RealmsServer serverData;
   private final Screen returnScreen;
   private final boolean join;
   private final Minecraft minecraft;

   public OpenServerTask(RealmsServer var1, Screen var2, boolean var3, Minecraft var4) {
      super();
      this.serverData = var1;
      this.returnScreen = var2;
      this.join = var3;
      this.minecraft = var4;
   }

   public void run() {
      RealmsClient var1 = RealmsClient.create();

      for(int var2 = 0; var2 < 25; ++var2) {
         if (this.aborted()) {
            return;
         }

         try {
            boolean var3 = var1.open(this.serverData.id);
            if (var3) {
               this.minecraft.execute(() -> {
                  if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                     ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                  }

                  this.serverData.state = RealmsServer.State.OPEN;
                  if (this.join) {
                     RealmsMainScreen.play(this.serverData, this.returnScreen);
                  } else {
                     this.minecraft.setScreen(this.returnScreen);
                  }

               });
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

            LOGGER.error("Failed to open server", var5);
            this.error(var5);
         }
      }

   }

   public Component getTitle() {
      return TITLE;
   }
}
