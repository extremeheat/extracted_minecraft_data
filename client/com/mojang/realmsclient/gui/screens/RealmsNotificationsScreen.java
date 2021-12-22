package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;

public class RealmsNotificationsScreen extends RealmsScreen {
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   private static final ResourceLocation NEWS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
   private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher(Minecraft.getInstance(), RealmsClient.create());
   private volatile int numberOfPendingInvites;
   static boolean checkedMcoAvailability;
   private static boolean trialAvailable;
   static boolean validClient;
   private static boolean hasUnreadNews;

   public RealmsNotificationsScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public void init() {
      this.checkIfMcoEnabled();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
   }

   public void tick() {
      if ((!this.getRealmsNotificationsEnabled() || !this.inTitleScreen() || !validClient) && !REALMS_DATA_FETCHER.isStopped()) {
         REALMS_DATA_FETCHER.stop();
      } else if (validClient && this.getRealmsNotificationsEnabled()) {
         REALMS_DATA_FETCHER.initWithSpecificTaskList();
         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = REALMS_DATA_FETCHER.isTrialAvailable();
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
         }

         REALMS_DATA_FETCHER.markClean();
      }
   }

   private boolean getRealmsNotificationsEnabled() {
      return this.minecraft.options.realmsNotifications;
   }

   private boolean inTitleScreen() {
      return this.minecraft.screen instanceof TitleScreen;
   }

   private void checkIfMcoEnabled() {
      if (!checkedMcoAvailability) {
         checkedMcoAvailability = true;
         (new Thread("Realms Notification Availability checker #1") {
            public void run() {
               RealmsClient var1 = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse var2 = var1.clientCompatible();
                  if (var2 != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                     return;
                  }
               } catch (RealmsServiceException var3) {
                  if (var3.httpResultCode != 401) {
                     RealmsNotificationsScreen.checkedMcoAvailability = false;
                  }

                  return;
               }

               RealmsNotificationsScreen.validClient = true;
            }
         }).start();
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (validClient) {
         this.drawIcons(var1, var2, var3);
      }

      super.render(var1, var2, var3, var4);
   }

   private void drawIcons(PoseStack var1, int var2, int var3) {
      int var4 = this.numberOfPendingInvites;
      boolean var5 = true;
      int var6 = this.height / 4 + 48;
      int var7 = this.width / 2 + 80;
      int var8 = var6 + 48 + 2;
      int var9 = 0;
      if (hasUnreadNews) {
         RenderSystem.setShaderTexture(0, NEWS_ICON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.pushPose();
         var1.scale(0.4F, 0.4F, 0.4F);
         GuiComponent.blit(var1, (int)((double)(var7 + 2 - var9) * 2.5D), (int)((double)var8 * 2.5D), 0.0F, 0.0F, 40, 40, 40, 40);
         var1.popPose();
         var9 += 14;
      }

      if (var4 != 0) {
         RenderSystem.setShaderTexture(0, INVITE_ICON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         GuiComponent.blit(var1, var7 - var9, var8 - 6, 0.0F, 0.0F, 15, 25, 31, 25);
         var9 += 16;
      }

      if (trialAvailable) {
         RenderSystem.setShaderTexture(0, TRIAL_ICON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         byte var10 = 0;
         if ((Util.getMillis() / 800L & 1L) == 1L) {
            var10 = 8;
         }

         GuiComponent.blit(var1, var7 + 4 - var9, var8 + 4, 0.0F, (float)var10, 8, 8, 8, 16);
      }

   }

   public void removed() {
      REALMS_DATA_FETCHER.stop();
   }
}
