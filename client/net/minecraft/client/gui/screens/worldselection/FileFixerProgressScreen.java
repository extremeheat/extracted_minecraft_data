package net.minecraft.client.gui.screens.worldselection;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.UpgradeProgress;

public class FileFixerProgressScreen extends Screen {
   private static final int PROGRESS_BAR_WIDTH = 200;
   private static final int PROGRESS_BAR_HEIGHT = 2;
   private static final int LINE_SPACING = 3;
   private static final int SECTION_SPACING = 30;
   private static final Component SCANNING = Component.translatable("upgradeWorld.info.scanning");
   private final UpgradeProgress upgradeProgress;

   public FileFixerProgressScreen(final UpgradeProgress upgradeProgress) {
      super(Component.translatable("upgradeWorld.title"));
      this.upgradeProgress = upgradeProgress;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      int xCenter = this.width / 2;
      int yCenter = this.height / 2;
      int textTop = yCenter - 50;
      this.renderTitle(graphics, xCenter, textTop);
      int totalFiles = this.upgradeProgress.getTotalFileFixStats().totalOperations();
      if (totalFiles > 0) {
         this.renderProgress(graphics, xCenter, textTop);
      } else {
         this.renderScanning(graphics, xCenter, textTop);
      }

   }

   private void renderTitle(final GuiGraphics graphics, final int xCenter, final int yTop) {
      graphics.drawCenteredString(this.font, (Component)this.title, xCenter, yTop, -1);
   }

   private void renderProgress(final GuiGraphics graphics, final int xCenter, final int textTop) {
      UpgradeProgress.FileFixStats typeFileStats = this.upgradeProgress.getTypeFileFixStats();
      UpgradeProgress.FileFixStats totalFileStats = this.upgradeProgress.getTotalFileFixStats();
      UpgradeProgress.FileFixStats runningFileFixerStats = this.upgradeProgress.getRunningFileFixerStats();
      Objects.requireNonNull(this.font);
      int y = textTop + 9 + 3;
      this.renderProgressBar(graphics, xCenter, y, runningFileFixerStats.getProgress());
      y += 7;
      this.renderFileStats(graphics, xCenter, y, totalFileStats.finishedOperations(), totalFileStats.totalOperations());
      Objects.requireNonNull(this.font);
      y += 9 * 2 + 6;
      this.renderFileFixerCount(graphics, xCenter, y, runningFileFixerStats.finishedOperations(), runningFileFixerStats.totalOperations());
      Objects.requireNonNull(this.font);
      y += 9 + 30 - 5;
      this.renderTypeText(graphics, xCenter, y);
      Objects.requireNonNull(this.font);
      y += 9 + 3;
      this.renderProgressBar(graphics, xCenter, y, typeFileStats.getProgress());
      y += 7;
      this.renderTypeProgress(graphics, xCenter, y, typeFileStats.getProgress());
   }

   private void renderProgressBar(final GuiGraphics graphics, final int xCenter, final int y, final float progress) {
      int barLeft = xCenter - 100;
      int barRight = barLeft + 200;
      int barBottom = y + 2;
      graphics.fill(barLeft, y, barRight, barBottom, -16777216);
      graphics.fill(barLeft, y, barLeft + Math.round(progress * 200.0F), barBottom, -16711936);
   }

   private void renderTypeText(final GuiGraphics graphics, final int xCenter, final int y) {
      UpgradeProgress.Type upgradeProgressType = this.upgradeProgress.getType();
      if (upgradeProgressType != null) {
         graphics.drawCenteredString(this.font, upgradeProgressType.label(), xCenter, y, -6250336);
      }

   }

   private void renderTypeProgress(final GuiGraphics graphics, final int xCenter, final int y, final float progress) {
      Component percentageText = Component.translatable("upgradeWorld.progress.percentage", Mth.floor(progress * 100.0F));
      graphics.drawCenteredString(this.font, percentageText, xCenter, y, -6250336);
   }

   private void renderFileStats(final GuiGraphics graphics, final int xCenter, final int yStart, final int converted, final int total) {
      Objects.requireNonNull(this.font);
      int lineHeight = 9 + 3;
      graphics.drawCenteredString(this.font, (Component)Component.translatable("upgradeWorld.info.converted", converted), xCenter, yStart, -6250336);
      graphics.drawCenteredString(this.font, (Component)Component.translatable("upgradeWorld.info.total", total), xCenter, yStart + lineHeight, -6250336);
   }

   private void renderScanning(final GuiGraphics graphics, final int xCenter, final int textTop) {
      Font var10001 = this.font;
      Component var10002 = SCANNING;
      Objects.requireNonNull(this.font);
      graphics.drawCenteredString(var10001, var10002, xCenter, textTop + 9 + 3, -6250336);
   }

   private void renderFileFixerCount(final GuiGraphics graphics, final int xCenter, final int y, final int current, final int total) {
      Component percentageText = Component.translatable("upgradeWorld.info.file_fix_stage", current, total);
      graphics.drawCenteredString(this.font, percentageText, xCenter, y, -6250336);
   }
}
