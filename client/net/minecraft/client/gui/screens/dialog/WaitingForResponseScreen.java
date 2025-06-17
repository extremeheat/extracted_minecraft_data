package net.minecraft.client.gui.screens.dialog;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class WaitingForResponseScreen extends Screen {
   private static final Component TITLE = Component.translatable("gui.waitingForResponse.title");
   private static final Component[] BUTTON_LABELS;
   private static final int BUTTON_VISIBLE_AFTER = 1;
   private static final int BUTTON_ACTIVE_AFTER = 5;
   @Nullable
   private final Screen previousScreen;
   private final HeaderAndFooterLayout layout;
   private final Button closeButton;
   private int ticks;

   public WaitingForResponseScreen(@Nullable Screen var1) {
      super(TITLE);
      this.previousScreen = var1;
      this.layout = new HeaderAndFooterLayout(this, 33, 0);
      this.closeButton = Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onClose()).width(200).build();
   }

   protected void init() {
      super.init();
      this.layout.addTitleHeader(TITLE, this.font);
      this.layout.addToContents(this.closeButton);
      this.closeButton.visible = false;
      this.closeButton.active = false;
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public void tick() {
      super.tick();
      if (!this.closeButton.active) {
         int var1 = this.ticks++ / 20;
         this.closeButton.visible = var1 >= 1;
         this.closeButton.setMessage(BUTTON_LABELS[var1]);
         if (var1 == 5) {
            this.closeButton.active = true;
            this.triggerImmediateNarration(true);
         }
      }

   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return this.closeButton.active;
   }

   public void onClose() {
      this.minecraft.setScreen(this.previousScreen);
   }

   @Nullable
   public Screen previousScreen() {
      return this.previousScreen;
   }

   static {
      BUTTON_LABELS = new Component[]{Component.empty(), Component.translatable("gui.waitingForResponse.button.inactive", 4), Component.translatable("gui.waitingForResponse.button.inactive", 3), Component.translatable("gui.waitingForResponse.button.inactive", 2), Component.translatable("gui.waitingForResponse.button.inactive", 1), CommonComponents.GUI_BACK};
   }
}
