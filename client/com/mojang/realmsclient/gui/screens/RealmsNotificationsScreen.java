package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.task.DataFetcher;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;

public class RealmsNotificationsScreen extends RealmsScreen {
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   private static final ResourceLocation NEWS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
   @Nullable
   private DataFetcher.Subscription realmsDataSubscription;
   private volatile int numberOfPendingInvites;
   static boolean checkedMcoAvailability;
   private static boolean trialAvailable;
   static boolean validClient;
   private static boolean hasUnreadNews;

   public RealmsNotificationsScreen() {
      super(GameNarrator.NO_TITLE);
   }

   @Override
   public void init() {
      this.checkIfMcoEnabled();
      if (this.realmsDataSubscription != null) {
         this.realmsDataSubscription.forceUpdate();
      }
   }

   @Override
   public void tick() {
      boolean var1 = this.getRealmsNotificationsEnabled() && this.inTitleScreen() && validClient;
      if (this.realmsDataSubscription == null && var1) {
         this.realmsDataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
      } else if (this.realmsDataSubscription != null && !var1) {
         this.realmsDataSubscription = null;
      }

      if (this.realmsDataSubscription != null) {
         this.realmsDataSubscription.tick();
      }
   }

   private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
      DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
      var2.subscribe(var1.pendingInvitesTask, var1x -> this.numberOfPendingInvites = var1x);
      var2.subscribe(var1.trialAvailabilityTask, var0 -> trialAvailable = var0);
      var2.subscribe(var1.newsTask, var1x -> {
         var1.newsManager.updateUnreadNews(var1x);
         hasUnreadNews = var1.newsManager.hasUnreadNews();
      });
      return var2;
   }

   private boolean getRealmsNotificationsEnabled() {
      return this.minecraft.options.realmsNotifications().get();
   }

   private boolean inTitleScreen() {
      return this.minecraft.screen instanceof TitleScreen;
   }

   private void checkIfMcoEnabled() {
      if (!checkedMcoAvailability) {
         checkedMcoAvailability = true;
         (new Thread("Realms Notification Availability checker #1") {
            @Override
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

   @Override
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
         GuiComponent.blit(var1, (int)((double)(var7 + 2 - var9) * 2.5), (int)((double)var8 * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
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
}
