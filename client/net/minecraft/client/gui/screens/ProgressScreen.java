package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProgressListener;

public class ProgressScreen extends Screen implements ProgressListener {
   @Nullable
   private Component header;
   @Nullable
   private Component stage;
   private int progress;
   private boolean stop;
   private final boolean clearScreenAfterStop;

   public ProgressScreen(boolean var1) {
      super(GameNarrator.NO_TITLE);
      this.clearScreenAfterStop = var1;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void progressStartNoAbort(Component var1) {
      this.progressStart(var1);
   }

   public void progressStart(Component var1) {
      this.header = var1;
      this.progressStage(Component.translatable("progress.working"));
   }

   public void progressStage(Component var1) {
      this.stage = var1;
      this.progressStagePercentage(0);
   }

   public void progressStagePercentage(int var1) {
      this.progress = var1;
   }

   public void stop() {
      this.stop = true;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.stop) {
         if (this.clearScreenAfterStop) {
            this.minecraft.setScreen((Screen)null);
         }

      } else {
         this.renderBackground(var1);
         if (this.header != null) {
            drawCenteredString(var1, this.font, this.header, this.width / 2, 70, 16777215);
         }

         if (this.stage != null && this.progress != 0) {
            drawCenteredString(var1, this.font, Component.empty().append(this.stage).append(" " + this.progress + "%"), this.width / 2, 90, 16777215);
         }

         super.render(var1, var2, var3, var4);
      }
   }
}
