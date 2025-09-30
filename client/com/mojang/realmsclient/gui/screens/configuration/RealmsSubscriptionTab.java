package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

class RealmsSubscriptionTab extends GridLayoutTab implements RealmsConfigurationTab {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_COMPONENT_WIDTH = 200;
   private static final int EXTRA_SPACING = 2;
   private static final int DEFAULT_SPACING = 6;
   static final Component TITLE = Component.translatable("mco.configure.world.subscription.tab");
   private static final Component SUBSCRIPTION_START_LABEL = Component.translatable("mco.configure.world.subscription.start");
   private static final Component TIME_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.timeleft");
   private static final Component DAYS_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.recurring.daysleft");
   private static final Component SUBSCRIPTION_EXPIRED_TEXT;
   private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
   private static final Component UNKNOWN;
   private static final Component RECURRING_INFO;
   private final RealmsConfigureWorldScreen configurationScreen;
   private final Minecraft minecraft;
   private final Button deleteButton;
   private final FocusableTextWidget subscriptionInfo;
   private final StringWidget startDateWidget;
   private final StringWidget daysLeftLabelWidget;
   private final StringWidget daysLeftWidget;
   private RealmsServer serverData;
   private Component daysLeft;
   private Component startDate;
   @Nullable
   private Subscription.SubscriptionType type;

   RealmsSubscriptionTab(RealmsConfigureWorldScreen var1, Minecraft var2, RealmsServer var3) {
      super(TITLE);
      this.daysLeft = UNKNOWN;
      this.startDate = UNKNOWN;
      this.configurationScreen = var1;
      this.minecraft = var2;
      this.serverData = var3;
      GridLayout.RowHelper var4 = this.layout.rowSpacing(6).createRowHelper(1);
      Font var5 = var1.getFont();
      Objects.requireNonNull(var5);
      var4.addChild(new StringWidget(200, 9, SUBSCRIPTION_START_LABEL, var5));
      Objects.requireNonNull(var5);
      this.startDateWidget = (StringWidget)var4.addChild(new StringWidget(200, 9, this.startDate, var5));
      var4.addChild(SpacerElement.height(2));
      Objects.requireNonNull(var5);
      this.daysLeftLabelWidget = (StringWidget)var4.addChild(new StringWidget(200, 9, TIME_LEFT_LABEL, var5));
      Objects.requireNonNull(var5);
      this.daysLeftWidget = (StringWidget)var4.addChild(new StringWidget(200, 9, this.daysLeft, var5));
      var4.addChild(SpacerElement.height(2));
      var4.addChild(Button.builder(Component.translatable("mco.configure.world.subscription.extend"), (var3x) -> ConfirmLinkScreen.confirmLinkNow(var1, (String)CommonLinks.extendRealms(var3.remoteSubscriptionId, var2.getUser().getProfileId()))).bounds(0, 0, 200, 20).build());
      var4.addChild(SpacerElement.height(2));
      this.deleteButton = (Button)var4.addChild(Button.builder(Component.translatable("mco.configure.world.delete.button"), (var3x) -> var2.setScreen(RealmsPopups.warningPopupScreen(var1, Component.translatable("mco.configure.world.delete.question.line1"), (var1x) -> this.deleteRealm()))).bounds(0, 0, 200, 20).build());
      var4.addChild(SpacerElement.height(2));
      this.subscriptionInfo = (FocusableTextWidget)var4.addChild(new FocusableTextWidget(200, Component.empty(), var5), LayoutSettings.defaults().alignHorizontallyCenter());
      this.subscriptionInfo.setMaxWidth(200);
      this.subscriptionInfo.setCentered(false);
      this.updateData(var3);
   }

   private void deleteRealm() {
      RealmsUtil.RealmsIoConsumer var10000 = (var1) -> var1.deleteRealm(this.serverData.id);
      RealmsConfigureWorldScreen var10001 = this.configurationScreen;
      Objects.requireNonNull(var10001);
      RealmsUtil.runAsync(var10000, RealmsUtil.openScreenAndLogOnFailure(var10001::createErrorScreen, "Couldn't delete world")).thenRunAsync(() -> this.minecraft.setScreen(this.configurationScreen.getLastScreen()), this.minecraft);
      this.minecraft.setScreen(this.configurationScreen);
   }

   private void getSubscription(long var1) {
      RealmsClient var3 = RealmsClient.getOrCreate();

      try {
         Subscription var4 = var3.subscriptionFor(var1);
         this.daysLeft = this.daysLeftPresentation(var4.daysLeft);
         this.startDate = localPresentation(var4.startDate);
         this.type = var4.type;
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't get subscription", var5);
         this.minecraft.setScreen(this.configurationScreen.createErrorScreen(var5));
      }

   }

   private static Component localPresentation(long var0) {
      GregorianCalendar var2 = new GregorianCalendar(TimeZone.getDefault());
      ((Calendar)var2).setTimeInMillis(var0);
      return Component.literal(DateFormat.getDateTimeInstance().format(((Calendar)var2).getTime())).withStyle(ChatFormatting.GRAY);
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
            return Component.translatable("mco.configure.world.subscription.remaining.months.days", var2, var3).withStyle(ChatFormatting.GRAY);
         } else if (var4) {
            return Component.translatable("mco.configure.world.subscription.remaining.months", var2).withStyle(ChatFormatting.GRAY);
         } else {
            return var5 ? Component.translatable("mco.configure.world.subscription.remaining.days", var3).withStyle(ChatFormatting.GRAY) : Component.empty();
         }
      }
   }

   public void updateData(RealmsServer var1) {
      this.serverData = var1;
      this.getSubscription(var1.id);
      this.startDateWidget.setMessage(this.startDate);
      if (this.type == Subscription.SubscriptionType.NORMAL) {
         this.daysLeftLabelWidget.setMessage(TIME_LEFT_LABEL);
      } else if (this.type == Subscription.SubscriptionType.RECURRING) {
         this.daysLeftLabelWidget.setMessage(DAYS_LEFT_LABEL);
      }

      this.daysLeftWidget.setMessage(this.daysLeft);
      boolean var2 = RealmsMainScreen.isSnapshot() && var1.parentWorldName != null;
      this.deleteButton.active = var1.expired;
      if (var2) {
         this.subscriptionInfo.setMessage(Component.translatable("mco.snapshot.subscription.info", var1.parentWorldName));
      } else {
         this.subscriptionInfo.setMessage(RECURRING_INFO);
      }

      this.layout.arrangeElements();
   }

   public Component getTabExtraNarration() {
      return CommonComponents.joinLines(TITLE, SUBSCRIPTION_START_LABEL, this.startDate, TIME_LEFT_LABEL, this.daysLeft);
   }

   static {
      SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.configure.world.subscription.expired").withStyle(ChatFormatting.GRAY);
      SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = Component.translatable("mco.configure.world.subscription.less_than_a_day").withStyle(ChatFormatting.GRAY);
      UNKNOWN = Component.translatable("mco.configure.world.subscription.unknown");
      RECURRING_INFO = Component.translatable("mco.configure.world.subscription.recurring.info");
   }
}
