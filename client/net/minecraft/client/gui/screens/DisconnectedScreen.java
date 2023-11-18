package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DisconnectedScreen extends Screen {
   private static final Component TO_SERVER_LIST = Component.translatable("gui.toMenu");
   private static final Component TO_TITLE = Component.translatable("gui.toTitle");
   private final Screen parent;
   private final Component reason;
   private final Component buttonText;
   private final LinearLayout layout = LinearLayout.vertical();

   public DisconnectedScreen(Screen var1, Component var2, Component var3) {
      this(var1, var2, var3, TO_SERVER_LIST);
   }

   public DisconnectedScreen(Screen var1, Component var2, Component var3, Component var4) {
      super(var2);
      this.parent = var1;
      this.reason = var3;
      this.buttonText = var4;
   }

   @Override
   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter().padding(10);
      this.layout.addChild(new StringWidget(this.title, this.font));
      this.layout.addChild(new MultiLineTextWidget(this.reason, this.font).setMaxWidth(this.width - 50).setCentered(true));
      Button var1;
      if (this.minecraft.allowsMultiplayer()) {
         var1 = Button.builder(this.buttonText, var1x -> this.minecraft.setScreen(this.parent)).build();
      } else {
         var1 = Button.builder(TO_TITLE, var1x -> this.minecraft.setScreen(new TitleScreen())).build();
      }

      this.layout.addChild(var1);
      this.layout.arrangeElements();
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.title, this.reason);
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }
}
