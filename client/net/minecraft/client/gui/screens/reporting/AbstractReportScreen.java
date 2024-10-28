package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.GenericWaitingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ThrowingComponent;
import org.slf4j.Logger;

public abstract class AbstractReportScreen<B extends Report.Builder<?>> extends Screen {
   private static final Component REPORT_SENT_MESSAGE = Component.translatable("gui.abuseReport.report_sent_msg");
   private static final Component REPORT_SENDING_TITLE;
   private static final Component REPORT_SENT_TITLE;
   private static final Component REPORT_ERROR_TITLE;
   private static final Component REPORT_SEND_GENERIC_ERROR;
   protected static final Component SEND_REPORT;
   protected static final Component OBSERVED_WHAT_LABEL;
   protected static final Component SELECT_REASON;
   private static final Component DESCRIBE_PLACEHOLDER;
   protected static final Component MORE_COMMENTS_LABEL;
   private static final Component MORE_COMMENTS_NARRATION;
   private static final Component ATTESTATION_CHECKBOX;
   protected static final int BUTTON_WIDTH = 120;
   protected static final int MARGIN = 20;
   protected static final int SCREEN_WIDTH = 280;
   protected static final int SPACING = 8;
   private static final Logger LOGGER;
   protected final Screen lastScreen;
   protected final ReportingContext reportingContext;
   protected final LinearLayout layout = LinearLayout.vertical().spacing(8);
   protected B reportBuilder;
   private Checkbox attestation;
   protected Button sendButton;

   protected AbstractReportScreen(Component var1, Screen var2, ReportingContext var3, B var4) {
      super(var1);
      this.lastScreen = var2;
      this.reportingContext = var3;
      this.reportBuilder = var4;
   }

   protected MultiLineEditBox createCommentBox(int var1, int var2, Consumer<String> var3) {
      AbuseReportLimits var4 = this.reportingContext.sender().reportLimits();
      MultiLineEditBox var5 = new MultiLineEditBox(this.font, 0, 0, var1, var2, DESCRIBE_PLACEHOLDER, MORE_COMMENTS_NARRATION);
      var5.setValue(this.reportBuilder.comments());
      var5.setCharacterLimit(var4.maxOpinionCommentsLength());
      var5.setValueListener(var3);
      return var5;
   }

   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.createHeader();
      this.addContent();
      this.createFooter();
      this.onReportChanged();
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   protected void createHeader() {
      this.layout.addChild(new StringWidget(this.title, this.font));
   }

   protected abstract void addContent();

