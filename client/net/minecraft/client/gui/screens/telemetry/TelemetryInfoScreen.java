package net.minecraft.client.gui.screens.telemetry;

import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class TelemetryInfoScreen extends Screen {
   private static final int PADDING = 8;
   private static final Component TITLE = Component.translatable("telemetry_info.screen.title");
   private static final Component DESCRIPTION = Component.translatable("telemetry_info.screen.description").withStyle(ChatFormatting.GRAY);
   private static final Component BUTTON_PRIVACY_STATEMENT = Component.translatable("telemetry_info.button.privacy_statement");
   private static final Component BUTTON_GIVE_FEEDBACK = Component.translatable("telemetry_info.button.give_feedback");
   private static final Component BUTTON_SHOW_DATA = Component.translatable("telemetry_info.button.show_data");
   private static final Component CHECKBOX_OPT_IN = Component.translatable("telemetry_info.opt_in.description");
   private final Screen lastScreen;
   private final Options options;
   @Nullable
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
      LinearLayout var2 = var1.addChild(LinearLayout.vertical(), var1.newChildLayoutSettings().align(0.5F, 0.0F));
      var2.defaultCellSetting().alignHorizontallyCenter().paddingBottom(8);
      var2.addChild(new StringWidget(this.getTitle(), this.font));
      var2.addChild(new MultiLineTextWidget(DESCRIPTION, this.font).setMaxWidth(this.width - 16).setCentered(true));
      GridLayout var3 = this.twoButtonContainer(
         Button.builder(BUTTON_PRIVACY_STATEMENT, this::openPrivacyStatementLink).build(),
         Button.builder(BUTTON_GIVE_FEEDBACK, this::openFeedbackLink).build()
      );
      var2.addChild(var3);
      Layout var4 = this.createLowerSection();
      var1.arrangeElements();
      var4.arrangeElements();
      int var5 = var3.getY() + var3.getHeight();
      int var6 = var4.getHeight();
      int var7 = this.height - var5 - var6 - 16;
      this.telemetryEventWidget = new TelemetryEventWidget(0, 0, this.width - 40, var7, this.minecraft.font);
      this.telemetryEventWidget.setScrollAmount(this.savedScroll);
      this.telemetryEventWidget.setOnScrolledListener(var1x -> this.savedScroll = var1x);
      this.setInitialFocus(this.telemetryEventWidget);
      var2.addChild(this.telemetryEventWidget);
      var2.addChild(var4);
      var1.arrangeElements();
      FrameLayout.alignInRectangle(var1, 0, 0, this.width, this.height, 0.5F, 0.0F);
      var1.visitWidgets(var1x -> {
      });
   }

   private Layout createLowerSection() {
      LinearLayout var1 = LinearLayout.vertical();
      var1.defaultCellSetting().alignHorizontallyCenter().paddingBottom(4);
      if (this.minecraft.extraTelemetryAvailable()) {
         var1.addChild(this.createTelemetryCheckbox());
      }

      var1.addChild(
         this.twoButtonContainer(
            Button.builder(BUTTON_SHOW_DATA, this::openDataFolder).build(), Button.builder(CommonComponents.GUI_DONE, this::openLastScreen).build()
         )
      );
      return var1;
   }

   private AbstractWidget createTelemetryCheckbox() {
      OptionInstance var1 = this.options.telemetryOptInExtra();
      Checkbox var2 = Checkbox.builder(CHECKBOX_OPT_IN, this.minecraft.font).selected(var1).onValueChange(this::onOptInChanged).build();
      var2.active = this.minecraft.extraTelemetryAvailable();
      return var2;
   }

   private void onOptInChanged(AbstractWidget var1, boolean var2) {
      if (this.telemetryEventWidget != null) {
         this.telemetryEventWidget.onOptInChanged(var2);
      }
   }

   private void openLastScreen(Button var1) {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void openPrivacyStatementLink(Button var1) {
      ConfirmLinkScreen.confirmLinkNow(this, "http://go.microsoft.com/fwlink/?LinkId=521839");
   }

   private void openFeedbackLink(Button var1) {
      ConfirmLinkScreen.confirmLinkNow(this, "https://aka.ms/javafeedback?ref=game");
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
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderDirtBackground(var1);
   }

   private GridLayout twoButtonContainer(AbstractWidget var1, AbstractWidget var2) {
      GridLayout var3 = new GridLayout();
      var3.defaultCellSetting().alignHorizontallyCenter().paddingHorizontal(4);
      var3.addChild(var1, 0, 0);
      var3.addChild(var2, 0, 1);
      return var3;
   }
}
