package net.minecraft.client.gui.screens;

import java.net.URI;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

public class NoticeWithLinkScreen extends Screen {
   private static final Component SYMLINK_WORLD_TITLE;
   private static final Component SYMLINK_WORLD_MESSAGE_TEXT;
   private static final Component SYMLINK_PACK_TITLE;
   private static final Component SYMLINK_PACK_MESSAGE_TEXT;
   private final Component message;
   private final URI uri;
   private final Runnable onClose;
   private final GridLayout layout = (new GridLayout()).rowSpacing(10);

   public NoticeWithLinkScreen(Component var1, Component var2, URI var3, Runnable var4) {
      super(var1);
      this.message = var2;
      this.uri = var3;
      this.onClose = var4;
   }

   public static Screen createWorldSymlinkWarningScreen(Runnable var0) {
      return new NoticeWithLinkScreen(SYMLINK_WORLD_TITLE, SYMLINK_WORLD_MESSAGE_TEXT, CommonLinks.SYMLINK_HELP, var0);
   }

   public static Screen createPackSymlinkWarningScreen(Runnable var0) {
      return new NoticeWithLinkScreen(SYMLINK_PACK_TITLE, SYMLINK_PACK_MESSAGE_TEXT, CommonLinks.SYMLINK_HELP, var0);
   }

   protected void init() {
      super.init();
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      GridLayout.RowHelper var1 = this.layout.createRowHelper(1);
      var1.addChild(new StringWidget(this.title, this.font));
      var1.addChild((new MultiLineTextWidget(this.message, this.font)).setMaxWidth(this.width - 50).setCentered(true));
      boolean var2 = true;
      GridLayout var3 = (new GridLayout()).columnSpacing(5);
      GridLayout.RowHelper var4 = var3.createRowHelper(3);
      var4.addChild(Button.builder(CommonComponents.GUI_OPEN_IN_BROWSER, (var1x) -> {
         Util.getPlatform().openUri(this.uri);
      }).size(120, 20).build());
      var4.addChild(Button.builder(CommonComponents.GUI_COPY_LINK_TO_CLIPBOARD, (var1x) -> {
         this.minecraft.keyboardHandler.setClipboard(this.uri.toString());
      }).size(120, 20).build());
      var4.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).size(120, 20).build());
      var1.addChild(var3);
      this.repositionElements();
      this.layout.visitWidgets(this::addRenderableWidget);
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.message);
   }

   public void onClose() {
      this.onClose.run();
   }

   static {
      SYMLINK_WORLD_TITLE = Component.translatable("symlink_warning.title.world").withStyle(ChatFormatting.BOLD);
      SYMLINK_WORLD_MESSAGE_TEXT = Component.translatable("symlink_warning.message.world", Component.translationArg(CommonLinks.SYMLINK_HELP));
      SYMLINK_PACK_TITLE = Component.translatable("symlink_warning.title.pack").withStyle(ChatFormatting.BOLD);
      SYMLINK_PACK_MESSAGE_TEXT = Component.translatable("symlink_warning.message.pack", Component.translationArg(CommonLinks.SYMLINK_HELP));
   }
}
