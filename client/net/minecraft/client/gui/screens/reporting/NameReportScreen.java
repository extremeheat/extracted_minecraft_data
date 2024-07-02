package net.minecraft.client.gui.screens.reporting;

import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.NameReport;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NameReportScreen extends AbstractReportScreen<NameReport.Builder> {
   private static final Component TITLE = Component.translatable("gui.abuseReport.name.title");
   private MultiLineEditBox commentBox;

   private NameReportScreen(Screen var1, ReportingContext var2, NameReport.Builder var3) {
      super(TITLE, var1, var2, var3);
   }

   public NameReportScreen(Screen var1, ReportingContext var2, UUID var3, String var4) {
      this(var1, var2, new NameReport.Builder(var3, var4, var2.sender().reportLimits()));
   }

   public NameReportScreen(Screen var1, ReportingContext var2, NameReport var3) {
      this(var1, var2, new NameReport.Builder(var3, var2.sender().reportLimits()));
   }

   @Override
   protected void addContent() {
      MutableComponent var1 = Component.literal(this.reportBuilder.report().getReportedName()).withStyle(ChatFormatting.YELLOW);
      this.layout
         .addChild(
            new StringWidget(Component.translatable("gui.abuseReport.name.reporting", var1), this.font), var0 -> var0.alignHorizontallyLeft().padding(0, 8)
         );
      this.commentBox = this.createCommentBox(280, 9 * 8, var1x -> {
         this.reportBuilder.setComments(var1x);
         this.onReportChanged();
      });
      this.layout.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, var0 -> var0.paddingBottom(12)));
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      return super.mouseReleased(var1, var3, var5) ? true : this.commentBox.mouseReleased(var1, var3, var5);
   }
}
