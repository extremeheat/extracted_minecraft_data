package net.minecraft.client.gui.screens.reporting;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.UUID;
import net.minecraft.Optionull;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ChatReport;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ChatReportScreen extends AbstractReportScreen<ChatReport.Builder> {
   private static final int BUTTON_WIDTH = 120;
   private static final Component TITLE = Component.translatable("gui.chatReport.title");
   private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
   private final LinearLayout layout = LinearLayout.vertical().spacing(8);
   private MultiLineEditBox commentBox;
   private Button sendButton;
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

   @Override
   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.layout.addChild(new StringWidget(this.title, this.font));
      this.selectMessagesButton = this.layout
         .addChild(
            Button.builder(
                  SELECT_CHAT_MESSAGE, var1x -> this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, this.reportBuilder, var1xx -> {
                        this.reportBuilder = var1xx;
                        this.onReportChanged();
                     }))
               )
               .width(280)
               .build()
         );
      this.selectReasonButton = Button.builder(
            SELECT_REASON, var1x -> this.minecraft.setScreen(new ReportReasonSelectionScreen(this, this.reportBuilder.reason(), var1xx -> {
                  this.reportBuilder.setReason(var1xx);
                  this.onReportChanged();
               }))
         )
         .width(280)
         .build();
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.selectReasonButton, OBSERVED_WHAT_LABEL));
      this.commentBox = this.createCommentBox(280, 9 * 8, var1x -> {
         this.reportBuilder.setComments(var1x);
         this.onReportChanged();
      });
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, var0 -> var0.paddingBottom(12)));
      LinearLayout var1 = this.layout.addChild(LinearLayout.horizontal().spacing(8));
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).width(120).build());
      this.sendButton = var1.addChild(Button.builder(SEND_REPORT, var1x -> this.sendReport()).width(120).build());
      this.layout.visitWidgets(var1x -> {
      });
      this.repositionElements();
      this.onReportChanged();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   private void onReportChanged() {
      IntSet var1 = this.reportBuilder.reportedMessages();
      if (var1.isEmpty()) {
         this.selectMessagesButton.setMessage(SELECT_CHAT_MESSAGE);
      } else {
         this.selectMessagesButton.setMessage(Component.translatable("gui.chatReport.selected_chat", var1.size()));
      }

      ReportReason var2 = this.reportBuilder.reason();
      if (var2 != null) {
         this.selectReasonButton.setMessage(var2.title());
      } else {
         this.selectReasonButton.setMessage(SELECT_REASON);
      }

      Report.CannotBuildReason var3 = this.reportBuilder.checkBuildable();
      this.sendButton.active = var3 == null;
      this.sendButton.setTooltip(Optionull.map(var3, Report.CannotBuildReason::tooltip));
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      return super.mouseReleased(var1, var3, var5) ? true : this.commentBox.mouseReleased(var1, var3, var5);
   }
}
