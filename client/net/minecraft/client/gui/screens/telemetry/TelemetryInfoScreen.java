package net.minecraft.client.gui.screens.telemetry;

import com.mojang.blaze3d.vertex.PoseStack;
import java.nio.file.Path;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class TelemetryInfoScreen extends Screen {
   private static final int PADDING = 8;
   private static final Component TITLE = Component.translatable("telemetry_info.screen.title");
   private static final Component DESCRIPTION = Component.translatable("telemetry_info.screen.description").withStyle(ChatFormatting.GRAY);
   private static final Component BUTTON_GIVE_FEEDBACK = Component.translatable("telemetry_info.button.give_feedback");
   private static final Component BUTTON_SHOW_DATA = Component.translatable("telemetry_info.button.show_data");
   private final Screen lastScreen;
   private final Options options;
   private TelemetryEventWidget telemetryEventWidget;
   private double savedScroll;

   public TelemetryInfoScreen(Screen var1, Options var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.options = var2;
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), DESCRIPTION);
   }

   @Override
   protected void init() {
      FrameLayout var1 = new FrameLayout();
      var1.defaultChildLayoutSetting().padding(8);
      var1.setMinHeight(this.height);
      GridLayout var2 = var1.addChild(new GridLayout(), var1.newChildLayoutSettings().align(0.5F, 0.0F));
      var2.defaultCellSetting().alignHorizontallyCenter().paddingBottom(8);
      GridLayout.RowHelper var3 = var2.createRowHelper(1);
      var3.addChild(new StringWidget(this.getTitle(), this.font));
      var3.addChild(new MultiLineTextWidget(DESCRIPTION, this.font).setMaxWidth(this.width - 16).setCentered(true));
      GridLayout var4 = this.twoButtonContainer(
         Button.builder(BUTTON_GIVE_FEEDBACK, this::openFeedbackLink).build(), Button.builder(BUTTON_SHOW_DATA, this::openDataFolder).build()
      );
      var3.addChild(var4);
      GridLayout var5 = this.twoButtonContainer(this.createTelemetryButton(), Button.builder(CommonComponents.GUI_DONE, this::openLastScreen).build());
      var1.addChild(var5, var1.newChildLayoutSettings().align(0.5F, 1.0F));
      var1.arrangeElements();
      this.telemetryEventWidget = new TelemetryEventWidget(0, 0, this.width - 40, var5.getY() - (var4.getY() + var4.getHeight()) - 16, this.minecraft.font);
      this.telemetryEventWidget.setScrollAmount(this.savedScroll);
      this.telemetryEventWidget.setOnScrolledListener(var1x -> this.savedScroll = var1x);
      this.setInitialFocus(this.telemetryEventWidget);
      var3.addChild(this.telemetryEventWidget);
      var1.arrangeElements();
      FrameLayout.alignInRectangle(var1, 0, 0, this.width, this.height, 0.5F, 0.0F);
      var1.visitWidgets(var1x -> {
      });
   }

   private AbstractWidget createTelemetryButton() {
      AbstractWidget var1 = this.options.telemetryOptInExtra().createButton(this.options, 0, 0, 150, var1x -> this.telemetryEventWidget.onOptInChanged(var1x));
      var1.active = this.minecraft.extraTelemetryAvailable();
      return var1;
   }

   private void openLastScreen(Button var1) {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void openFeedbackLink(Button var1) {
      this.minecraft.setScreen(new ConfirmLinkScreen(var1x -> {
         if (var1x) {
            Util.getPlatform().openUri("https://aka.ms/javafeedback?ref=game");
         }

         this.minecraft.setScreen(this);
      }, "https://aka.ms/javafeedback?ref=game", true));
   }

   private void openDataFolder(Button var1) {
      Path var2 = this.minecraft.getTelemetryManager().getLogDirectory();
      Util.getPlatform().openUri(var2.toUri());
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderDirtBackground(var1);
      super.render(var1, var2, var3, var4);
   }

   private GridLayout twoButtonContainer(AbstractWidget var1, AbstractWidget var2) {
      GridLayout var3 = new GridLayout();
      var3.defaultCellSetting().alignHorizontallyCenter().paddingHorizontal(4);
      var3.addChild(var1, 0, 0);
      var3.addChild(var2, 0, 1);
      return var3;
   }
}
