package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class DownloadTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long worldId;
   private final int slot;
   private final Screen lastScreen;
   private final String downloadName;

   public DownloadTask(long var1, int var3, String var4, Screen var5) {
      super();
      this.worldId = var1;
      this.slot = var3;
      this.lastScreen = var5;
      this.downloadName = var4;
   }

   @Override
   public void run() {
      this.setTitle(Component.translatable("mco.download.preparing"));
      RealmsClient var1 = RealmsClient.create();
      int var2 = 0;

      while(var2 < 25) {
         try {
            if (this.aborted()) {
               return;
            }

            WorldDownload var3 = var1.requestDownloadInfo(this.worldId, this.slot);
            pause(1L);
            if (this.aborted()) {
               return;
            }

            setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, var3, this.downloadName, var0 -> {
            }));
            return;
         } catch (RetryCallException var4) {
            if (this.aborted()) {
               return;
            }

            pause((long)var4.delaySeconds);
            ++var2;
         } catch (RealmsServiceException var5) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't download world data");
            setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
            return;
         } catch (Exception var6) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't download world data", var6);
            this.error(var6.getLocalizedMessage());
            return;
         }
      }
   }
}
