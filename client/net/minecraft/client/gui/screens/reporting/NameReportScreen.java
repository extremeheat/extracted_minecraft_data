package net.minecraft.client.gui.screens.reporting;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.NameReport;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NameReportScreen extends AbstractReportScreen<NameReport.Builder> {
   private static final int BUTTON_WIDTH = 120;
   private static final Component TITLE = Component.translatable("gui.abuseReport.name.title");
   private final LinearLayout layout;
   private MultiLineEditBox commentBox;
   private Button sendButton;

   private NameReportScreen(Screen var1, ReportingContext var2, NameReport.Builder var3) {
      super(TITLE, var1, var2, var3);
      this.layout = LinearLayout.vertical().spacing(8);
   }

   public NameReportScreen(Screen var1, ReportingContext var2, UUID var3, String var4) {
      this(var1, var2, new NameReport.Builder(var3, var4, var2.sender().reportLimits()));
   }

   public NameReportScreen(Screen var1, ReportingContext var2, NameReport var3) {
      this(var1, var2, new NameReport.Builder(var3, var2.sender().reportLimits()));
   }

   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.layout.addChild(new StringWidget(this.title, this.font));
      MutableComponent var1 = Component.literal(((NameReport)((NameReport.Builder)this.reportBuilder).report()).getReportedName()).withStyle(ChatFormatting.YELLOW);
      this.layout.addChild(new StringWidget(Component.translatable("gui.abuseReport.name.reporting", var1), this.font), (Consumer)((var0) -> {
         var0.alignHorizontallyLeft().padding(0, 8);
      }));
      Objects.requireNonNull(this.font);
      this.commentBox = this.createCommentBox(280, 9 * 8, (var1x) -> {
         ((NameReport.Builder)this.reportBuilder).setComments(var1x);
         this.onReportChanged();
      });
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, (var0) -> {
         var0.paddingBottom(12);
      }));
      LinearLayout var2 = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(8));
      var2.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).width(120).build());
      this.sendButton = (Button)var2.addChild(Button.builder(SEND_REPORT, (var1x) -> {
         this.sendReport();
      }).width(120).build());
      this.onReportChanged();
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   private void onReportChanged() {
      Report.CannotBuildReason var1 = ((NameReport.Builder)this.reportBuilder).checkBuildable();
      this.sendButton.active = var1 == null;
      this.sendButton.setTooltip((Tooltip)Optionull.map(var1, Report.CannotBuildReason::tooltip));
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return super.mouseReleased(var1, var3, var5) ? true : this.commentBox.mouseReleased(var1, var3, var5);
   }
}
