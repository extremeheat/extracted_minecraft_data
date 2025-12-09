package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;

public class ConfirmLinkScreen extends ConfirmScreen {
   private static final Component WARNING_TEXT = Component.translatable("chat.link.warning").withColor(-13108);
   private static final int BUTTON_WIDTH = 100;
   private final String url;
   private final boolean showWarning;

   public ConfirmLinkScreen(BooleanConsumer var1, String var2, boolean var3) {
      this(var1, confirmMessage(var3), Component.literal(var2), (String)var2, var3 ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, var3);
   }

   public ConfirmLinkScreen(BooleanConsumer var1, Component var2, String var3, boolean var4) {
      this(var1, var2, confirmMessage(var4, var3), (String)var3, var4 ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, var4);
   }

   public ConfirmLinkScreen(BooleanConsumer var1, Component var2, URI var3, boolean var4) {
      this(var1, var2, var3.toString(), var4);
   }

   public ConfirmLinkScreen(BooleanConsumer var1, Component var2, Component var3, URI var4, Component var5, boolean var6) {
      this(var1, var2, var3, var4.toString(), var5, true);
   }

   public ConfirmLinkScreen(BooleanConsumer var1, Component var2, Component var3, String var4, Component var5, boolean var6) {
      super(var1, var2, var3);
      this.yesButtonComponent = var6 ? CommonComponents.GUI_OPEN_IN_BROWSER : CommonComponents.GUI_YES;
      this.noButtonComponent = var5;
      this.showWarning = !var6;
      this.url = var4;
   }

   protected static MutableComponent confirmMessage(boolean var0, String var1) {
      return confirmMessage(var0).append(CommonComponents.SPACE).append((Component)Component.literal(var1));
   }

   protected static MutableComponent confirmMessage(boolean var0) {
      return Component.translatable(var0 ? "chat.link.confirmTrusted" : "chat.link.confirm");
   }

   protected void addAdditionalText() {
      if (this.showWarning) {
         this.layout.addChild(new StringWidget(WARNING_TEXT, this.font));
      }

   }

   protected void addButtons(LinearLayout var1) {
      this.yesButton = (Button)var1.addChild(Button.builder(this.yesButtonComponent, (var1x) -> this.callback.accept(true)).width(100).build());
      var1.addChild(Button.builder(CommonComponents.GUI_COPY_TO_CLIPBOARD, (var1x) -> {
         this.copyToClipboard();
         this.callback.accept(false);
      }).width(100).build());
      this.noButton = (Button)var1.addChild(Button.builder(this.noButtonComponent, (var1x) -> this.callback.accept(false)).width(100).build());
   }

   public void copyToClipboard() {
      this.minecraft.keyboardHandler.setClipboard(this.url);
   }

   public static void confirmLinkNow(Screen var0, String var1, boolean var2) {
      Minecraft var3 = Minecraft.getInstance();
      var3.setScreen(new ConfirmLinkScreen((var3x) -> {
         if (var3x) {
            Util.getPlatform().openUri(var1);
         }

         var3.setScreen(var0);
      }, var1, var2));
   }

   public static void confirmLinkNow(Screen var0, URI var1, boolean var2) {
      Minecraft var3 = Minecraft.getInstance();
      var3.setScreen(new ConfirmLinkScreen((var3x) -> {
         if (var3x) {
            Util.getPlatform().openUri(var1);
         }

         var3.setScreen(var0);
      }, var1.toString(), var2));
   }

   public static void confirmLinkNow(Screen var0, URI var1) {
      confirmLinkNow(var0, var1, true);
   }

   public static void confirmLinkNow(Screen var0, String var1) {
      confirmLinkNow(var0, var1, true);
   }

   public static Button.OnPress confirmLink(Screen var0, String var1, boolean var2) {
      return (var3) -> confirmLinkNow(var0, var1, var2);
   }

   public static Button.OnPress confirmLink(Screen var0, URI var1, boolean var2) {
      return (var3) -> confirmLinkNow(var0, var1, var2);
   }

   public static Button.OnPress confirmLink(Screen var0, String var1) {
      return confirmLink(var0, var1, true);
   }

   public static Button.OnPress confirmLink(Screen var0, URI var1) {
      return confirmLink(var0, var1, true);
   }
}
