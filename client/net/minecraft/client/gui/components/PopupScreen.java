package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PopupScreen extends Screen {
   private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("popup/background");
   private static final int SPACING = 12;
   private static final int BG_BORDER_WITH_SPACING = 18;
   private static final int BUTTON_SPACING = 6;
   private static final int IMAGE_SIZE_X = 130;
   private static final int IMAGE_SIZE_Y = 64;
   private static final int POPUP_DEFAULT_WIDTH = 250;
   private final Screen backgroundScreen;
   @Nullable
   private final ResourceLocation image;
   private final Component message;
   private final List<PopupScreen.ButtonOption> buttons;
   @Nullable
   private final Runnable onClose;
   private final int contentWidth;
   private final LinearLayout layout = LinearLayout.vertical();

   PopupScreen(
      Screen var1, int var2, @Nullable ResourceLocation var3, Component var4, Component var5, List<PopupScreen.ButtonOption> var6, @Nullable Runnable var7
   ) {
      super(var4);
      this.backgroundScreen = var1;
      this.image = var3;
      this.message = var5;
      this.buttons = var6;
      this.onClose = var7;
      this.contentWidth = var2 - 36;
   }

   @Override
   public void added() {
      super.added();
      this.backgroundScreen.clearFocus();
   }

   @Override
   protected void init() {
      this.layout.spacing(12).defaultCellSetting().alignHorizontallyCenter();
      this.layout
         .addChild(new MultiLineTextWidget(this.title.copy().withStyle(ChatFormatting.BOLD), this.font).setMaxWidth(this.contentWidth).setCentered(true));
      if (this.image != null) {
         this.layout.addChild(ImageWidget.texture(130, 64, this.image, 130, 64));
      }

      this.layout.addChild(new MultiLineTextWidget(this.message, this.font).setMaxWidth(this.contentWidth).setCentered(true));
      this.layout.addChild(this.buildButtonRow());
      this.layout.visitWidgets(var1 -> {
      });
      this.repositionElements();
   }

   private LinearLayout buildButtonRow() {
      int var1 = 6 * (this.buttons.size() - 1);
      int var2 = Math.min((this.contentWidth - var1) / this.buttons.size(), 150);
      LinearLayout var3 = LinearLayout.horizontal();
      var3.spacing(6);

      for(PopupScreen.ButtonOption var5 : this.buttons) {
         var3.addChild(Button.builder(var5.message(), var2x -> var5.action().accept(this)).width(var2).build());
      }

      return var3;
   }

   @Override
   protected void repositionElements() {
      this.backgroundScreen.resize(this.minecraft, this.width, this.height);
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.backgroundScreen.render(var1, -1, -1, var4);
      var1.flush();
      RenderSystem.clear(256, Minecraft.ON_OSX);
      this.renderTransparentBackground(var1);
      var1.blitSprite(BACKGROUND_SPRITE, this.layout.getX() - 18, this.layout.getY() - 18, this.layout.getWidth() + 36, this.layout.getHeight() + 36);
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.title, this.message);
   }

   @Override
   public void onClose() {
      if (this.onClose != null) {
         this.onClose.run();
      }

      this.minecraft.setScreen(this.backgroundScreen);
   }

   public static class Builder {
      private final Screen backgroundScreen;
      private final Component title;
      private Component message = CommonComponents.EMPTY;
      private int width = 250;
      @Nullable
      private ResourceLocation image;
      private final List<PopupScreen.ButtonOption> buttons = new ArrayList<>();
      @Nullable
      private Runnable onClose = null;

      public Builder(Screen var1, Component var2) {
         super();
         this.backgroundScreen = var1;
         this.title = var2;
      }

      public PopupScreen.Builder setWidth(int var1) {
         this.width = var1;
         return this;
      }

      public PopupScreen.Builder setImage(ResourceLocation var1) {
         this.image = var1;
         return this;
      }

      public PopupScreen.Builder setMessage(Component var1) {
         this.message = var1;
         return this;
      }

      public PopupScreen.Builder addButton(Component var1, Consumer<PopupScreen> var2) {
         this.buttons.add(new PopupScreen.ButtonOption(var1, var2));
         return this;
      }

      public PopupScreen.Builder onClose(Runnable var1) {
         this.onClose = var1;
         return this;
      }

      public PopupScreen build() {
         if (this.buttons.isEmpty()) {
            throw new IllegalStateException("Popup must have at least one button");
         } else {
            return new PopupScreen(this.backgroundScreen, this.width, this.image, this.title, this.message, List.copyOf(this.buttons), this.onClose);
         }
      }
   }

   static record ButtonOption(Component a, Consumer<PopupScreen> b) {
      private final Component message;
      private final Consumer<PopupScreen> action;

      ButtonOption(Component var1, Consumer<PopupScreen> var2) {
         super();
         this.message = var1;
         this.action = var2;
      }
   }
}
