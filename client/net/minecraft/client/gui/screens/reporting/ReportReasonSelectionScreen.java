package net.minecraft.client.gui.screens.reporting;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

public class ReportReasonSelectionScreen extends Screen {
   private static final Component REASON_TITLE = Component.translatable("gui.abuseReport.reason.title");
   private static final Component REASON_DESCRIPTION = Component.translatable("gui.abuseReport.reason.description");
   private static final Component READ_INFO_LABEL = Component.translatable("gui.abuseReport.read_info");
   private static final int DESCRIPTION_BOX_WIDTH = 320;
   private static final int DESCRIPTION_BOX_HEIGHT = 62;
   private static final int PADDING = 4;
   @Nullable
   private final Screen lastScreen;
   @Nullable
   private ReportReasonSelectionScreen.ReasonSelectionList reasonSelectionList;
   @Nullable
   ReportReason currentlySelectedReason;
   private final Consumer<ReportReason> onSelectedReason;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public ReportReasonSelectionScreen(@Nullable Screen var1, @Nullable ReportReason var2, Consumer<ReportReason> var3) {
      super(REASON_TITLE);
      this.lastScreen = var1;
      this.currentlySelectedReason = var2;
      this.onSelectedReason = var3;
   }

   @Override
   protected void init() {
      this.layout.addTitleHeader(REASON_TITLE, this.font);
      LinearLayout var1 = this.layout.addToContents(LinearLayout.vertical().spacing(4));
      this.reasonSelectionList = var1.addChild(new ReportReasonSelectionScreen.ReasonSelectionList(this.minecraft));
      ReportReasonSelectionScreen.ReasonSelectionList.Entry var2 = Optionull.map(this.currentlySelectedReason, this.reasonSelectionList::findEntry);
      this.reasonSelectionList.setSelected(var2);
      var1.addChild(SpacerElement.height(this.descriptionHeight()));
      LinearLayout var3 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var3.addChild(Button.builder(READ_INFO_LABEL, ConfirmLinkScreen.confirmLink(this, CommonLinks.REPORTING_HELP)).build());
      var3.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> {
         ReportReasonSelectionScreen.ReasonSelectionList.Entry var2x = this.reasonSelectionList.getSelected();
         if (var2x != null) {
            this.onSelectedReason.accept(var2x.getReason());
         }

         this.minecraft.setScreen(this.lastScreen);
      }).build());
      this.layout.visitWidgets(var1x -> {
         AbstractWidget var10000 = this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.reasonSelectionList != null) {
         this.reasonSelectionList.updateSizeAndPosition(this.width, this.listHeight(), this.layout.getHeaderHeight());
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.fill(this.descriptionLeft(), this.descriptionTop(), this.descriptionRight(), this.descriptionBottom(), -16777216);
      var1.renderOutline(this.descriptionLeft(), this.descriptionTop(), this.descriptionWidth(), this.descriptionHeight(), -1);
      var1.drawString(this.font, REASON_DESCRIPTION, this.descriptionLeft() + 4, this.descriptionTop() + 4, -1);
      ReportReasonSelectionScreen.ReasonSelectionList.Entry var5 = this.reasonSelectionList.getSelected();
      if (var5 != null) {
         int var6 = this.descriptionLeft() + 4 + 16;
         int var7 = this.descriptionRight() - 4;
         int var8 = this.descriptionTop() + 4 + 9 + 2;
         int var9 = this.descriptionBottom() - 4;
         int var10 = var7 - var6;
         int var11 = var9 - var8;
         int var12 = this.font.wordWrapHeight(var5.reason.description(), var10);
         var1.drawWordWrap(this.font, var5.reason.description(), var6, var8 + (var11 - var12) / 2, var10, -1);
      }
   }

   private int descriptionLeft() {
      return (this.width - 320) / 2;
   }

   private int descriptionRight() {
      return (this.width + 320) / 2;
   }

   private int descriptionTop() {
      return this.descriptionBottom() - this.descriptionHeight();
   }

   private int descriptionBottom() {
      return this.height - this.layout.getFooterHeight() - 4;
   }

   private int descriptionWidth() {
      return 320;
   }

   private int descriptionHeight() {
      return 62;
   }

   int listHeight() {
      return this.layout.getContentHeight() - this.descriptionHeight() - 8;
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public class ReasonSelectionList extends ObjectSelectionList<ReportReasonSelectionScreen.ReasonSelectionList.Entry> {
      public ReasonSelectionList(final Minecraft nullx) {
         super(
            nullx,
            ReportReasonSelectionScreen.this.width,
            ReportReasonSelectionScreen.this.listHeight(),
            ReportReasonSelectionScreen.this.layout.getHeaderHeight(),
            18
         );

         for (ReportReason var6 : ReportReason.values()) {
            this.addEntry(new ReportReasonSelectionScreen.ReasonSelectionList.Entry(var6));
         }
      }

      @Nullable
      public ReportReasonSelectionScreen.ReasonSelectionList.Entry findEntry(ReportReason var1) {
         return this.children().stream().filter(var1x -> var1x.reason == var1).findFirst().orElse(null);
      }

      @Override
      public int getRowWidth() {
         return 320;
      }

      public void setSelected(@Nullable ReportReasonSelectionScreen.ReasonSelectionList.Entry var1) {
         super.setSelected(var1);
         ReportReasonSelectionScreen.this.currentlySelectedReason = var1 != null ? var1.getReason() : null;
      }

      public class Entry extends ObjectSelectionList.Entry<ReportReasonSelectionScreen.ReasonSelectionList.Entry> {
         final ReportReason reason;

         public Entry(final ReportReason nullx) {
            super();
            this.reason = nullx;
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            int var11 = var4 + 1;
            int var12 = var3 + (var6 - 9) / 2 + 1;
            var1.drawString(ReportReasonSelectionScreen.this.font, this.reason.title(), var11, var12, -1);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("gui.abuseReport.reason.narration", this.reason.title(), this.reason.description());
         }

         @Override
         public boolean mouseClicked(double var1, double var3, int var5) {
            ReasonSelectionList.this.setSelected(this);
            return super.mouseClicked(var1, var3, var5);
         }

         public ReportReason getReason() {
            return this.reason;
         }
      }
   }
}
