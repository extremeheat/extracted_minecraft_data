package net.minecraft.client.gui.screens;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProgressListener;
import org.jspecify.annotations.Nullable;

public class ProgressScreen extends Screen implements ProgressListener {
   private @Nullable Component header;
   private @Nullable Component stage;
   private int progress;
   private boolean stop;
   private final boolean clearScreenAfterStop;

   public ProgressScreen(final boolean clearScreenAfterStop) {
      super(GameNarrator.NO_TITLE);
      this.clearScreenAfterStop = clearScreenAfterStop;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean shouldNarrateNavigation() {
      return false;
   }

   public void progressStartNoAbort(final Component string) {
      this.progressStart(string);
   }

   public void progressStart(final Component string) {
      this.header = string;
      this.progressStage(Component.translatable("menu.working"));
   }

   public void progressStage(final Component string) {
      this.stage = string;
      this.progressStagePercentage(0);
   }

   public void progressStagePercentage(final int i) {
      this.progress = i;
   }

   public void stop() {
      this.stop = true;
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      if (this.stop) {
         if (this.clearScreenAfterStop) {
            this.minecraft.setScreen((Screen)null);
         }

      } else {
         super.render(graphics, mouseX, mouseY, a);
         if (this.header != null) {
            graphics.drawCenteredString(this.font, (Component)this.header, this.width / 2, 70, -1);
         }

         if (this.stage != null && this.progress != 0) {
            graphics.drawCenteredString(this.font, (Component)Component.empty().append(this.stage).append(" " + this.progress + "%"), this.width / 2, 90, -1);
         }

      }
   }
}
