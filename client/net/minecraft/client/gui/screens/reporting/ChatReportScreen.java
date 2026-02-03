package net.minecraft.client.gui.screens.reporting;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.chat.report.ChatReport;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;

public class ChatReportScreen extends AbstractReportScreen<ChatReport.Builder> {
   private static final Component TITLE = Component.translatable("gui.chatReport.title");
   private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
   private MultiLineEditBox commentBox;
   private Button selectMessagesButton;
   private Button selectReasonButton;

   private ChatReportScreen(final Screen lastScreen, final ReportingContext reportingContext, final ChatReport.Builder reportBuilder) {
      super(TITLE, lastScreen, reportingContext, reportBuilder);
   }

   public ChatReportScreen(final Screen lastScreen, final ReportingContext reportingContext, final UUID playerId) {
      this(lastScreen, reportingContext, new ChatReport.Builder(playerId, reportingContext.sender().reportLimits()));
   }

   public ChatReportScreen(final Screen lastScreen, final ReportingContext reportingContext, final ChatReport draft) {
      this(lastScreen, reportingContext, new ChatReport.Builder(draft, reportingContext.sender().reportLimits()));
   }

   protected void addContent() {
      this.selectMessagesButton = (Button)this.layout.addChild(Button.builder(SELECT_CHAT_MESSAGE, (b) -> this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, this.reportBuilder, (updatedReport) -> {
            this.reportBuilder = updatedReport;
            this.onReportChanged();
         }))).width(280).build());
      this.selectReasonButton = Button.builder(SELECT_REASON, (b) -> this.minecraft.setScreen(new ReportReasonSelectionScreen(this, ((ChatReport.Builder)this.reportBuilder).reason(), ReportType.CHAT, (reason) -> {
            ((ChatReport.Builder)this.reportBuilder).setReason(reason);
            this.onReportChanged();
         }))).width(280).build();
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.selectReasonButton, OBSERVED_WHAT_LABEL));
      Objects.requireNonNull(this.font);
      this.commentBox = this.createCommentBox(280, 9 * 8, (comments) -> {
         ((ChatReport.Builder)this.reportBuilder).setComments(comments);
         this.onReportChanged();
      });
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, (s) -> s.paddingBottom(12)));
   }

   protected void onReportChanged() {
      IntSet reportedMessages = ((ChatReport.Builder)this.reportBuilder).reportedMessages();
      if (reportedMessages.isEmpty()) {
         this.selectMessagesButton.setMessage(SELECT_CHAT_MESSAGE);
      } else {
         this.selectMessagesButton.setMessage(Component.translatable("gui.chatReport.selected_chat", reportedMessages.size()));
      }

      ReportReason reportReason = ((ChatReport.Builder)this.reportBuilder).reason();
      if (reportReason != null) {
         this.selectReasonButton.setMessage(reportReason.title());
      } else {
         this.selectReasonButton.setMessage(SELECT_REASON);
      }

      super.onReportChanged();
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      return super.mouseReleased(event) ? true : this.commentBox.mouseReleased(event);
   }
}
