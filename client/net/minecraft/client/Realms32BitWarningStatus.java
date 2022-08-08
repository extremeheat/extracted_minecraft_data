package net.minecraft.client;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.Realms32bitWarningScreen;
import org.slf4j.Logger;

public class Realms32BitWarningStatus {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   @Nullable
   private CompletableFuture<Boolean> subscriptionCheck;
   private boolean warningScreenShown;

   public Realms32BitWarningStatus(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void showRealms32BitWarningIfNeeded(Screen var1) {
      if (!this.minecraft.is64Bit() && !this.minecraft.options.skipRealms32bitWarning && !this.warningScreenShown && this.checkForRealmsSubscription()) {
         this.minecraft.setScreen(new Realms32bitWarningScreen(var1));
         this.warningScreenShown = true;
      }

   }

   private Boolean checkForRealmsSubscription() {
      if (this.subscriptionCheck == null) {
         this.subscriptionCheck = CompletableFuture.supplyAsync(this::hasRealmsSubscription, Util.backgroundExecutor());
      }

      try {
         return (Boolean)this.subscriptionCheck.getNow(false);
      } catch (CompletionException var2) {
         LOGGER.warn("Failed to retrieve realms subscriptions", var2);
         this.warningScreenShown = true;
         return false;
      }
   }

   private boolean hasRealmsSubscription() {
      try {
         return RealmsClient.create(this.minecraft).listWorlds().servers.stream().anyMatch((var1) -> {
            return var1.ownerUUID != null && !var1.expired && var1.ownerUUID.equals(this.minecraft.getUser().getUuid());
         });
      } catch (RealmsServiceException var2) {
         return false;
      }
   }
}
