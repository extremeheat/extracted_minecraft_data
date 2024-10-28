package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OutOfMemoryScreen extends Screen {
   private static final Component TITLE = Component.translatable("outOfMemory.title");
   private static final Component MESSAGE = Component.translatable("outOfMemory.message");
   private static final int MESSAGE_WIDTH = 300;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public OutOfMemoryScreen() {
      super(TITLE);
   }

   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.layout.addToContents(new FocusableTextWidget(300, MESSAGE, this.font));
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var1.addChild(Button.builder(CommonComponents.GUI_TO_TITLE, (var1x) -> {
         this.minecraft.setScreen(new TitleScreen());
      }).build());
      var1.addChild(Button.builder(Component.translatable("menu.quit"), (var1x) -> {
         this.minecraft.stop();
      }).build());
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
