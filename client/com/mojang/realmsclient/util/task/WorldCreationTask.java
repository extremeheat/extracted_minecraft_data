package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class WorldCreationTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.create.world.wait");
   private final String name;
   private final String motd;
   private final long worldId;

   public WorldCreationTask(long var1, String var3, String var4) {
      super();
      this.worldId = var1;
      this.name = var3;
      this.motd = var4;
   }

   @Override
   public void run() {
      RealmsClient var1 = RealmsClient.create();

      try {
         var1.initializeWorld(this.worldId, this.name, this.motd);
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn't create world", var3);
         this.error(var3);
      } catch (Exception var4) {
         LOGGER.error("Could not create world", var4);
         this.error(var4);
      }
   }

   @Override
   public Component getTitle() {
      return TITLE;
   }
}
