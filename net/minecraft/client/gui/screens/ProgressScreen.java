package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.ProgressListener;

public class ProgressScreen extends Screen implements ProgressListener {
   private String title = "";
   private String stage = "";
   private int progress;
   private boolean stop;

   public ProgressScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void progressStartNoAbort(Component var1) {
      this.progressStart(var1);
   }

   public void progressStart(Component var1) {
      this.title = var1.getColoredString();
      this.progressStage(new TranslatableComponent("progress.working", new Object[0]));
   }

   public void progressStage(Component var1) {
      this.stage = var1.getColoredString();
      this.progressStagePercentage(0);
   }

   public void progressStagePercentage(int var1) {
      this.progress = var1;
   }

   public void stop() {
      this.stop = true;
   }

   public void render(int var1, int var2, float var3) {
      if (this.stop) {
         if (!this.minecraft.isConnectedToRealms()) {
            this.minecraft.setScreen((Screen)null);
         }

      } else {
         this.renderBackground();
         this.drawCenteredString(this.font, this.title, this.width / 2, 70, 16777215);
         if (!Objects.equals(this.stage, "") && this.progress != 0) {
            this.drawCenteredString(this.font, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.render(var1, var2, var3);
      }
   }
}
