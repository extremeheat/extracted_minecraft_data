package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class GetServerDetailsTask extends LongRunningTask {
   private final RealmsServer server;
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   private final ReentrantLock connectLock;

   public GetServerDetailsTask(RealmsMainScreen var1, Screen var2, RealmsServer var3, ReentrantLock var4) {
      super();
      this.lastScreen = var2;
      this.mainScreen = var1;
      this.server = var3;
      this.connectLock = var4;
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.connect.connecting"));
      RealmsClient var1 = RealmsClient.create();
      boolean var2 = false;
      boolean var3 = false;
      int var4 = 5;
      RealmsServerAddress var5 = null;
      boolean var6 = false;
      boolean var7 = false;

      for(int var8 = 0; var8 < 40 && !this.aborted(); ++var8) {
         try {
            var5 = var1.join(this.server.id);
            var2 = true;
         } catch (RetryCallException var11) {
            var4 = var11.delaySeconds;
         } catch (RealmsServiceException var12) {
            if (var12.errorCode == 6002) {
               var6 = true;
            } else if (var12.errorCode == 6006) {
               var7 = true;
            } else {
               var3 = true;
               this.error(var12.toString());
               LOGGER.error("Couldn't connect to world", var12);
            }
            break;
         } catch (Exception var13) {
            var3 = true;
            LOGGER.error("Couldn't connect to world", var13);
            this.error(var13.getLocalizedMessage());
            break;
         }

         if (var2) {
            break;
         }

         this.sleep(var4);
      }

      if (var6) {
         setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
      } else if (var7) {
         if (this.server.ownerUUID.equals(Minecraft.getInstance().getUser().getUuid())) {
            setScreen(new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME));
         } else {
            setScreen(new RealmsGenericErrorScreen(new TranslatableComponent("mco.brokenworld.nonowner.title"), new TranslatableComponent("mco.brokenworld.nonowner.error"), this.lastScreen));
         }
      } else if (!this.aborted() && !var3) {
         if (var2) {
            if (var5.resourcePackUrl != null && var5.resourcePackHash != null) {
               TranslatableComponent var9 = new TranslatableComponent("mco.configure.world.resourcepack.question.line1");
               TranslatableComponent var10 = new TranslatableComponent("mco.configure.world.resourcepack.question.line2");
               setScreen(new RealmsLongConfirmationScreen((var2x) -> {
                  try {
                     if (var2x) {
                        Function var3 = (var1) -> {
                           Minecraft.getInstance().getClientPackSource().clearServerPack();
                           LOGGER.error(var1);
                           setScreen(new RealmsGenericErrorScreen(new TextComponent("Failed to download resource pack!"), this.lastScreen));
                           return null;
                        };

                        try {
                           Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(var5.resourcePackUrl, var5.resourcePackHash).thenRun(() -> {
                              this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, var5)));
                           }).exceptionally(var3);
                        } catch (Exception var8) {
                           var3.apply(var8);
                        }
                     } else {
                        setScreen(this.lastScreen);
                     }
                  } finally {
                     if (this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
                        this.connectLock.unlock();
                     }

                  }

               }, RealmsLongConfirmationScreen.Type.Info, var9, var10, true));
            } else {
               this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, var5)));
            }
         } else {
            this.error(new TranslatableComponent("mco.errorMessage.connectionFailure"));
         }
      }

   }

   private void sleep(int var1) {
      try {
         Thread.sleep((long)(var1 * 1000));
      } catch (InterruptedException var3) {
         LOGGER.warn(var3.getLocalizedMessage());
      }

   }
}
