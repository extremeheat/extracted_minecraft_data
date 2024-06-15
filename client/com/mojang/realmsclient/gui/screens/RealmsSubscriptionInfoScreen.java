package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

public class RealmsSubscriptionInfoScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component SUBSCRIPTION_TITLE = Component.translatable("mco.configure.world.subscription.title");
   private static final Component SUBSCRIPTION_START_LABEL = Component.translatable("mco.configure.world.subscription.start");
   private static final Component TIME_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.timeleft");
   private static final Component DAYS_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.recurring.daysleft");
   private static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.configure.world.subscription.expired");
   private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = Component.translatable("mco.configure.world.subscription.less_than_a_day");
   private static final Component UNKNOWN = Component.translatable("mco.configure.world.subscription.unknown");
   private static final Component RECURRING_INFO = Component.translatable("mco.configure.world.subscription.recurring.info");
   private final Screen lastScreen;
   final RealmsServer serverData;
   final Screen mainScreen;
   private Component daysLeft = UNKNOWN;
   private Component startDate = UNKNOWN;
   @Nullable
   private Subscription.SubscriptionType type;

   public RealmsSubscriptionInfoScreen(Screen var1, RealmsServer var2, Screen var3) {
      super(GameNarrator.NO_TITLE);
      this.lastScreen = var1;
      this.serverData = var2;
      this.mainScreen = var3;
   }

   @Override
   public void init() {
      this.getSubscription(this.serverData.id);
      this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.subscription.extend"),
               var1 -> ConfirmLinkScreen.confirmLinkNow(
                     this, CommonLinks.extendRealms(this.serverData.remoteSubscriptionId, this.minecraft.getUser().getProfileId())
                  )
            )
            .bounds(this.width / 2 - 100, row(6), 200, 20)
            .build()
      );
      if (this.serverData.expired) {
         this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.delete.button"), var1 -> {
            MutableComponent var2 = Component.translatable("mco.configure.world.delete.question.line1");
            MutableComponent var3 = Component.translatable("mco.configure.world.delete.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::deleteRealm, RealmsLongConfirmationScreen.Type.WARNING, var2, var3, true));
         }).bounds(this.width / 2 - 100, row(10), 200, 20).build());
      } else if (RealmsMainScreen.isSnapshot() && this.serverData.parentWorldName != null) {
         this.addRenderableWidget(
            new FittingMultiLineTextWidget(
                  this.width / 2 - 100, row(8), 200, 46, Component.translatable("mco.snapshot.subscription.info", this.serverData.parentWorldName), this.font
               )
               .setColor(-6250336)
         );
      } else {
         this.addRenderableWidget(new FittingMultiLineTextWidget(this.width / 2 - 100, row(8), 200, 46, RECURRING_INFO, this.font).setColor(-6250336));
      }

      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, var1 -> this.onClose()).bounds(this.width / 2 - 100, row(12), 200, 20).build());
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinLines(SUBSCRIPTION_TITLE, SUBSCRIPTION_START_LABEL, this.startDate, TIME_LEFT_LABEL, this.daysLeft);
   }

   private void deleteRealm(boolean var1) {
      if (var1) {
         (new Thread("Realms-delete-realm") {
               @Override
               public void run() {
                  try {
                     RealmsClient var1 = RealmsClient.create();
                     var1.deleteRealm(RealmsSubscriptionInfoScreen.this.serverData.id);
                  } catch (RealmsServiceException var2) {
                     RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world", var2);
                  }

                  RealmsSubscriptionInfoScreen.this.minecraft
                     .execute(() -> RealmsSubscriptionInfoScreen.this.minecraft.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
               }
            })
            .start();
      }

      this.minecraft.setScreen(this);
   }

   private void getSubscription(long var1) {
      RealmsClient var3 = RealmsClient.create();

      try {
         Subscription var4 = var3.subscriptionFor(var1);
         this.daysLeft = this.daysLeftPresentation(var4.daysLeft);
         this.startDate = localPresentation(var4.startDate);
         this.type = var4.type;
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't get subscription", var5);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
      }
   }

   private static Component localPresentation(long var0) {
      GregorianCalendar var2 = new GregorianCalendar(TimeZone.getDefault());
      var2.setTimeInMillis(var0);
      return Component.literal(DateFormat.getDateTimeInstance().format(var2.getTime()));
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = this.width / 2 - 100;
      var1.drawCenteredString(this.font, SUBSCRIPTION_TITLE, this.width / 2, 17, -1);
      var1.drawString(this.font, SUBSCRIPTION_START_LABEL, var5, row(0), -6250336, false);
      var1.drawString(this.font, this.startDate, var5, row(1), -1, false);
      if (this.type == Subscription.SubscriptionType.NORMAL) {
         var1.drawString(this.font, TIME_LEFT_LABEL, var5, row(3), -6250336, false);
      } else if (this.type == Subscription.SubscriptionType.RECURRING) {
         var1.drawString(this.font, DAYS_LEFT_LABEL, var5, row(3), -6250336, false);
      }

      var1.drawString(this.font, this.daysLeft, var5, row(4), -1, false);
   }

   private Component daysLeftPresentation(int var1) {
      if (var1 < 0 && this.serverData.expired) {
         return SUBSCRIPTION_EXPIRED_TEXT;
      } else if (var1 <= 1) {
         return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
      } else {
         int var2 = var1 / 30;
         int var3 = var1 % 30;
         boolean var4 = var2 > 0;
         boolean var5 = var3 > 0;
         if (var4 && var5) {
            return Component.translatable("mco.configure.world.subscription.remaining.months.days", var2, var3);
         } else if (var4) {
            return Component.translatable("mco.configure.world.subscription.remaining.months", var2);
         } else {
            return var5 ? Component.translatable("mco.configure.world.subscription.remaining.days", var3) : Component.empty();
         }
      }
   }
}
