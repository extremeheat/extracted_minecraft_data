package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsJoinInformation;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoConnectTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
import org.slf4j.Logger;

public class GetServerDetailsTask extends LongRunningTask {
   private static final Component APPLYING_PACK_TEXT = Component.translatable("multiplayer.applyingPack");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.connect.connecting");
   private final RealmsServer server;
   private final Screen lastScreen;

   public GetServerDetailsTask(final Screen lastScreen, final RealmsServer server) {
      super();
      this.lastScreen = lastScreen;
      this.server = server;
   }

   public void run() {
      RealmsJoinInformation address;
      try {
         address = this.fetchServerAddress();
      } catch (CancellationException var4) {
         LOGGER.info("User aborted connecting to realms");
         return;
      } catch (RealmsServiceException e) {
         switch (e.realmsError.errorCode()) {
            case 6002:
               setScreen(new RealmsTermsScreen(this.lastScreen, this.server));
               return;
            case 6006:
               boolean isOwner = Minecraft.getInstance().isLocalPlayer(this.server.ownerUUID);
               setScreen((Screen)(isOwner ? new RealmsBrokenWorldScreen(this.lastScreen, this.server.id, this.server.isMinigameActive()) : new RealmsGenericErrorScreen(Component.translatable("mco.brokenworld.nonowner.title"), Component.translatable("mco.brokenworld.nonowner.error"), this.lastScreen)));
               return;
            default:
               this.error(e);
               LOGGER.error("Couldn't connect to world", e);
               return;
         }
      } catch (TimeoutException var6) {
         this.error(Component.translatable("mco.errorMessage.connectionFailure"));
         return;
      } catch (Exception e) {
         LOGGER.error("Couldn't connect to world", e);
         this.error(e);
         return;
      }

      if (address.address() == null) {
         this.error(Component.translatable("mco.errorMessage.connectionFailure"));
      } else {
         boolean requiresResourcePack = address.resourcePackUrl() != null && address.resourcePackHash() != null;
         Screen nextScreen = (Screen)(requiresResourcePack ? this.resourcePackDownloadConfirmationScreen(address, generatePackId(this.server), this::connectScreen) : this.connectScreen(address));
         setScreen(nextScreen);
      }
   }

   private static UUID generatePackId(final RealmsServer serverData) {
      return serverData.minigameName != null ? UUID.nameUUIDFromBytes(("minigame:" + serverData.minigameName).getBytes(StandardCharsets.UTF_8)) : UUID.nameUUIDFromBytes(("realms:" + (String)Objects.requireNonNullElse(serverData.name, "") + ":" + serverData.activeSlot).getBytes(StandardCharsets.UTF_8));
   }

   public Component getTitle() {
      return TITLE;
   }

   private RealmsJoinInformation fetchServerAddress() throws RealmsServiceException, TimeoutException, CancellationException {
      RealmsClient client = RealmsClient.getOrCreate();

      for(int i = 0; i < 40; ++i) {
         if (this.aborted()) {
            throw new CancellationException();
         }

         try {
            return client.join(this.server.id);
         } catch (RetryCallException e) {
            pause((long)e.delaySeconds);
         }
      }

      throw new TimeoutException();
   }

   public RealmsLongRunningMcoTaskScreen connectScreen(final RealmsJoinInformation address) {
      return new RealmsLongRunningMcoConnectTaskScreen(this.lastScreen, address, new ConnectTask(this.lastScreen, this.server, address));
   }

   private PopupScreen resourcePackDownloadConfirmationScreen(final RealmsJoinInformation address, final UUID packId, final Function<RealmsJoinInformation, Screen> onCompletionScreen) {
      Component popupMessage = Component.translatable("mco.configure.world.resourcepack.question");
      return RealmsPopups.infoPopupScreen(this.lastScreen, popupMessage, (popupScreen) -> {
         setScreen(new GenericMessageScreen(APPLYING_PACK_TEXT));
         this.scheduleResourcePackDownload(address, packId).thenRun(() -> setScreen((Screen)onCompletionScreen.apply(address))).exceptionally((e) -> {
            Minecraft.getInstance().getDownloadedPackSource().cleanupAfterDisconnect();
            LOGGER.error("Failed to download resource pack from {}", address, e);
            setScreen(new RealmsGenericErrorScreen(Component.translatable("mco.download.resourcePack.fail"), this.lastScreen));
            return null;
         });
      });
   }

   private CompletableFuture<?> scheduleResourcePackDownload(final RealmsJoinInformation address, final UUID packId) {
      try {
         if (address.resourcePackUrl() == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("resourcePackUrl was null"));
         } else if (address.resourcePackHash() == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("resourcePackHash was null"));
         } else {
            DownloadedPackSource packSource = Minecraft.getInstance().getDownloadedPackSource();
            CompletableFuture<Void> result = packSource.waitForPackFeedback(packId);
            packSource.allowServerPacks();
            packSource.pushPack(packId, new URL(address.resourcePackUrl()), address.resourcePackHash());
            return result;
         }
      } catch (Exception e) {
         return CompletableFuture.failedFuture(e);
      }
   }
}