   protected void createFooter() {
      this.attestation = (Checkbox)this.layout.addChild(Checkbox.builder(ATTESTATION_CHECKBOX, this.font).selected(this.reportBuilder.attested()).maxWidth(280).onValueChange((var1x, var2) -> {
         this.reportBuilder.setAttested(var2);
         this.onReportChanged();
      }).build());
      LinearLayout var1 = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(8));
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).width(120).build());
      this.sendButton = (Button)var1.addChild(Button.builder(SEND_REPORT, (var1x) -> {
         this.sendReport();
      }).width(120).build());
   }

   protected void onReportChanged() {
      Report.CannotBuildReason var1 = this.reportBuilder.checkBuildable();
      this.sendButton.active = var1 == null && this.attestation.selected();
      this.sendButton.setTooltip((Tooltip)Optionull.map(var1, Report.CannotBuildReason::tooltip));
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   protected void sendReport() {
      this.reportBuilder.build(this.reportingContext).ifLeft((var1) -> {
         CompletableFuture var2 = this.reportingContext.sender().send(var1.id(), var1.reportType(), var1.report());
         this.minecraft.setScreen(GenericWaitingScreen.createWaiting(REPORT_SENDING_TITLE, CommonComponents.GUI_CANCEL, () -> {
            this.minecraft.setScreen(this);
            var2.cancel(true);
         }));
         var2.handleAsync((var1x, var2x) -> {
            if (var2x == null) {
               this.onReportSendSuccess();
            } else {
               if (var2x instanceof CancellationException) {
                  return null;
               }

               this.onReportSendError(var2x);
            }

            return null;
         }, this.minecraft);
      }).ifRight((var1) -> {
         this.displayReportSendError(var1.message());
      });
   }

   private void onReportSendSuccess() {
      this.clearDraft();
      this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE, CommonComponents.GUI_DONE, () -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   private void onReportSendError(Throwable var1) {
      LOGGER.error("Encountered error while sending abuse report", var1);
      Throwable var4 = var1.getCause();
      Component var2;
      if (var4 instanceof ThrowingComponent var3) {
         var2 = var3.getComponent();
      } else {
         var2 = REPORT_SEND_GENERIC_ERROR;
      }

      this.displayReportSendError(var2);
   }

   private void displayReportSendError(Component var1) {
      MutableComponent var2 = var1.copy().withStyle(ChatFormatting.RED);
      this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_ERROR_TITLE, var2, CommonComponents.GUI_BACK, () -> {
         this.minecraft.setScreen(this);
      }));
   }

   void saveDraft() {
      if (this.reportBuilder.hasContent()) {
         this.reportingContext.setReportDraft(this.reportBuilder.report().copy());
      }

   }

   void clearDraft() {
      this.reportingContext.setReportDraft((Report)null);
   }

   public void onClose() {
      if (this.reportBuilder.hasContent()) {
         this.minecraft.setScreen(new DiscardReportWarningScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public void removed() {
      this.saveDraft();
      super.removed();
   }

   static {
      REPORT_SENDING_TITLE = Component.translatable("gui.abuseReport.sending.title").withStyle(ChatFormatting.BOLD);
      REPORT_SENT_TITLE = Component.translatable("gui.abuseReport.sent.title").withStyle(ChatFormatting.BOLD);
      REPORT_ERROR_TITLE = Component.translatable("gui.abuseReport.error.title").withStyle(ChatFormatting.BOLD);
      REPORT_SEND_GENERIC_ERROR = Component.translatable("gui.abuseReport.send.generic_error");
      SEND_REPORT = Component.translatable("gui.abuseReport.send");
      OBSERVED_WHAT_LABEL = Component.translatable("gui.abuseReport.observed_what");
      SELECT_REASON = Component.translatable("gui.abuseReport.select_reason");
      DESCRIBE_PLACEHOLDER = Component.translatable("gui.abuseReport.describe");
      MORE_COMMENTS_LABEL = Component.translatable("gui.abuseReport.more_comments");
      MORE_COMMENTS_NARRATION = Component.translatable("gui.abuseReport.comments");
      ATTESTATION_CHECKBOX = Component.translatable("gui.abuseReport.attestation");
      LOGGER = LogUtils.getLogger();
   }

   private class DiscardReportWarningScreen extends WarningScreen {
      private static final Component TITLE;
      private static final Component MESSAGE;
      private static final Component RETURN;
      private static final Component DRAFT;
      private static final Component DISCARD;

      protected DiscardReportWarningScreen() {
         super(TITLE, MESSAGE, MESSAGE);
      }

      protected Layout addFooterButtons() {
         LinearLayout var1 = LinearLayout.vertical().spacing(8);
         var1.defaultCellSetting().alignHorizontallyCenter();
         LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(8));
         var2.addChild(Button.builder(RETURN, (var1x) -> {
            this.onClose();
         }).build());
         var2.addChild(Button.builder(DRAFT, (var1x) -> {
            AbstractReportScreen.this.saveDraft();
            this.minecraft.setScreen(AbstractReportScreen.this.lastScreen);
         }).build());
         var1.addChild(Button.builder(DISCARD, (var1x) -> {
            AbstractReportScreen.this.clearDraft();
            this.minecraft.setScreen(AbstractReportScreen.this.lastScreen);
         }).build());
         return var1;
      }

      public void onClose() {
         this.minecraft.setScreen(AbstractReportScreen.this);
      }

      public boolean shouldCloseOnEsc() {
         return false;
      }

      static {
         TITLE = Component.translatable("gui.abuseReport.discard.title").withStyle(ChatFormatting.BOLD);
         MESSAGE = Component.translatable("gui.abuseReport.discard.content");
         RETURN = Component.translatable("gui.abuseReport.discard.return");
         DRAFT = Component.translatable("gui.abuseReport.discard.draft");
         DISCARD = Component.translatable("gui.abuseReport.discard.discard");
      }
   }
}
