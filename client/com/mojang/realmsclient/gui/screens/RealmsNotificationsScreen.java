package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.task.DataFetcher;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;

public class RealmsNotificationsScreen extends RealmsScreen {
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   private static final ResourceLocation NEWS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
   private static final ResourceLocation UNSEEN_NOTIFICATION_ICON_LOCATION = new ResourceLocation("minecraft", "textures/gui/unseen_notification.png");
   @Nullable
   private DataFetcher.Subscription realmsDataSubscription;
   @Nullable
   private RealmsNotificationsScreen.DataFetcherConfiguration currentConfiguration;
   private volatile int numberOfPendingInvites;
   static boolean checkedMcoAvailability;
   private static boolean trialAvailable;
   static boolean validClient;
   private static boolean hasUnreadNews;
   private static boolean hasUnseenNotifications;
   private final RealmsNotificationsScreen.DataFetcherConfiguration showAll = new RealmsNotificationsScreen.DataFetcherConfiguration() {
      @Override
      public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
         DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
         RealmsNotificationsScreen.this.addNewsAndInvitesSubscriptions(var1, var2);
         RealmsNotificationsScreen.this.addNotificationsSubscriptions(var1, var2);
         return var2;
      }

      @Override
      public boolean showOldNotifications() {
         return true;
      }
   };
   private final RealmsNotificationsScreen.DataFetcherConfiguration onlyNotifications = new RealmsNotificationsScreen.DataFetcherConfiguration() {
      @Override
      public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
         DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
         RealmsNotificationsScreen.this.addNotificationsSubscriptions(var1, var2);
         return var2;
      }

      @Override
      public boolean showOldNotifications() {
         return false;
      }
   };

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
   public void added() {
      super.added();
      this.minecraft.realmsDataFetcher().notificationsTask.reset();
   }

   @Nullable
   private RealmsNotificationsScreen.DataFetcherConfiguration getConfiguration() {
      boolean var1 = this.inTitleScreen() && validClient;
      if (!var1) {
         return null;
      } else {
         return this.getRealmsNotificationsEnabled() ? this.showAll : this.onlyNotifications;
      }
   }

   @Override
   public void tick() {
      RealmsNotificationsScreen.DataFetcherConfiguration var1 = this.getConfiguration();
      if (!Objects.equals(this.currentConfiguration, var1)) {
         this.currentConfiguration = var1;
         if (this.currentConfiguration != null) {
            this.realmsDataSubscription = this.currentConfiguration.initDataFetcher(this.minecraft.realmsDataFetcher());
         } else {
            this.realmsDataSubscription = null;
         }
      }

      if (this.realmsDataSubscription != null) {
         this.realmsDataSubscription.tick();
      }
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
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (validClient) {
         this.drawIcons(var1, var2, var3);
      }

      super.render(var1, var2, var3, var4);
   }

   private void drawIcons(GuiGraphics var1, int var2, int var3) {
      int var4 = this.numberOfPendingInvites;
      boolean var5 = true;
      int var6 = this.height / 4 + 48;
      int var7 = this.width / 2 + 80;
      int var8 = var6 + 48 + 2;
      int var9 = 0;
      if (hasUnseenNotifications) {
         var1.blit(UNSEEN_NOTIFICATION_ICON_LOCATION, var7 - var9 + 5, var8 + 3, 0.0F, 0.0F, 10, 10, 10, 10);
         var9 += 14;
      }

      if (this.currentConfiguration != null && this.currentConfiguration.showOldNotifications()) {
         if (hasUnreadNews) {
            var1.pose().pushPose();
            var1.pose().scale(0.4F, 0.4F, 0.4F);
            var1.blit(NEWS_ICON_LOCATION, (int)((double)(var7 + 2 - var9) * 2.5), (int)((double)var8 * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
            var1.pose().popPose();
            var9 += 14;
         }

         if (var4 != 0) {
            var1.blit(INVITE_ICON_LOCATION, var7 - var9, var8 - 6, 0.0F, 0.0F, 15, 25, 31, 25);
            var9 += 16;
         }

         if (trialAvailable) {
            byte var10 = 0;
            if ((Util.getMillis() / 800L & 1L) == 1L) {
               var10 = 8;
            }

            var1.blit(TRIAL_ICON_LOCATION, var7 + 4 - var9, var8 + 4, 0.0F, (float)var10, 8, 8, 8, 16);
         }
      }
   }

   void addNewsAndInvitesSubscriptions(RealmsDataFetcher var1, DataFetcher.Subscription var2) {
      var2.subscribe(var1.pendingInvitesTask, var1x -> this.numberOfPendingInvites = var1x);
      var2.subscribe(var1.trialAvailabilityTask, var0 -> trialAvailable = var0);
      var2.subscribe(var1.newsTask, var1x -> {
         var1.newsManager.updateUnreadNews(var1x);
         hasUnreadNews = var1.newsManager.hasUnreadNews();
      });
   }

   void addNotificationsSubscriptions(RealmsDataFetcher var1, DataFetcher.Subscription var2) {
      var2.subscribe(var1.notificationsTask, var0 -> {
         hasUnseenNotifications = false;

         for(RealmsNotification var2x : var0) {
            if (!var2x.seen()) {
               hasUnseenNotifications = true;
               break;
            }
         }
      });
   }

   interface DataFetcherConfiguration {
      DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1);

      boolean showOldNotifications();
   }
}
