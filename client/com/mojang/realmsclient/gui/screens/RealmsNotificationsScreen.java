package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsAvailability;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.task.DataFetcher;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;

public class RealmsNotificationsScreen extends RealmsScreen {
   private static final ResourceLocation UNSEEN_NOTIFICATION_SPRITE = new ResourceLocation("icon/unseen_notification");
   private static final ResourceLocation NEWS_SPRITE = new ResourceLocation("icon/news");
   private static final ResourceLocation INVITE_SPRITE = new ResourceLocation("icon/invite");
   private static final ResourceLocation TRIAL_AVAILABLE_SPRITE = new ResourceLocation("icon/trial_available");
   private final CompletableFuture<Boolean> validClient = RealmsAvailability.get().thenApply((var0) -> {
      return var0.type() == RealmsAvailability.Type.SUCCESS;
   });
   @Nullable
   private DataFetcher.Subscription realmsDataSubscription;
   @Nullable
   private DataFetcherConfiguration currentConfiguration;
   private volatile int numberOfPendingInvites;
   private static boolean trialAvailable;
   private static boolean hasUnreadNews;
   private static boolean hasUnseenNotifications;
   private final DataFetcherConfiguration showAll = new DataFetcherConfiguration() {
      public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
         DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
         RealmsNotificationsScreen.this.addNewsAndInvitesSubscriptions(var1, var2);
         RealmsNotificationsScreen.this.addNotificationsSubscriptions(var1, var2);
         return var2;
      }

      public boolean showOldNotifications() {
         return true;
      }
   };
   private final DataFetcherConfiguration onlyNotifications = new DataFetcherConfiguration() {
      public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
         DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
         RealmsNotificationsScreen.this.addNotificationsSubscriptions(var1, var2);
         return var2;
      }

      public boolean showOldNotifications() {
         return false;
      }
   };

   public RealmsNotificationsScreen() {
      super(GameNarrator.NO_TITLE);
   }

   public void init() {
      if (this.realmsDataSubscription != null) {
         this.realmsDataSubscription.forceUpdate();
      }

   }

   public void added() {
      super.added();
      this.minecraft.realmsDataFetcher().notificationsTask.reset();
   }

   @Nullable
   private DataFetcherConfiguration getConfiguration() {
      boolean var1 = this.inTitleScreen() && (Boolean)this.validClient.getNow(false);
      if (!var1) {
         return null;
      } else {
         return this.getRealmsNotificationsEnabled() ? this.showAll : this.onlyNotifications;
      }
   }

   public void tick() {
      DataFetcherConfiguration var1 = this.getConfiguration();
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
      return (Boolean)this.minecraft.options.realmsNotifications().get();
   }

   private boolean inTitleScreen() {
      return this.minecraft.screen instanceof TitleScreen;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if ((Boolean)this.validClient.getNow(false)) {
         this.drawIcons(var1);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
   }

   private void drawIcons(GuiGraphics var1) {
      int var2 = this.numberOfPendingInvites;
      boolean var3 = true;
      int var4 = this.height / 4 + 48;
      int var5 = this.width / 2 + 100;
      int var6 = var4 + 48 + 2;
      int var7 = var5 - 3;
      if (hasUnseenNotifications) {
         var1.blitSprite(UNSEEN_NOTIFICATION_SPRITE, var7 - 12, var6 + 3, 10, 10);
         var7 -= 16;
      }

      if (this.currentConfiguration != null && this.currentConfiguration.showOldNotifications()) {
         if (hasUnreadNews) {
            var1.blitSprite(NEWS_SPRITE, var7 - 14, var6 + 1, 14, 14);
            var7 -= 16;
         }

         if (var2 != 0) {
            var1.blitSprite(INVITE_SPRITE, var7 - 14, var6 + 1, 14, 14);
            var7 -= 16;
         }

         if (trialAvailable) {
            var1.blitSprite(TRIAL_AVAILABLE_SPRITE, var7 - 10, var6 + 4, 8, 8);
         }
      }

   }

   void addNewsAndInvitesSubscriptions(RealmsDataFetcher var1, DataFetcher.Subscription var2) {
      var2.subscribe(var1.pendingInvitesTask, (var1x) -> {
         this.numberOfPendingInvites = var1x;
      });
      var2.subscribe(var1.trialAvailabilityTask, (var0) -> {
         trialAvailable = var0;
      });
      var2.subscribe(var1.newsTask, (var1x) -> {
         var1.newsManager.updateUnreadNews(var1x);
         hasUnreadNews = var1.newsManager.hasUnreadNews();
      });
   }

   void addNotificationsSubscriptions(RealmsDataFetcher var1, DataFetcher.Subscription var2) {
      var2.subscribe(var1.notificationsTask, (var0) -> {
         hasUnseenNotifications = false;
         Iterator var1 = var0.iterator();

         while(var1.hasNext()) {
            RealmsNotification var2 = (RealmsNotification)var1.next();
            if (!var2.seen()) {
               hasUnseenNotifications = true;
               break;
            }
         }

      });
   }

   private interface DataFetcherConfiguration {
      DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1);

      boolean showOldNotifications();
   }
}
