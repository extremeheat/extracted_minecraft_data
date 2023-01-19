package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsSubscriptionInfoScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component SUBSCRIPTION_TITLE = Component.translatable("mco.configure.world.subscription.title");
   private static final Component SUBSCRIPTION_START_LABEL = Component.translatable("mco.configure.world.subscription.start");
   private static final Component TIME_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.timeleft");
   private static final Component DAYS_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.recurring.daysleft");
   private static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.configure.world.subscription.expired");
   private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = Component.translatable("mco.configure.world.subscription.less_than_a_day");
   private static final Component MONTH_SUFFIX = Component.translatable("mco.configure.world.subscription.month");
   private static final Component MONTHS_SUFFIX = Component.translatable("mco.configure.world.subscription.months");
   private static final Component DAY_SUFFIX = Component.translatable("mco.configure.world.subscription.day");
   private static final Component DAYS_SUFFIX = Component.translatable("mco.configure.world.subscription.days");
   private static final Component UNKNOWN = Component.translatable("mco.configure.world.subscription.unknown");
   private final Screen lastScreen;
   final RealmsServer serverData;
   final Screen mainScreen;
   private Component daysLeft = UNKNOWN;
   private Component startDate = UNKNOWN;
   @Nullable
   private Subscription.SubscriptionType type;
   private static final String PURCHASE_LINK = "https://aka.ms/ExtendJavaRealms";

   public RealmsSubscriptionInfoScreen(Screen var1, RealmsServer var2, Screen var3) {
      super(NarratorChatListener.NO_TITLE);
      this.lastScreen = var1;
      this.serverData = var2;
      this.mainScreen = var3;
   }

   @Override
   public void init() {
      this.getSubscription(this.serverData.id);
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addRenderableWidget(
         new Button(
            this.width / 2 - 100,
            row(6),
            200,
            20,
            Component.translatable("mco.configure.world.subscription.extend"),
            var1 -> {
               String var2 = "https://aka.ms/ExtendJavaRealms?subscriptionId="
                  + this.serverData.remoteSubscriptionId
                  + "&profileId="
                  + this.minecraft.getUser().getUuid();
               this.minecraft.keyboardHandler.setClipboard(var2);
               Util.getPlatform().openUri(var2);
            }
         )
      );
      this.addRenderableWidget(
         new Button(this.width / 2 - 100, row(12), 200, 20, CommonComponents.GUI_BACK, var1 -> this.minecraft.setScreen(this.lastScreen))
      );
      if (this.serverData.expired) {
         this.addRenderableWidget(new Button(this.width / 2 - 100, row(10), 200, 20, Component.translatable("mco.configure.world.delete.button"), var1 -> {
            MutableComponent var2 = Component.translatable("mco.configure.world.delete.question.line1");
            MutableComponent var3 = Component.translatable("mco.configure.world.delete.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::deleteRealm, RealmsLongConfirmationScreen.Type.Warning, var2, var3, true));
         }));
      }
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
                     var1.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
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
         LOGGER.error("Couldn't get subscription");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
      }
   }

   private static Component localPresentation(long var0) {
      GregorianCalendar var2 = new GregorianCalendar(TimeZone.getDefault());
      var2.setTimeInMillis(var0);
      return Component.literal(DateFormat.getDateTimeInstance().format(var2.getTime()));
   }

   @Override
   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      int var5 = this.width / 2 - 100;
      drawCenteredString(var1, this.font, SUBSCRIPTION_TITLE, this.width / 2, 17, 16777215);
      this.font.draw(var1, SUBSCRIPTION_START_LABEL, (float)var5, (float)row(0), 10526880);
      this.font.draw(var1, this.startDate, (float)var5, (float)row(1), 16777215);
      if (this.type == Subscription.SubscriptionType.NORMAL) {
         this.font.draw(var1, TIME_LEFT_LABEL, (float)var5, (float)row(3), 10526880);
      } else if (this.type == Subscription.SubscriptionType.RECURRING) {
         this.font.draw(var1, DAYS_LEFT_LABEL, (float)var5, (float)row(3), 10526880);
      }

      this.font.draw(var1, this.daysLeft, (float)var5, (float)row(4), 16777215);
      super.render(var1, var2, var3, var4);
   }

   private Component daysLeftPresentation(int var1) {
      if (var1 < 0 && this.serverData.expired) {
         return SUBSCRIPTION_EXPIRED_TEXT;
      } else if (var1 <= 1) {
         return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
      } else {
         int var2 = var1 / 30;
         int var3 = var1 % 30;
         MutableComponent var4 = Component.empty();
         if (var2 > 0) {
            var4.append(Integer.toString(var2)).append(" ");
            if (var2 == 1) {
               var4.append(MONTH_SUFFIX);
            } else {
               var4.append(MONTHS_SUFFIX);
            }
         }

         if (var3 > 0) {
            if (var2 > 0) {
               var4.append(", ");
            }

            var4.append(Integer.toString(var3)).append(" ");
            if (var3 == 1) {
               var4.append(DAY_SUFFIX);
            } else {
               var4.append(DAYS_SUFFIX);
            }
         }

         return var4;
      }
   }
}
