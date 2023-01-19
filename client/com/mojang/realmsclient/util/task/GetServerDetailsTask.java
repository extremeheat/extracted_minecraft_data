package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
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
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class GetServerDetailsTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   @Override
   public void run() {
      this.setTitle(Component.translatable("mco.connect.connecting"));

      RealmsServerAddress var1;
      try {
         var1 = this.fetchServerAddress();
      } catch (CancellationException var4) {
         LOGGER.info("User aborted connecting to realms");
         return;
      } catch (RealmsServiceException var5) {
         switch(var5.realmsErrorCodeOrDefault(-1)) {
            case 6002:
               setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
               return;
            case 6006:
               boolean var3 = this.server.ownerUUID.equals(Minecraft.getInstance().getUser().getUuid());
               setScreen(
                  (Screen)(var3
                     ? new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME)
                     : new RealmsGenericErrorScreen(
                        Component.translatable("mco.brokenworld.nonowner.title"), Component.translatable("mco.brokenworld.nonowner.error"), this.lastScreen
                     ))
               );
               return;
            default:
               this.error(var5.toString());
               LOGGER.error("Couldn't connect to world", var5);
               return;
         }
      } catch (TimeoutException var6) {
         this.error(Component.translatable("mco.errorMessage.connectionFailure"));
         return;
      } catch (Exception var7) {
         LOGGER.error("Couldn't connect to world", var7);
         this.error(var7.getLocalizedMessage());
         return;
      }

      boolean var2 = var1.resourcePackUrl != null && var1.resourcePackHash != null;
      Object var8 = var2 ? this.resourcePackDownloadConfirmationScreen(var1, this::connectScreen) : this.connectScreen(var1);
      setScreen((Screen)var8);
   }

   private RealmsServerAddress fetchServerAddress() throws RealmsServiceException, TimeoutException, CancellationException {
      RealmsClient var1 = RealmsClient.create();

      for(int var2 = 0; var2 < 40; ++var2) {
         if (this.aborted()) {
            throw new CancellationException();
         }

         try {
            return var1.join(this.server.id);
         } catch (RetryCallException var4) {
            pause((long)var4.delaySeconds);
         }
      }

      throw new TimeoutException();
   }

   public RealmsLongRunningMcoTaskScreen connectScreen(RealmsServerAddress var1) {
      return new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, var1));
   }

   private RealmsLongConfirmationScreen resourcePackDownloadConfirmationScreen(RealmsServerAddress var1, Function<RealmsServerAddress, Screen> var2) {
      BooleanConsumer var3 = var3x -> {
         try {
            if (var3x) {
               this.scheduleResourcePackDownload(var1).thenRun(() -> setScreen((Screen)var2.apply(var1))).exceptionally(var2xx -> {
                  Minecraft.getInstance().getDownloadedPackSource().clearServerPack();
                  LOGGER.error("Failed to download resource pack from {}", var1, var2xx);
                  setScreen(new RealmsGenericErrorScreen(Component.literal("Failed to download resource pack!"), this.lastScreen));
                  return null;
               });
               return;
            }

            setScreen(this.lastScreen);
         } finally {
            if (this.connectLock.isHeldByCurrentThread()) {
               this.connectLock.unlock();
            }
         }
      };
      return new RealmsLongConfirmationScreen(
         var3,
         RealmsLongConfirmationScreen.Type.Info,
         Component.translatable("mco.configure.world.resourcepack.question.line1"),
         Component.translatable("mco.configure.world.resourcepack.question.line2"),
         true
      );
   }

   private CompletableFuture<?> scheduleResourcePackDownload(RealmsServerAddress var1) {
      try {
         return Minecraft.getInstance().getDownloadedPackSource().downloadAndSelectResourcePack(new URL(var1.resourcePackUrl), var1.resourcePackHash, false);
      } catch (Exception var4) {
         CompletableFuture var3 = new CompletableFuture();
         var3.completeExceptionally(var4);
         return var3;
      }
   }
}
