package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
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
   private static final Component REPORT_SENDING_TITLE = Component.translatable("gui.abuseReport.sending.title").withStyle(ChatFormatting.BOLD);
   private static final Component REPORT_SENT_TITLE = Component.translatable("gui.abuseReport.sent.title").withStyle(ChatFormatting.BOLD);
   private static final Component REPORT_ERROR_TITLE = Component.translatable("gui.abuseReport.error.title").withStyle(ChatFormatting.BOLD);
   private static final Component REPORT_SEND_GENERIC_ERROR = Component.translatable("gui.abuseReport.send.generic_error");
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   final Screen lastScreen;
   private final ReportingContext reportingContext;
   @Nullable
   private MultiLineLabel reasonDescriptionLabel;
   @Nullable
   private MultiLineEditBox commentBox;
   private Button sendButton;
   private ChatReportBuilder reportBuilder;
   @Nullable
   private ChatReportBuilder.CannotBuildReason cannotBuildReason;

   private ChatReportScreen(@Nullable Screen var1, ReportingContext var2, ChatReportBuilder var3) {
      super(Component.translatable("gui.chatReport.title"));
      this.lastScreen = var1;
      this.reportingContext = var2;
      this.reportBuilder = var3;
   }

   public ChatReportScreen(@Nullable Screen var1, ReportingContext var2, UUID var3) {
      this(var1, var2, new ChatReportBuilder(var3, var2.sender().reportLimits()));
   }

   public ChatReportScreen(@Nullable Screen var1, ReportingContext var2, ChatReportBuilder.ChatReport var3) {
      this(var1, var2, new ChatReportBuilder(var3, var2.sender().reportLimits()));
   }

   @Override
   protected void init() {
      AbuseReportLimits var1 = this.reportingContext.sender().reportLimits();
      int var2 = this.width / 2;
      ReportReason var3 = this.reportBuilder.reason();
      if (var3 != null) {
         this.reasonDescriptionLabel = MultiLineLabel.create(this.font, var3.description(), 280);
      } else {
         this.reasonDescriptionLabel = null;
      }

      IntSet var4 = this.reportBuilder.reportedMessages();
      Object var5;
      if (var4.isEmpty()) {
         var5 = SELECT_CHAT_MESSAGE;
      } else {
         var5 = Component.translatable("gui.chatReport.selected_chat", var4.size());
      }

      this.addRenderableWidget(
         Button.builder(
               (Component)var5, var1x -> this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, this.reportBuilder, var1xx -> {
                     this.reportBuilder = var1xx;
                     this.onReportChanged();
                  }))
            )
            .bounds(this.contentLeft(), this.selectChatTop(), 280, 20)
            .build()
      );
      Component var6 = Util.mapNullable(var3, ReportReason::title, SELECT_REASON);
      this.addRenderableWidget(
         Button.builder(var6, var1x -> this.minecraft.setScreen(new ReportReasonSelectionScreen(this, this.reportBuilder.reason(), var1xx -> {
               this.reportBuilder.setReason(var1xx);
               this.onReportChanged();
            }))).bounds(this.contentLeft(), this.selectInfoTop(), 280, 20).build()
      );
      this.commentBox = this.addRenderableWidget(
         new MultiLineEditBox(
            this.minecraft.font,
            this.contentLeft(),
            this.commentBoxTop(),
            280,
            this.commentBoxBottom() - this.commentBoxTop(),
            DESCRIBE_PLACEHOLDER,
            Component.translatable("gui.chatReport.comments")
         )
      );
      this.commentBox.setValue(this.reportBuilder.comments());
      this.commentBox.setCharacterLimit(var1.maxOpinionCommentsLength());
      this.commentBox.setValueListener(var1x -> {
         this.reportBuilder.setComments(var1x);
         this.onReportChanged();
      });
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).bounds(var2 - 120, this.completeButtonTop(), 120, 20).build()
      );
      this.sendButton = this.addRenderableWidget(
         Button.builder(Component.translatable("gui.chatReport.send"), var1x -> this.sendReport())
            .bounds(var2 + 10, this.completeButtonTop(), 120, 20)
            .build()
      );
      this.onReportChanged();
   }

   private void onReportChanged() {
      this.cannotBuildReason = this.reportBuilder.checkBuildable();
      this.sendButton.active = this.cannotBuildReason == null;
      this.sendButton.setTooltip(Util.mapNullable(this.cannotBuildReason, var0 -> Tooltip.create(var0.message())));
   }

   private void sendReport() {
      this.reportBuilder.build(this.reportingContext).ifLeft(var1 -> {
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
      }).ifRight(var1 -> this.displayReportSendError(var1.message()));
   }

   private void onReportSendSuccess() {
      this.clearDraft();
      this.minecraft
         .setScreen(
            GenericWaitingScreen.createCompleted(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE, CommonComponents.GUI_DONE, () -> this.minecraft.setScreen(null))
         );
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
      this.minecraft
         .setScreen(GenericWaitingScreen.createCompleted(REPORT_ERROR_TITLE, var2, CommonComponents.GUI_BACK, () -> this.minecraft.setScreen(this)));
   }

   void saveDraft() {
      if (this.reportBuilder.hasContent()) {
         this.reportingContext.setChatReportDraft(this.reportBuilder.report().copy());
      }
   }

   void clearDraft() {
      this.reportingContext.setChatReportDraft(null);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      int var5 = this.width / 2;
      RenderSystem.disableDepthTest();
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, var5, 10, 16777215);
      drawCenteredString(var1, this.font, OBSERVED_WHAT_LABEL, var5, this.selectChatTop() - 9 - 6, 16777215);
      if (this.reasonDescriptionLabel != null) {
         this.reasonDescriptionLabel.renderLeftAligned(var1, this.contentLeft(), this.selectInfoTop() + 20 + 5, 9, 16777215);
      }

      drawString(var1, this.font, MORE_COMMENTS_LABEL, this.contentLeft(), this.commentBoxTop() - 9 - 6, 16777215);
      super.render(var1, var2, var3, var4);
      RenderSystem.enableDepthTest();
   }

   @Override
   public void tick() {
      this.commentBox.tick();
      super.tick();
   }

   @Override
   public void onClose() {
      if (this.reportBuilder.hasContent()) {
         this.minecraft.setScreen(new ChatReportScreen.DiscardReportWarningScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }
   }

   @Override
   public void removed() {
      this.saveDraft();
      super.removed();
   }

   @Override
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
         var1 += (this.reasonDescriptionLabel.getLineCount() + 1) * 9;
      }

      return var1;
   }

   private int commentBoxBottom() {
      return this.completeButtonTop() - 20;
   }

   private int completeButtonTop() {
      return this.contentBottom() - 20 - 10;
   }

   class DiscardReportWarningScreen extends WarningScreen {
      private static final Component TITLE = Component.translatable("gui.chatReport.discard.title").withStyle(ChatFormatting.BOLD);
      private static final Component MESSAGE = Component.translatable("gui.chatReport.discard.content");
      private static final Component RETURN = Component.translatable("gui.chatReport.discard.return");
      private static final Component DRAFT = Component.translatable("gui.chatReport.discard.draft");
      private static final Component DISCARD = Component.translatable("gui.chatReport.discard.discard");

      protected DiscardReportWarningScreen() {
         super(TITLE, MESSAGE, MESSAGE);
      }

      @Override
      protected void initButtons(int var1) {
         boolean var2 = true;
         this.addRenderableWidget(Button.builder(RETURN, var1x -> this.onClose()).bounds(this.width / 2 - 155, 100 + var1, 150, 20).build());
         this.addRenderableWidget(Button.builder(DRAFT, var1x -> {
            ChatReportScreen.this.saveDraft();
            this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
         }).bounds(this.width / 2 + 5, 100 + var1, 150, 20).build());
         this.addRenderableWidget(Button.builder(DISCARD, var1x -> {
            ChatReportScreen.this.clearDraft();
            this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
         }).bounds(this.width / 2 - 75, 130 + var1, 150, 20).build());
      }

      @Override
      public void onClose() {
         this.minecraft.setScreen(ChatReportScreen.this);
      }

      @Override
      public boolean shouldCloseOnEsc() {
         return false;
      }

      @Override
      protected void renderTitle(PoseStack var1) {
         drawString(var1, this.font, this.title, this.width / 2 - 155, 30, 16777215);
      }
   }
}
