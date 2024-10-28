package net.minecraft.client.gui.screens.reporting;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.Optionull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.multiplayer.chat.report.SkinReport;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SkinReportScreen extends AbstractReportScreen<SkinReport.Builder> {
   private static final int BUTTON_WIDTH = 120;
   private static final int SKIN_WIDTH = 85;
   private static final int FORM_WIDTH = 178;
   private static final Component TITLE = Component.translatable("gui.abuseReport.skin.title");
   private final LinearLayout layout;
   private MultiLineEditBox commentBox;
   private Button sendButton;
   private Button selectReasonButton;

   private SkinReportScreen(Screen var1, ReportingContext var2, SkinReport.Builder var3) {
      super(TITLE, var1, var2, var3);
      this.layout = LinearLayout.vertical().spacing(8);
   }

   public SkinReportScreen(Screen var1, ReportingContext var2, UUID var3, Supplier<PlayerSkin> var4) {
      this(var1, var2, new SkinReport.Builder(var3, var4, var2.sender().reportLimits()));
   }

   public SkinReportScreen(Screen var1, ReportingContext var2, SkinReport var3) {
      this(var1, var2, new SkinReport.Builder(var3, var2.sender().reportLimits()));
   }

   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.layout.addChild(new StringWidget(this.title, this.font));
      LinearLayout var1 = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(8));
      var1.defaultCellSetting().alignVerticallyMiddle();
      var1.addChild(new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), ((SkinReport)((SkinReport.Builder)this.reportBuilder).report()).getSkinGetter()));
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.vertical().spacing(8));
      this.selectReasonButton = Button.builder(SELECT_REASON, (var1x) -> {
         this.minecraft.setScreen(new ReportReasonSelectionScreen(this, ((SkinReport.Builder)this.reportBuilder).reason(), (var1) -> {
            ((SkinReport.Builder)this.reportBuilder).setReason(var1);
            this.onReportChanged();
         }));
      }).width(178).build();
      var2.addChild(CommonLayouts.labeledElement(this.font, this.selectReasonButton, OBSERVED_WHAT_LABEL));
      Objects.requireNonNull(this.font);
      this.commentBox = this.createCommentBox(178, 9 * 8, (var1x) -> {
         ((SkinReport.Builder)this.reportBuilder).setComments(var1x);
         this.onReportChanged();
      });
      var2.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, (var0) -> {
         var0.paddingBottom(12);
      }));
      LinearLayout var3 = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(8));
      var3.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).width(120).build());
      this.sendButton = (Button)var3.addChild(Button.builder(SEND_REPORT, (var1x) -> {
         this.sendReport();
      }).width(120).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
      this.onReportChanged();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   private void onReportChanged() {
      ReportReason var1 = ((SkinReport.Builder)this.reportBuilder).reason();
      if (var1 != null) {
         this.selectReasonButton.setMessage(var1.title());
      } else {
         this.selectReasonButton.setMessage(SELECT_REASON);
      }

      Report.CannotBuildReason var2 = ((SkinReport.Builder)this.reportBuilder).checkBuildable();
      this.sendButton.active = var2 == null;
      this.sendButton.setTooltip((Tooltip)Optionull.map(var2, Report.CannotBuildReason::tooltip));
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return super.mouseReleased(var1, var3, var5) ? true : this.commentBox.mouseReleased(var1, var3, var5);
   }
}
