package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.GenericWaitingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ThrowingComponent;
import org.slf4j.Logger;

public class ChatReportScreen extends Screen {
   private static final int BUTTON_WIDTH = 120;
   private static final int BUTTON_HEIGHT = 20;
   private static final int BUTTON_MARGIN = 20;
   private static final int BUTTON_MARGIN_HALF = 10;
   private static final int LABEL_HEIGHT = 25;
   private static final int SCREEN_WIDTH = 280;
   private static final int SCREEN_HEIGHT = 300;
   private static final Component OBSERVED_WHAT_LABEL = Component.translatable("gui.chatReport.observed_what");
   private static final Component SELECT_REASON = Component.translatable("gui.chatReport.select_reason");
   private static final Component MORE_COMMENTS_LABEL = Component.translatable("gui.chatReport.more_comments");
   private static final Component DESCRIBE_PLACEHOLDER = Component.translatable("gui.chatReport.describe");
   private static final Component REPORT_SENT_MESSAGE = Component.translatable("gui.chatReport.report_sent_msg");
   private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
   private static final Component REPORT_SENDING_TITLE;
   private static final Component REPORT_SENT_TITLE;
   private static final Component REPORT_ERROR_TITLE;
   private static final Component REPORT_SEND_GENERIC_ERROR;
   private static final Logger LOGGER;
   @Nullable
   final Screen lastScreen;
   private final ReportingContext reportingContext;
   @Nullable
   private MultiLineLabel reasonDescriptionLabel;
   @Nullable
   private MultiLineEditBox commentBox;
   private Button sendButton;
   private ChatReportBuilder report;
   @Nullable
   ChatReportBuilder.CannotBuildReason cannotBuildReason;

