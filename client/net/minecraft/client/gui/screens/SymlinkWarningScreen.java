package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SymlinkWarningScreen extends Screen {
   private static final Component TITLE = Component.translatable("symlink_warning.title").withStyle(ChatFormatting.BOLD);
   private static final Component MESSAGE_TEXT = Component.translatable("symlink_warning.message", "https://aka.ms/MinecraftSymLinks");
   @Nullable
   private final Screen callbackScreen;
   private final GridLayout layout = new GridLayout().rowSpacing(10);

   public SymlinkWarningScreen(@Nullable Screen var1) {
      super(TITLE);
      this.callbackScreen = var1;
   }

   @Override
   protected void init() {
      super.init();
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      GridLayout.RowHelper var1 = this.layout.createRowHelper(1);
      var1.addChild(new StringWidget(this.title, this.font));
      var1.addChild(new MultiLineTextWidget(MESSAGE_TEXT, this.font).setMaxWidth(this.width - 50).setCentered(true));
      boolean var2 = true;
      GridLayout var3 = new GridLayout().columnSpacing(5);
      GridLayout.RowHelper var4 = var3.createRowHelper(3);
      var4.addChild(
         Button.builder(CommonComponents.GUI_OPEN_IN_BROWSER, var0 -> Util.getPlatform().openUri("https://aka.ms/MinecraftSymLinks")).size(120, 20).build()
      );
      var4.addChild(
         Button.builder(CommonComponents.GUI_COPY_LINK_TO_CLIPBOARD, var1x -> this.minecraft.keyboardHandler.setClipboard("https://aka.ms/MinecraftSymLinks"))
            .size(120, 20)
            .build()
      );
      var4.addChild(Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).size(120, 20).build());
      var1.addChild(var3);
      this.repositionElements();
      this.layout.visitWidgets(this::addRenderableWidget);
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE_TEXT);
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.callbackScreen);
   }
}
