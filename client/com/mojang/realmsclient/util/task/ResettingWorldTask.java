package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ResettingWorldTask extends LongRunningTask {
   private final String seed;
   private final WorldTemplate worldTemplate;
   private final int levelType;
   private final boolean generateStructures;
   private final long serverId;
   private Component title = new TranslatableComponent("mco.reset.world.resetting.screen.title");
   private final Runnable callback;

   public ResettingWorldTask(@Nullable String var1, @Nullable WorldTemplate var2, int var3, boolean var4, long var5, @Nullable Component var7, Runnable var8) {
      super();
      this.seed = var1;
      this.worldTemplate = var2;
      this.levelType = var3;
      this.generateStructures = var4;
      this.serverId = var5;
      if (var7 != null) {
         this.title = var7;
      }

      this.callback = var8;
   }

   public void run() {
      RealmsClient var1 = RealmsClient.create();
      this.setTitle(this.title);
      int var2 = 0;

      while(var2 < 25) {
         try {
            if (this.aborted()) {
               return;
            }

            if (this.worldTemplate != null) {
               var1.resetWorldWithTemplate(this.serverId, this.worldTemplate.id);
            } else {
               var1.resetWorldWithSeed(this.serverId, this.seed, this.levelType, this.generateStructures);
            }

            if (this.aborted()) {
               return;
            }

            this.callback.run();
            return;
         } catch (RetryCallException var4) {
            if (this.aborted()) {
               return;
            }

            pause(var4.delaySeconds);
            ++var2;
         } catch (Exception var5) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't reset world");
            this.error(var5.toString());
            return;
         }
      }

   }
}