   public ChatReportScreen(Screen var1, ReportingContext var2, UUID var3) {
      super(Component.translatable("gui.chatReport.title"));
      this.lastScreen = var1;
      this.reportingContext = var2;
      this.report = new ChatReportBuilder(var3, var2.sender().reportLimits());
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      AbuseReportLimits var1 = this.reportingContext.sender().reportLimits();
      int var2 = this.width / 2;
      ReportReason var3 = this.report.reason();
      if (var3 != null) {
         this.reasonDescriptionLabel = MultiLineLabel.create(this.font, var3.description(), 280);
      } else {
         this.reasonDescriptionLabel = null;
      }

      IntSet var4 = this.report.reportedMessages();
      Object var5;
      if (var4.isEmpty()) {
         var5 = SELECT_CHAT_MESSAGE;
      } else {
         var5 = Component.translatable("gui.chatReport.selected_chat", var4.size());
      }

      this.addRenderableWidget(new Button(this.contentLeft(), this.selectChatTop(), 280, 20, (Component)var5, (var1x) -> {
         this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, this.report, (var1) -> {
            this.report = var1;
            this.onReportChanged();
         }));
      }));
      Component var6 = (Component)Util.mapNullable(var3, ReportReason::title, SELECT_REASON);
      this.addRenderableWidget(new Button(this.contentLeft(), this.selectInfoTop(), 280, 20, var6, (var1x) -> {
         this.minecraft.setScreen(new ReportReasonSelectionScreen(this, this.report.reason(), (var1) -> {
            this.report.setReason(var1);
            this.onReportChanged();
         }));
      }));
      this.commentBox = (MultiLineEditBox)this.addRenderableWidget(new MultiLineEditBox(this.minecraft.font, this.contentLeft(), this.commentBoxTop(), 280, this.commentBoxBottom() - this.commentBoxTop(), DESCRIBE_PLACEHOLDER, Component.translatable("gui.chatReport.comments")));
      this.commentBox.setValue(this.report.comments());
      this.commentBox.setCharacterLimit(var1.maxOpinionCommentsLength());
      this.commentBox.setValueListener((var1x) -> {
         this.report.setComments(var1x);
         this.onReportChanged();
      });
      this.addRenderableWidget(new Button(var2 - 120, this.completeButtonTop(), 120, 20, CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }));
      this.sendButton = (Button)this.addRenderableWidget(new Button(var2 + 10, this.completeButtonTop(), 120, 20, Component.translatable("gui.chatReport.send"), (var1x) -> {
         this.sendReport();
      }, new SubmitButtonTooltip()));
      this.onReportChanged();
   }

   private void onReportChanged() {
      this.cannotBuildReason = this.report.checkBuildable();
      this.sendButton.active = this.cannotBuildReason == null;
   }

   private void sendReport() {
      this.report.build(this.reportingContext).ifLeft((var1) -> {
         CompletableFuture var2 = this.reportingContext.sender().send(var1.id(), var1.report());
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      int var5 = this.width / 2;
      RenderSystem.disableDepthTest();
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, var5, 10, 16777215);
      Font var10001 = this.font;
      Component var10002 = OBSERVED_WHAT_LABEL;
      int var10004 = this.selectChatTop();
      Objects.requireNonNull(this.font);
      drawCenteredString(var1, var10001, var10002, var5, var10004 - 9 - 6, 16777215);
      int var10003;
      if (this.reasonDescriptionLabel != null) {
         MultiLineLabel var10000 = this.reasonDescriptionLabel;
         int var6 = this.contentLeft();
         var10003 = this.selectInfoTop() + 20 + 5;
         Objects.requireNonNull(this.font);
         var10000.renderLeftAligned(var1, var6, var10003, 9, 16777215);
      }

      var10001 = this.font;
      var10002 = MORE_COMMENTS_LABEL;
      var10003 = this.contentLeft();
      var10004 = this.commentBoxTop();
      Objects.requireNonNull(this.font);
      drawString(var1, var10001, var10002, var10003, var10004 - 9 - 6, 16777215);
      super.render(var1, var2, var3, var4);
      RenderSystem.enableDepthTest();
   }

   public void tick() {
      this.commentBox.tick();
      super.tick();
   }

   public void onClose() {
      if (!this.commentBox.getValue().isEmpty()) {
         this.minecraft.setScreen(new DiscardReportWarningScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return super.mouseReleased(var1, var3, var5) ? true : this.commentBox.mouseReleased(var1, var3, var5);
   }

   private int contentLeft() {
      return this.width / 2 - 140;
   }

   private int contentRight() {
      return this.width / 2 + 140;
   }

   private int contentTop() {
      return Math.max((this.height - 300) / 2, 0);
   }

   private int contentBottom() {
      return Math.min((this.height + 300) / 2, this.height);
   }

   private int selectChatTop() {
      return this.contentTop() + 40;
   }

   private int selectInfoTop() {
      return this.selectChatTop() + 10 + 20;
   }

   private int commentBoxTop() {
      int var1 = this.selectInfoTop() + 20 + 25;
      if (this.reasonDescriptionLabel != null) {
         int var10001 = this.reasonDescriptionLabel.getLineCount() + 1;
         Objects.requireNonNull(this.font);
         var1 += var10001 * 9;
      }

      return var1;
   }

   private int commentBoxBottom() {
      return this.completeButtonTop() - 20;
   }

   private int completeButtonTop() {
      return this.contentBottom() - 20 - 10;
   }

   static {
      REPORT_SENDING_TITLE = Component.translatable("gui.abuseReport.sending.title").withStyle(ChatFormatting.BOLD);
      REPORT_SENT_TITLE = Component.translatable("gui.abuseReport.sent.title").withStyle(ChatFormatting.BOLD);
      REPORT_ERROR_TITLE = Component.translatable("gui.abuseReport.error.title").withStyle(ChatFormatting.BOLD);
      REPORT_SEND_GENERIC_ERROR = Component.translatable("gui.abuseReport.send.generic_error");
      LOGGER = LogUtils.getLogger();
   }

   class SubmitButtonTooltip implements Button.OnTooltip {
      SubmitButtonTooltip() {
         super();
      }

      public void onTooltip(Button var1, PoseStack var2, int var3, int var4) {
         if (ChatReportScreen.this.cannotBuildReason != null) {
            Component var5 = ChatReportScreen.this.cannotBuildReason.message();
            ChatReportScreen.this.renderTooltip(var2, ChatReportScreen.this.font.split(var5, Math.max(ChatReportScreen.this.width / 2 - 43, 170)), var3, var4);
         }

      }
   }

   class DiscardReportWarningScreen extends WarningScreen {
      private static final Component TITLE;
      private static final Component MESSAGE;
      private static final Component RETURN;
      private static final Component DISCARD;

      protected DiscardReportWarningScreen() {
         super(TITLE, MESSAGE, MESSAGE);
      }

      protected void initButtons(int var1) {
         this.addRenderableWidget(new Button(this.width / 2 - 155, 100 + var1, 150, 20, RETURN, (var1x) -> {
            this.onClose();
         }));
         this.addRenderableWidget(new Button(this.width / 2 + 5, 100 + var1, 150, 20, DISCARD, (var1x) -> {
            this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
         }));
      }

      public void onClose() {
         this.minecraft.setScreen(ChatReportScreen.this);
      }

      public boolean shouldCloseOnEsc() {
         return false;
      }

      protected void renderTitle(PoseStack var1) {
         drawString(var1, this.font, this.title, this.width / 2 - 155, 30, 16777215);
      }

      static {
         TITLE = Component.translatable("gui.chatReport.discard.title").withStyle(ChatFormatting.BOLD);
         MESSAGE = Component.translatable("gui.chatReport.discard.content");
         RETURN = Component.translatable("gui.chatReport.discard.return");
         DISCARD = Component.translatable("gui.chatReport.discard.discard");
      }
   }
}
