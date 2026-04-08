package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.util.RealmsUtil;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.Objects;
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
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
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
   private Subscription.@Nullable SubscriptionType type;

   RealmsSubscriptionTab(final RealmsConfigureWorldScreen configurationScreen, final Minecraft minecraft, final RealmsServer serverData) {
      super(TITLE);
      this.daysLeft = UNKNOWN;
      this.startDate = UNKNOWN;
      this.configurationScreen = configurationScreen;
      this.minecraft = minecraft;
      this.serverData = serverData;
      GridLayout.RowHelper helper = this.layout.rowSpacing(6).createRowHelper(1);
      Font font = configurationScreen.getFont();
      Objects.requireNonNull(font);
      helper.addChild(new StringWidget(200, 9, SUBSCRIPTION_START_LABEL, font));
      Objects.requireNonNull(font);
      this.startDateWidget = (StringWidget)helper.addChild(new StringWidget(200, 9, this.startDate, font));
      helper.addChild(SpacerElement.height(2));
      Objects.requireNonNull(font);
      this.daysLeftLabelWidget = (StringWidget)helper.addChild(new StringWidget(200, 9, TIME_LEFT_LABEL, font));
      Objects.requireNonNull(font);
      this.daysLeftWidget = (StringWidget)helper.addChild(new StringWidget(200, 9, this.daysLeft, font));
      helper.addChild(SpacerElement.height(2));
      helper.addChild(Button.builder(Component.translatable("mco.configure.world.subscription.extend"), (button) -> ConfirmLinkScreen.confirmLinkNow(configurationScreen, (String)CommonLinks.extendRealms(serverData.remoteSubscriptionId, minecraft.getUser().getProfileId()))).bounds(0, 0, 200, 20).build());
      helper.addChild(SpacerElement.height(2));
      this.deleteButton = (Button)helper.addChild(Button.builder(Component.translatable("mco.configure.world.delete.button"), (button) -> minecraft.setScreen(RealmsPopups.warningPopupScreen(configurationScreen, Component.translatable("mco.configure.world.delete.question.line1"), (popup) -> this.deleteRealm()))).bounds(0, 0, 200, 20).build());
      helper.addChild(SpacerElement.height(2));
      this.subscriptionInfo = (FocusableTextWidget)helper.addChild(FocusableTextWidget.builder(Component.empty(), font).maxWidth(200).build(), LayoutSettings.defaults().alignHorizontallyCenter());
      this.subscriptionInfo.setCentered(false);
      this.updateData(serverData);
   }

   private void deleteRealm() {
      RealmsUtil.RealmsIoConsumer var10000 = (client) -> client.deleteRealm(this.serverData.id);
      RealmsConfigureWorldScreen var10001 = this.configurationScreen;
      Objects.requireNonNull(var10001);
      RealmsUtil.runAsync(var10000, RealmsUtil.openScreenAndLogOnFailure(var10001::createErrorScreen, "Couldn't delete world")).thenRunAsync(() -> this.minecraft.setScreen(this.configurationScreen.getLastScreen()), this.minecraft);
      this.minecraft.setScreen(this.configurationScreen);
   }

   private void getSubscription(final long realmId) {
      RealmsClient client = RealmsClient.getOrCreate();

      try {
         Subscription subscription = client.subscriptionFor(realmId);
         this.daysLeft = this.daysLeftPresentation(subscription.daysLeft());
         this.startDate = localPresentation(subscription.startDate());
         this.type = subscription.type();
      } catch (RealmsServiceException e) {
         LOGGER.error("Couldn't get subscription", e);
         this.minecraft.setScreen(this.configurationScreen.createErrorScreen(e));
      }

   }

   private static Component localPresentation(final Instant time) {
      String formattedDate = ZonedDateTime.ofInstant(time, ZoneId.systemDefault()).format(Util.localizedDateFormatter(FormatStyle.MEDIUM));
      return Component.literal(formattedDate).withStyle(ChatFormatting.GRAY);
   }

   private Component daysLeftPresentation(final int daysLeft) {
      if (daysLeft < 0 && this.serverData.expired) {
         return SUBSCRIPTION_EXPIRED_TEXT;
      } else if (daysLeft <= 1) {
         return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
      } else {
         int months = daysLeft / 30;
         int days = daysLeft % 30;
         boolean showMonths = months > 0;
         boolean showDays = days > 0;
         if (showMonths && showDays) {
            return Component.translatable("mco.configure.world.subscription.remaining.months.days", months, days).withStyle(ChatFormatting.GRAY);
         } else if (showMonths) {
            return Component.translatable("mco.configure.world.subscription.remaining.months", months).withStyle(ChatFormatting.GRAY);
         } else {
            return showDays ? Component.translatable("mco.configure.world.subscription.remaining.days", days).withStyle(ChatFormatting.GRAY) : Component.empty();
         }
      }
   }

   public void updateData(final RealmsServer serverData) {
      this.serverData = serverData;
      this.getSubscription(serverData.id);
      this.startDateWidget.setMessage(this.startDate);
      if (this.type == Subscription.SubscriptionType.NORMAL) {
         this.daysLeftLabelWidget.setMessage(TIME_LEFT_LABEL);
      } else if (this.type == Subscription.SubscriptionType.RECURRING) {
         this.daysLeftLabelWidget.setMessage(DAYS_LEFT_LABEL);
      }

      this.daysLeftWidget.setMessage(this.daysLeft);
      boolean snapshotWorld = RealmsMainScreen.isSnapshot() && serverData.parentWorldName != null;
      this.deleteButton.active = serverData.expired;
      if (snapshotWorld) {
         this.subscriptionInfo.setMessage(Component.translatable("mco.snapshot.subscription.info", serverData.parentWorldName));
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
