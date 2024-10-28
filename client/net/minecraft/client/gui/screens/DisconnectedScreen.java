package net.minecraft.client.gui.screens;

import java.net.URI;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DisconnectedScreen extends Screen {
   private static final Component TO_SERVER_LIST = Component.translatable("gui.toMenu");
   private static final Component TO_TITLE = Component.translatable("gui.toTitle");
   private static final Component REPORT_TO_SERVER_TITLE = Component.translatable("gui.report_to_server");
   private static final Component OPEN_REPORT_DIR_TITLE = Component.translatable("gui.open_report_dir");
   private final Screen parent;
   private final DisconnectionDetails details;
   private final Component buttonText;
   private final LinearLayout layout;

   public DisconnectedScreen(Screen var1, Component var2, Component var3) {
      this(var1, var2, new DisconnectionDetails(var3));
   }

   public DisconnectedScreen(Screen var1, Component var2, Component var3, Component var4) {
      this(var1, var2, new DisconnectionDetails(var3), var4);
   }

   public DisconnectedScreen(Screen var1, Component var2, DisconnectionDetails var3) {
      this(var1, var2, var3, TO_SERVER_LIST);
   }

   public DisconnectedScreen(Screen var1, Component var2, DisconnectionDetails var3, Component var4) {
      super(var2);
      this.layout = LinearLayout.vertical();
      this.parent = var1;
      this.details = var3;
      this.buttonText = var4;
   }

   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter().padding(10);
      this.layout.addChild(new StringWidget(this.title, this.font));
      this.layout.addChild((new MultiLineTextWidget(this.details.reason(), this.font)).setMaxWidth(this.width - 50).setCentered(true));
      this.layout.defaultCellSetting().padding(2);
      this.details.bugReportLink().ifPresent((var1x) -> {
         this.layout.addChild(Button.builder(REPORT_TO_SERVER_TITLE, ConfirmLinkScreen.confirmLink(this, (URI)var1x, false)).width(200).build());
      });
      this.details.report().ifPresent((var1x) -> {
         this.layout.addChild(Button.builder(OPEN_REPORT_DIR_TITLE, (var1) -> {
            Util.getPlatform().openPath(var1x.getParent());
         }).width(200).build());
      });
      Button var1;
      if (this.minecraft.allowsMultiplayer()) {
         var1 = Button.builder(this.buttonText, (var1x) -> {
            this.minecraft.setScreen(this.parent);
         }).width(200).build();
      } else {
         var1 = Button.builder(TO_TITLE, (var1x) -> {
            this.minecraft.setScreen(new TitleScreen());
         }).width(200).build();
      }

      this.layout.addChild(var1);
      this.layout.arrangeElements();
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.title, this.details.reason());
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
