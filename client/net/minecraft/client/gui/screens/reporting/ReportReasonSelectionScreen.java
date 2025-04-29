package net.minecraft.client.gui.screens.reporting;

import java.net.URI;
import java.util.Objects;
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
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
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
   private ReasonSelectionList reasonSelectionList;
   @Nullable
   ReportReason currentlySelectedReason;
   private final Consumer<ReportReason> onSelectedReason;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   final ReportType reportType;

   public ReportReasonSelectionScreen(@Nullable Screen var1, @Nullable ReportReason var2, ReportType var3, Consumer<ReportReason> var4) {
      super(REASON_TITLE);
      this.lastScreen = var1;
      this.currentlySelectedReason = var2;
      this.onSelectedReason = var4;
      this.reportType = var3;
   }

   protected void init() {
      this.layout.addTitleHeader(REASON_TITLE, this.font);
      LinearLayout var1 = (LinearLayout)this.layout.addToContents(LinearLayout.vertical().spacing(4));
      this.reasonSelectionList = (ReasonSelectionList)var1.addChild(new ReasonSelectionList(this.minecraft));
      ReportReason var10000 = this.currentlySelectedReason;
      ReasonSelectionList var10001 = this.reasonSelectionList;
      Objects.requireNonNull(var10001);
      ReasonSelectionList.Entry var2 = (ReasonSelectionList.Entry)Optionull.map(var10000, var10001::findEntry);
      this.reasonSelectionList.setSelected(var2);
      var1.addChild(SpacerElement.height(this.descriptionHeight()));
      LinearLayout var3 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var3.addChild(Button.builder(READ_INFO_LABEL, ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.REPORTING_HELP)).build());
      var3.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         ReasonSelectionList.Entry var2 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
         if (var2 != null) {
            this.onSelectedReason.accept(var2.getReason());
         }

         this.minecraft.setScreen(this.lastScreen);
      }).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.reasonSelectionList != null) {
         this.reasonSelectionList.updateSizeAndPosition(this.width, this.listHeight(), this.layout.getHeaderHeight());
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.fill(this.descriptionLeft(), this.descriptionTop(), this.descriptionRight(), this.descriptionBottom(), -16777216);
      var1.renderOutline(this.descriptionLeft(), this.descriptionTop(), this.descriptionWidth(), this.descriptionHeight(), -1);
      var1.drawString(this.font, (Component)REASON_DESCRIPTION, this.descriptionLeft() + 4, this.descriptionTop() + 4, -1);
      ReasonSelectionList.Entry var5 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
      if (var5 != null) {
         int var6 = this.descriptionLeft() + 4 + 16;
         int var7 = this.descriptionRight() - 4;
         int var10000 = this.descriptionTop() + 4;
         Objects.requireNonNull(this.font);
         int var8 = var10000 + 9 + 2;
         int var9 = this.descriptionBottom() - 4;
         int var10 = var7 - var6;
         int var11 = var9 - var8;
         int var12 = this.font.wordWrapHeight((FormattedText)var5.reason.description(), var10);
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

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public class ReasonSelectionList extends ObjectSelectionList<Entry> {
      public ReasonSelectionList(final Minecraft var2) {
         super(var2, ReportReasonSelectionScreen.this.width, ReportReasonSelectionScreen.this.listHeight(), ReportReasonSelectionScreen.this.layout.getHeaderHeight(), 18);

         for(ReportReason var6 : ReportReason.values()) {
            if (!ReportReason.getIncompatibleCategories(ReportReasonSelectionScreen.this.reportType).contains(var6)) {
               this.addEntry(new Entry(var6));
            }
         }

      }

      @Nullable
      public Entry findEntry(ReportReason var1) {
         return (Entry)this.children().stream().filter((var1x) -> var1x.reason == var1).findFirst().orElse((Object)null);
      }

      public int getRowWidth() {
         return 320;
      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         ReportReasonSelectionScreen.this.currentlySelectedReason = var1 != null ? var1.getReason() : null;
      }

      public class Entry extends ObjectSelectionList.Entry<Entry> {
         final ReportReason reason;

         public Entry(final ReportReason var2) {
            super();
            this.reason = var2;
         }

         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            int var11 = var4 + 1;
            Objects.requireNonNull(ReportReasonSelectionScreen.this.font);
            int var12 = var3 + (var6 - 9) / 2 + 1;
            var1.drawString(ReportReasonSelectionScreen.this.font, (Component)this.reason.title(), var11, var12, -1);
         }

         public Component getNarration() {
            return Component.translatable("gui.abuseReport.reason.narration", this.reason.title(), this.reason.description());
         }

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
