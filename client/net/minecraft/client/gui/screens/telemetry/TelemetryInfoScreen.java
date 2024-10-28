package net.minecraft.client.gui.screens.telemetry;

import java.net.URI;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

public class TelemetryInfoScreen extends Screen {
   private static final Component TITLE = Component.translatable("telemetry_info.screen.title");
   private static final Component DESCRIPTION = Component.translatable("telemetry_info.screen.description").withColor(-4539718);
   private static final Component BUTTON_PRIVACY_STATEMENT = Component.translatable("telemetry_info.button.privacy_statement");
   private static final Component BUTTON_GIVE_FEEDBACK = Component.translatable("telemetry_info.button.give_feedback");
   private static final Component BUTTON_VIEW_DATA = Component.translatable("telemetry_info.button.show_data");
   private static final Component CHECKBOX_OPT_IN = Component.translatable("telemetry_info.opt_in.description");
   private static final int SPACING = 8;
   private static final boolean EXTRA_TELEMETRY_AVAILABLE = Minecraft.getInstance().extraTelemetryAvailable();
   private final Screen lastScreen;
   private final Options options;
   private final HeaderAndFooterLayout layout;
   @Nullable
   private TelemetryEventWidget telemetryEventWidget;
   @Nullable
   private MultiLineTextWidget description;
   private double savedScroll;

   public TelemetryInfoScreen(Screen var1, Options var2) {
      super(TITLE);
      Objects.requireNonNull(Minecraft.getInstance().font);
      this.layout = new HeaderAndFooterLayout(this, 16 + 9 * 5 + 20, EXTRA_TELEMETRY_AVAILABLE ? 33 + Checkbox.getBoxSize(Minecraft.getInstance().font) : 33);
      this.lastScreen = var1;
      this.options = var2;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), DESCRIPTION);
   }

   protected void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(4));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(TITLE, this.font));
      this.description = (MultiLineTextWidget)var1.addChild((new MultiLineTextWidget(DESCRIPTION, this.font)).setCentered(true));
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(8));
      var2.addChild(Button.builder(BUTTON_PRIVACY_STATEMENT, this::openPrivacyStatementLink).build());
      var2.addChild(Button.builder(BUTTON_GIVE_FEEDBACK, this::openFeedbackLink).build());
      LinearLayout var3 = (LinearLayout)this.layout.addToFooter(LinearLayout.vertical().spacing(4));
      if (EXTRA_TELEMETRY_AVAILABLE) {
         var3.addChild(this.createTelemetryCheckbox());
      }

      LinearLayout var4 = (LinearLayout)var3.addChild(LinearLayout.horizontal().spacing(8));
      var4.addChild(Button.builder(BUTTON_VIEW_DATA, this::openDataFolder).build());
      var4.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.onClose();
      }).build());
      LinearLayout var5 = (LinearLayout)this.layout.addToContents(LinearLayout.vertical().spacing(8));
      this.telemetryEventWidget = (TelemetryEventWidget)var5.addChild(new TelemetryEventWidget(0, 0, this.width - 40, this.layout.getContentHeight(), this.font));
      this.telemetryEventWidget.setOnScrolledListener((var1x) -> {
         this.savedScroll = var1x;
      });
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.telemetryEventWidget != null) {
         this.telemetryEventWidget.setScrollAmount(this.savedScroll);
         this.telemetryEventWidget.setWidth(this.width - 40);
         this.telemetryEventWidget.setHeight(this.layout.getContentHeight());
         this.telemetryEventWidget.updateLayout();
      }

      if (this.description != null) {
         this.description.setMaxWidth(this.width - 16);
      }

      this.layout.arrangeElements();
   }

   protected void setInitialFocus() {
      if (this.telemetryEventWidget != null) {
         this.setInitialFocus(this.telemetryEventWidget);
      }

   }

   private AbstractWidget createTelemetryCheckbox() {
      OptionInstance var1 = this.options.telemetryOptInExtra();
      return Checkbox.builder(CHECKBOX_OPT_IN, this.font).selected(var1).onValueChange(this::onOptInChanged).build();
   }

   private void onOptInChanged(AbstractWidget var1, boolean var2) {
      if (this.telemetryEventWidget != null) {
         this.telemetryEventWidget.onOptInChanged(var2);
      }

   }

   private void openPrivacyStatementLink(Button var1) {
      ConfirmLinkScreen.confirmLinkNow(this, (URI)CommonLinks.PRIVACY_STATEMENT);
   }

   private void openFeedbackLink(Button var1) {
      ConfirmLinkScreen.confirmLinkNow(this, (URI)CommonLinks.RELEASE_FEEDBACK);
   }

   private void openDataFolder(Button var1) {
      Util.getPlatform().openPath(this.minecraft.getTelemetryManager().getLogDirectory());
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}
