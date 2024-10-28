package net.minecraft.client.gui.screens.reporting;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ChatReport;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;

public class ChatReportScreen extends AbstractReportScreen<ChatReport.Builder> {
   private static final Component TITLE = Component.translatable("gui.chatReport.title");
   private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
   private MultiLineEditBox commentBox;
   private Button selectMessagesButton;
   private Button selectReasonButton;

   private ChatReportScreen(Screen var1, ReportingContext var2, ChatReport.Builder var3) {
      super(TITLE, var1, var2, var3);
   }

   public ChatReportScreen(Screen var1, ReportingContext var2, UUID var3) {
      this(var1, var2, new ChatReport.Builder(var3, var2.sender().reportLimits()));
   }

   public ChatReportScreen(Screen var1, ReportingContext var2, ChatReport var3) {
      this(var1, var2, new ChatReport.Builder(var3, var2.sender().reportLimits()));
   }

   protected void addContent() {
      this.selectMessagesButton = (Button)this.layout.addChild(Button.builder(SELECT_CHAT_MESSAGE, (var1) -> {
         this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, (ChatReport.Builder)this.reportBuilder, (var1x) -> {
            this.reportBuilder = var1x;
            this.onReportChanged();
         }));
      }).width(280).build());
      this.selectReasonButton = Button.builder(SELECT_REASON, (var1) -> {
         this.minecraft.setScreen(new ReportReasonSelectionScreen(this, ((ChatReport.Builder)this.reportBuilder).reason(), (var1x) -> {
            ((ChatReport.Builder)this.reportBuilder).setReason(var1x);
            this.onReportChanged();
         }));
      }).width(280).build();
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.selectReasonButton, OBSERVED_WHAT_LABEL));
      Objects.requireNonNull(this.font);
      this.commentBox = this.createCommentBox(280, 9 * 8, (var1) -> {
         ((ChatReport.Builder)this.reportBuilder).setComments(var1);
         this.onReportChanged();
      });
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, (var0) -> {
         var0.paddingBottom(12);
      }));
   }

   protected void onReportChanged() {
      IntSet var1 = ((ChatReport.Builder)this.reportBuilder).reportedMessages();
      if (var1.isEmpty()) {
         this.selectMessagesButton.setMessage(SELECT_CHAT_MESSAGE);
      } else {
         this.selectMessagesButton.setMessage(Component.translatable("gui.chatReport.selected_chat", var1.size()));
      }

      ReportReason var2 = ((ChatReport.Builder)this.reportBuilder).reason();
      if (var2 != null) {
         this.selectReasonButton.setMessage(var2.title());
      } else {
         this.selectReasonButton.setMessage(SELECT_REASON);
      }

      super.onReportChanged();
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return super.mouseReleased(var1, var3, var5) ? true : this.commentBox.mouseReleased(var1, var3, var5);
   }
}
