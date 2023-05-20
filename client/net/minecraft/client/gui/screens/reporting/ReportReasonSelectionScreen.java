package net.minecraft.client.gui.screens.reporting;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ReportReasonSelectionScreen extends Screen {
   private static final Component REASON_TITLE = Component.translatable("gui.abuseReport.reason.title");
   private static final Component REASON_DESCRIPTION = Component.translatable("gui.abuseReport.reason.description");
   private static final Component READ_INFO_LABEL = Component.translatable("gui.chatReport.read_info");
   private static final int FOOTER_HEIGHT = 95;
   private static final int BUTTON_WIDTH = 150;
   private static final int BUTTON_HEIGHT = 20;
   private static final int CONTENT_WIDTH = 320;
   private static final int PADDING = 4;
   @Nullable
   private final Screen lastScreen;
   @Nullable
   private ReportReasonSelectionScreen.ReasonSelectionList reasonSelectionList;
   @Nullable
   ReportReason currentlySelectedReason;
   private final Consumer<ReportReason> onSelectedReason;

   public ReportReasonSelectionScreen(@Nullable Screen var1, @Nullable ReportReason var2, Consumer<ReportReason> var3) {
      super(REASON_TITLE);
      this.lastScreen = var1;
      this.currentlySelectedReason = var2;
      this.onSelectedReason = var3;
   }

   @Override
   protected void init() {
      this.reasonSelectionList = new ReportReasonSelectionScreen.ReasonSelectionList(this.minecraft);
      this.reasonSelectionList.setRenderBackground(false);
      this.addWidget(this.reasonSelectionList);
      ReportReasonSelectionScreen.ReasonSelectionList.Entry var1 = Optionull.map(this.currentlySelectedReason, this.reasonSelectionList::findEntry);
      this.reasonSelectionList.setSelected(var1);
      int var2 = this.width / 2 - 150 - 5;
      this.addRenderableWidget(Button.builder(READ_INFO_LABEL, var1x -> this.minecraft.setScreen(new ConfirmLinkScreen(var1xx -> {
            if (var1xx) {
               Util.getPlatform().openUri("https://aka.ms/aboutjavareporting");
            }

            this.minecraft.setScreen(this);
         }, "https://aka.ms/aboutjavareporting", true))).bounds(var2, this.buttonTop(), 150, 20).build());
      int var3 = this.width / 2 + 5;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, var1x -> {
         ReportReasonSelectionScreen.ReasonSelectionList.Entry var2x = this.reasonSelectionList.getSelected();
         if (var2x != null) {
            this.onSelectedReason.accept(var2x.getReason());
         }

         this.minecraft.setScreen(this.lastScreen);
      }).bounds(var3, this.buttonTop(), 150, 20).build());
      super.init();
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.reasonSelectionList.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 16, 16777215);
      super.render(var1, var2, var3, var4);
      fill(var1, this.contentLeft(), this.descriptionTop(), this.contentRight(), this.descriptionBottom(), 2130706432);
      drawString(var1, this.font, REASON_DESCRIPTION, this.contentLeft() + 4, this.descriptionTop() + 4, -8421505);
      ReportReasonSelectionScreen.ReasonSelectionList.Entry var5 = this.reasonSelectionList.getSelected();
      if (var5 != null) {
         int var6 = this.contentLeft() + 4 + 16;
         int var7 = this.contentRight() - 4;
         int var8 = this.descriptionTop() + 4 + 9 + 2;
         int var9 = this.descriptionBottom() - 4;
         int var10 = var7 - var6;
         int var11 = var9 - var8;
         int var12 = this.font.wordWrapHeight(var5.reason.description(), var10);
         this.font.drawWordWrap(var1, var5.reason.description(), var6, var8 + (var11 - var12) / 2, var10, -1);
      }
   }

   private int buttonTop() {
      return this.height - 20 - 4;
   }

   private int contentLeft() {
      return (this.width - 320) / 2;
   }

   private int contentRight() {
      return (this.width + 320) / 2;
   }

   private int descriptionTop() {
      return this.height - 95 + 4;
   }

   private int descriptionBottom() {
      return this.buttonTop() - 4;
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public class ReasonSelectionList extends ObjectSelectionList<ReportReasonSelectionScreen.ReasonSelectionList.Entry> {
      public ReasonSelectionList(Minecraft var2) {
         super(var2, ReportReasonSelectionScreen.this.width, ReportReasonSelectionScreen.this.height, 40, ReportReasonSelectionScreen.this.height - 95, 18);

         for(ReportReason var6 : ReportReason.values()) {
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

      @Override
      protected int getScrollbarPosition() {
         return this.getRowRight() - 2;
      }

      public void setSelected(@Nullable ReportReasonSelectionScreen.ReasonSelectionList.Entry var1) {
         super.setSelected(var1);
         ReportReasonSelectionScreen.this.currentlySelectedReason = var1 != null ? var1.getReason() : null;
      }

      public class Entry extends ObjectSelectionList.Entry<ReportReasonSelectionScreen.ReasonSelectionList.Entry> {
         final ReportReason reason;

         public Entry(ReportReason var2) {
            super();
            this.reason = var2;
         }

         @Override
         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            int var11 = var4 + 1;
            int var12 = var3 + (var6 - 9) / 2 + 1;
            GuiComponent.drawString(var1, ReportReasonSelectionScreen.this.font, this.reason.title(), var11, var12, -1);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("gui.abuseReport.reason.narration", this.reason.title(), this.reason.description());
         }

         @Override
         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               ReasonSelectionList.this.setSelected(this);
               return true;
            } else {
               return false;
            }
         }

         public ReportReason getReason() {
            return this.reason;
         }
      }
   }
}
