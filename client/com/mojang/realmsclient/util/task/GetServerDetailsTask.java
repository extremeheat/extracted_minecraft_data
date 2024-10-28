package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTickTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class GetServerDetailsTask extends LongRunningTask {
   private static final Component APPLYING_PACK_TEXT = Component.translatable("multiplayer.applyingPack");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.connect.connecting");
   private final RealmsServer server;
   private final Screen lastScreen;

   public GetServerDetailsTask(Screen var1, RealmsServer var2) {
      super();
      this.lastScreen = var1;
      this.server = var2;
   }

   public void run() {
      RealmsServerAddress var1;
      try {
         var1 = this.fetchServerAddress();
      } catch (CancellationException var4) {
         LOGGER.info("User aborted connecting to realms");
         return;
      } catch (RealmsServiceException var5) {
         switch (var5.realmsError.errorCode()) {
            case 6002:
               setScreen(new RealmsTermsScreen(this.lastScreen, this.server));
               return;
            case 6006:
               boolean var3 = Minecraft.getInstance().isLocalPlayer(this.server.ownerUUID);
               setScreen((Screen)(var3 ? new RealmsBrokenWorldScreen(this.lastScreen, this.server.id, this.server.isMinigameActive()) : new RealmsGenericErrorScreen(Component.translatable("mco.brokenworld.nonowner.title"), Component.translatable("mco.brokenworld.nonowner.error"), this.lastScreen)));
               return;
            default:
               this.error(var5);
               LOGGER.error("Couldn't connect to world", var5);
               return;
         }
      } catch (TimeoutException var6) {
         this.error(Component.translatable("mco.errorMessage.connectionFailure"));
         return;
      } catch (Exception var7) {
         LOGGER.error("Couldn't connect to world", var7);
         this.error(var7);
         return;
      }

      boolean var2 = var1.resourcePackUrl != null && var1.resourcePackHash != null;
      Object var8 = var2 ? this.resourcePackDownloadConfirmationScreen(var1, generatePackId(this.server), this::connectScreen) : this.connectScreen(var1);
      setScreen((Screen)var8);
   }

   private static UUID generatePackId(RealmsServer var0) {
      return var0.minigameName != null ? UUID.nameUUIDFromBytes(("minigame:" + var0.minigameName).getBytes(StandardCharsets.UTF_8)) : UUID.nameUUIDFromBytes(("realms:" + var0.name + ":" + var0.activeSlot).getBytes(StandardCharsets.UTF_8));
   }

   public Component getTitle() {
      return TITLE;
   }

   private RealmsServerAddress fetchServerAddress() throws RealmsServiceException, TimeoutException, CancellationException {
      RealmsClient var1 = RealmsClient.create();
      int var2 = 0;

      while(var2 < 40) {
         if (this.aborted()) {
            throw new CancellationException();
         }

         try {
            return var1.join(this.server.id);
         } catch (RetryCallException var4) {
            pause((long)var4.delaySeconds);
            ++var2;
         }
      }

      throw new TimeoutException();
   }

   public RealmsLongRunningMcoTaskScreen connectScreen(RealmsServerAddress var1) {
      return new RealmsLongRunningMcoTickTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, var1));
   }

   private PopupScreen resourcePackDownloadConfirmationScreen(RealmsServerAddress var1, UUID var2, Function<RealmsServerAddress, Screen> var3) {
      MutableComponent var4 = Component.translatable("mco.configure.world.resourcepack.question");
      return RealmsPopups.infoPopupScreen(this.lastScreen, var4, (var4x) -> {
         setScreen(new GenericMessageScreen(APPLYING_PACK_TEXT));
         this.scheduleResourcePackDownload(var1, var2).thenRun(() -> {
            setScreen((Screen)var3.apply(var1));
         }).exceptionally((var2x) -> {
            Minecraft.getInstance().getDownloadedPackSource().cleanupAfterDisconnect();
            LOGGER.error("Failed to download resource pack from {}", var1, var2x);
            setScreen(new RealmsGenericErrorScreen(Component.translatable("mco.download.resourcePack.fail"), this.lastScreen));
            return null;
         });
      });
   }

   private CompletableFuture<?> scheduleResourcePackDownload(RealmsServerAddress var1, UUID var2) {
      CompletableFuture var4;
      try {
         DownloadedPackSource var3 = Minecraft.getInstance().getDownloadedPackSource();
         var4 = var3.waitForPackFeedback(var2);
         var3.allowServerPacks();
         var3.pushPack(var2, new URL(var1.resourcePackUrl), var1.resourcePackHash);
         return var4;
      } catch (Exception var5) {
         var4 = new CompletableFuture();
         var4.completeExceptionally(var5);
         return var4;
      }
   }
}
