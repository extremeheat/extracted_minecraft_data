package net.minecraft.client.gui.screens.reporting;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;

public class ReportPlayerScreen extends Screen {
   private static final Component TITLE = Component.translatable("gui.abuseReport.title");
   private static final Component MESSAGE = Component.translatable("gui.abuseReport.message");
   private static final Component REPORT_CHAT = Component.translatable("gui.abuseReport.type.chat");
   private static final Component REPORT_SKIN = Component.translatable("gui.abuseReport.type.skin");
   private static final Component REPORT_NAME = Component.translatable("gui.abuseReport.type.name");
   private static final int SPACING = 6;
   private final Screen lastScreen;
   private final Button chatButton;
   private final Button skinButton;
   private final Button nameButton;
   private final LinearLayout layout = LinearLayout.vertical().spacing(6);

   public ReportPlayerScreen(final Screen lastScreen, final ReportingContext context, final UUID playerId, final String playerName, final Supplier<PlayerSkin> skinGetter, final boolean skinReportable, final boolean chatDisabledOrBlocked, final boolean chatReportable, final boolean hasRecentMessages) {
      super(TITLE);
      this.lastScreen = lastScreen;
      this.chatButton = Button.builder(REPORT_CHAT, (var4) -> this.minecraft.gui.setScreen(new ChatReportScreen(lastScreen, context, playerId))).build();
      this.skinButton = Button.builder(REPORT_SKIN, (var5) -> this.minecraft.gui.setScreen(new SkinReportScreen(lastScreen, context, playerId, skinGetter))).build();
      this.nameButton = Button.builder(REPORT_NAME, (var5) -> this.minecraft.gui.setScreen(new NameReportScreen(lastScreen, context, playerId, playerName))).build();
      if (chatDisabledOrBlocked) {
         this.chatButton.active = false;
         this.chatButton.setTooltip(Tooltip.create(Component.translatable("gui.socialInteractions.tooltip.report.chat_disabled_or_blocked")));
      } else if (!chatReportable) {
         this.chatButton.active = false;
         this.chatButton.setTooltip(Tooltip.create(Component.translatable("gui.socialInteractions.tooltip.report.not_reportable")));
      } else if (!hasRecentMessages) {
         this.chatButton.active = false;
         this.chatButton.setTooltip(Tooltip.create(Component.translatable("gui.socialInteractions.tooltip.report.no_messages", playerName)));
      }

      if (!skinReportable) {
         this.skinButton.active = false;
         this.skinButton.setTooltip(Tooltip.create(Component.translatable("gui.socialInteractions.tooltip.report.skin_not_reportable", playerName)));
      }

   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE);
   }

   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.layout.addChild(new StringWidget(this.title, this.font), this.layout.newCellSettings().paddingBottom(6));
      this.layout.addChild((new MultiLineTextWidget(MESSAGE, this.font)).setCentered(true), this.layout.newCellSettings().paddingBottom(6));
      this.layout.addChild(this.chatButton);
      this.layout.addChild(this.skinButton);
      this.layout.addChild(this.nameButton);
      this.layout.addChild(SpacerElement.height(20));
      this.layout.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1) -> this.onClose()).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }
}
