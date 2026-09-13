package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.Blaze3D;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public class ConfirmLinkScreen extends ConfirmScreen {
   private static final Component WARNING_TEXT = Component.translatable("chat.link.warning").withColor(-13108);
   private static final int BUTTON_WIDTH = 100;
   private final URI url;
   private final boolean showWarning;

   public ConfirmLinkScreen(final BooleanConsumer callback, final URI url, final boolean trusted) {
      this(callback, confirmMessage(trusted), Component.literal(url.toString()), url, trusted ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, trusted);
   }

   public ConfirmLinkScreen(final BooleanConsumer callback, final Component title, final URI url, final boolean trusted) {
      this(callback, title, confirmMessage(trusted, url.toString()), url, trusted ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, trusted);
   }

   public ConfirmLinkScreen(final BooleanConsumer callback, final Component title, final Component message, final URI url, final Component noButtonComponent, final boolean trusted) {
      super(callback, title, message);
      this.yesButtonComponent = trusted ? CommonComponents.GUI_OPEN_IN_BROWSER : CommonComponents.GUI_YES;
      this.noButtonComponent = noButtonComponent;
      this.showWarning = !trusted;
      this.url = url;
   }

   protected static MutableComponent confirmMessage(final boolean trusted, final String url) {
      return confirmMessage(trusted).append(CommonComponents.SPACE).append((Component)Component.literal(url));
   }

   protected static MutableComponent confirmMessage(final boolean trusted) {
      return Component.translatable(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm");
   }

   protected void addAdditionalText() {
      if (this.showWarning) {
         this.layout.addChild(new StringWidget(WARNING_TEXT, this.font));
      }

   }

   protected void addButtons(final LinearLayout buttonLayout) {
      this.yesButton = (Button)buttonLayout.addChild(Button.builder(this.yesButtonComponent, (button) -> this.callback.accept(true)).width(100).build());
      buttonLayout.addChild(Button.builder(CommonComponents.GUI_COPY_TO_CLIPBOARD, (button) -> {
         this.copyToClipboard();
         this.callback.accept(false);
      }).width(100).build());
      this.noButton = (Button)buttonLayout.addChild(Button.builder(this.noButtonComponent, (button) -> this.callback.accept(false)).width(100).build());
   }

   public void copyToClipboard() {
      this.minecraft.keyboardHandler.setClipboard(this.url.toString());
   }

   public static void confirmLinkNow(final @Nullable Screen parentScreen, final URI uri, final boolean trusted) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.gui.setScreen(new ConfirmLinkScreen((shouldOpen) -> {
         if (shouldOpen) {
            Blaze3D.openUri(uri);
         }

         minecraft.gui.setScreen(parentScreen);
      }, uri, trusted));
   }

   public static void confirmLinkNow(final @Nullable Screen parentScreen, final URI uri) {
      confirmLinkNow(parentScreen, uri, true);
   }

   public static Button.OnPress confirmLink(final Screen parentScreen, final URI uri, final boolean trusted) {
      return (button) -> confirmLinkNow(parentScreen, uri, trusted);
   }

   public static Button.OnPress confirmLink(final Screen parentScreen, final URI uri) {
      return confirmLink(parentScreen, uri, true);
   }
}
