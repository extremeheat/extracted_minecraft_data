package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class WorldCreationTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String name;
   private final String motd;
   private final long worldId;
   private final Screen lastScreen;

   public WorldCreationTask(long var1, String var3, String var4, Screen var5) {
      super();
      this.worldId = var1;
      this.name = var3;
      this.motd = var4;
      this.lastScreen = var5;
   }

   @Override
   public void run() {
      this.setTitle(Component.translatable("mco.create.world.wait"));
      RealmsClient var1 = RealmsClient.create();

      try {
         var1.initializeWorld(this.worldId, this.name, this.motd);
         setScreen(this.lastScreen);
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn't create world");
         this.error(var3.toString());
      } catch (Exception var4) {
         LOGGER.error("Could not create world");
         this.error(var4.getLocalizedMessage());
      }
   }
}
