package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
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

   protected AbstractReportScreen(final Component title, final Screen lastScreen, final ReportingContext reportingContext, final B reportBuilder) {
      super(title);
      this.lastScreen = lastScreen;
      this.reportingContext = reportingContext;
      this.reportBuilder = reportBuilder;
   }

   protected MultiLineEditBox createCommentBox(final int width, final int height, final Consumer<String> valueListener) {
      AbuseReportLimits reportLimits = this.reportingContext.sender().reportLimits();
      MultiLineEditBox commentBox = MultiLineEditBox.builder().setPlaceholder(DESCRIBE_PLACEHOLDER).build(this.font, width, height, MORE_COMMENTS_NARRATION);
      commentBox.setValue(this.reportBuilder.comments());
      commentBox.setCharacterLimit(reportLimits.maxOpinionCommentsLength());
      commentBox.setValueListener(valueListener);
      return commentBox;
   }

   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.createHeader();
      this.addContent();
      this.createFooter();
      this.onReportChanged();
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   protected void createHeader() {
      this.layout.addChild(new StringWidget(this.title, this.font));
   }

   protected abstract void addContent();

   protected void createFooter() {
      this.attestation = (Checkbox)this.layout.addChild(Checkbox.builder(ATTESTATION_CHECKBOX, this.font).selected(this.reportBuilder.attested()).maxWidth(280).onValueChange((checkbox, value) -> {
         this.reportBuilder.setAttested(value);
         this.onReportChanged();
      }).build());
      LinearLayout buttonsLayout = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(8));
      buttonsLayout.addChild(Button.builder(CommonComponents.GUI_BACK, (b) -> this.onClose()).width(120).build());
      this.sendButton = (Button)buttonsLayout.addChild(Button.builder(SEND_REPORT, (b) -> this.sendReport()).width(120).build());
   }

   protected void onReportChanged() {
      Report.CannotBuildReason cannotBuildReason = this.reportBuilder.checkBuildable();
      this.sendButton.active = cannotBuildReason == null && this.attestation.selected();
      this.sendButton.setTooltip((Tooltip)Optionull.map(cannotBuildReason, Report.CannotBuildReason::tooltip));
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   protected void sendReport() {
      this.reportBuilder.build(this.reportingContext).ifLeft((result) -> {
         CompletableFuture<?> sendFuture = this.reportingContext.sender().send(result.id(), result.reportType(), result.report());
         this.minecraft.setScreen(GenericWaitingScreen.createWaiting(REPORT_SENDING_TITLE, CommonComponents.GUI_CANCEL, () -> {
            this.minecraft.setScreen(this);
            sendFuture.cancel(true);
         }));
         sendFuture.handleAsync((ok, throwable) -> {
            if (throwable == null) {
               this.onReportSendSuccess();
            } else {
               if (throwable instanceof CancellationException) {
                  return null;
               }

               this.onReportSendError(throwable);
            }

            return null;
         }, this.minecraft);
      }).ifRight((reason) -> this.displayReportSendError(reason.message()));
   }

   private void onReportSendSuccess() {
      this.clearDraft();
      this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE, CommonComponents.GUI_DONE, () -> this.minecraft.setScreen((Screen)null)));
   }

   private void onReportSendError(final Throwable throwable) {
      LOGGER.error("Encountered error while sending abuse report", throwable);
      Throwable var4 = throwable.getCause();
      Component message;
      if (var4 instanceof ThrowingComponent error) {
         message = error.getComponent();
      } else {
         message = REPORT_SEND_GENERIC_ERROR;
      }

      this.displayReportSendError(message);
   }

   private void displayReportSendError(final Component message) {
      Component styledMessage = message.copy().withStyle(ChatFormatting.RED);
      this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_ERROR_TITLE, styledMessage, CommonComponents.GUI_BACK, () -> this.minecraft.setScreen(this)));
   }

   private void saveDraft() {
      if (this.reportBuilder.hasContent()) {
         this.reportingContext.setReportDraft(this.reportBuilder.report().copy());
      }

   }

   private void clearDraft() {
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
      ATTESTATION_CHECKBOX = Component.translatable("gui.abuseReport.attestation").withColor(-2039584);
      LOGGER = LogUtils.getLogger();
   }

   private class DiscardReportWarningScreen extends WarningScreen {
      private static final Component TITLE;
      private static final Component MESSAGE;
      private static final Component RETURN;
      private static final Component DRAFT;
      private static final Component DISCARD;

      protected DiscardReportWarningScreen() {
         Objects.requireNonNull(AbstractReportScreen.this);
         super(TITLE, MESSAGE, MESSAGE);
      }

      protected Layout addFooterButtons() {
         LinearLayout footer = LinearLayout.vertical().spacing(8);
         footer.defaultCellSetting().alignHorizontallyCenter();
         LinearLayout firstFooterRow = (LinearLayout)footer.addChild(LinearLayout.horizontal().spacing(8));
         firstFooterRow.addChild(Button.builder(RETURN, (button) -> this.onClose()).build());
         firstFooterRow.addChild(Button.builder(DRAFT, (button) -> {
            AbstractReportScreen.this.saveDraft();
            this.minecraft.setScreen(AbstractReportScreen.this.lastScreen);
         }).build());
         footer.addChild(Button.builder(DISCARD, (button) -> {
            AbstractReportScreen.this.clearDraft();
            this.minecraft.setScreen(AbstractReportScreen.this.lastScreen);
         }).build());
         return footer;
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
