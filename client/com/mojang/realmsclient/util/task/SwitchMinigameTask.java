package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class SwitchMinigameTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.minigame.world.starting.screen.title");
   private final long realmId;
   private final WorldTemplate worldTemplate;
   private final RealmsConfigureWorldScreen lastScreen;

   public SwitchMinigameTask(long var1, WorldTemplate var3, RealmsConfigureWorldScreen var4) {
      super();
      this.realmId = var1;
      this.worldTemplate = var3;
      this.lastScreen = var4;
   }

   public void run() {
      RealmsClient var1 = RealmsClient.create();

      for(int var2 = 0; var2 < 25; ++var2) {
         try {
            if (this.aborted()) {
               return;
            }

            if (var1.putIntoMinigameMode(this.realmId, this.worldTemplate.id)) {
               setScreen(this.lastScreen);
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

            LOGGER.error("Couldn't start mini game!");
            this.error(var5);
         }
      }

   }

   public Component getTitle() {
      return TITLE;
   }
}
