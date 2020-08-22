package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

public class RealmsNotificationsScreen extends RealmsScreen {
   private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
   private volatile int numberOfPendingInvites;
   private static boolean checkedMcoAvailability;
   private static boolean trialAvailable;
   private static boolean validClient;
   private static boolean hasUnreadNews;
   private static final List tasks;

   public RealmsNotificationsScreen(RealmsScreen var1) {
   }

   public void init() {
      this.checkIfMcoEnabled();
      this.setKeyboardHandlerSendRepeatsToGui(true);
   }

   public void tick() {
      if ((!Realms.getRealmsNotificationsEnabled() || !Realms.inTitleScreen() || !validClient) && !realmsDataFetcher.isStopped()) {
         realmsDataFetcher.stop();
      } else if (validClient && Realms.getRealmsNotificationsEnabled()) {
         realmsDataFetcher.initWithSpecificTaskList(tasks);
         if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
         }

         if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = realmsDataFetcher.isTrialAvailable();
         }

         if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = realmsDataFetcher.hasUnreadNews();
         }

         realmsDataFetcher.markClean();
      }
   }

   private void checkIfMcoEnabled() {
      if (!checkedMcoAvailability) {
         checkedMcoAvailability = true;
         (new Thread("Realms Notification Availability checker #1") {
            public void run() {
               RealmsClient var1 = RealmsClient.createRealmsClient();

               try {
                  RealmsClient.CompatibleVersionResponse var2 = var1.clientCompatible();
                  if (!var2.equals(RealmsClient.CompatibleVersionResponse.COMPATIBLE)) {
                     return;
                  }
               } catch (RealmsServiceException var3) {
                  if (var3.httpResultCode != 401) {
                     RealmsNotificationsScreen.checkedMcoAvailability = false;
                  }

                  return;
               } catch (IOException var4) {
                  RealmsNotificationsScreen.checkedMcoAvailability = false;
                  return;
               }

               RealmsNotificationsScreen.validClient = true;
            }
         }).start();
      }

   }

   public void render(int var1, int var2, float var3) {
      if (validClient) {
         this.drawIcons(var1, var2);
      }

      super.render(var1, var2, var3);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return super.mouseClicked(var1, var3, var5);
   }

   private void drawIcons(int var1, int var2) {
      int var3 = this.numberOfPendingInvites;
      boolean var4 = true;
      int var5 = this.height() / 4 + 48;
      int var6 = this.width() / 2 + 80;
      int var7 = var5 + 48 + 2;
      int var8 = 0;
      if (hasUnreadNews) {
         RealmsScreen.bind("realms:textures/gui/realms/news_notification_mainscreen.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.4F, 0.4F, 0.4F);
         RealmsScreen.blit((int)((double)(var6 + 2 - var8) * 2.5D), (int)((double)var7 * 2.5D), 0.0F, 0.0F, 40, 40, 40, 40);
         RenderSystem.popMatrix();
         var8 += 14;
      }

      if (var3 != 0) {
         RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(var6 - var8, var7 - 6, 0.0F, 0.0F, 15, 25, 31, 25);
         RenderSystem.popMatrix();
         var8 += 16;
      }

      if (trialAvailable) {
         RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         byte var9 = 0;
         if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
            var9 = 8;
         }

         RealmsScreen.blit(var6 + 4 - var8, var7 + 4, 0.0F, (float)var9, 8, 8, 8, 16);
         RenderSystem.popMatrix();
      }

   }

   public void removed() {
      realmsDataFetcher.stop();
   }

   static {
      tasks = Arrays.asList(RealmsDataFetcher.Task.PENDING_INVITE, RealmsDataFetcher.Task.TRIAL_AVAILABLE, RealmsDataFetcher.Task.UNREAD_NEWS);
   }
}
