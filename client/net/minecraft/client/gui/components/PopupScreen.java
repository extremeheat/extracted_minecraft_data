package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PopupScreen extends Screen {
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("popup/background");
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
   private final List<ButtonOption> buttons;
   @Nullable
   private final Runnable onClose;
   private final int contentWidth;
   private final LinearLayout layout = LinearLayout.vertical();

   PopupScreen(Screen var1, int var2, @Nullable ResourceLocation var3, Component var4, Component var5, List<ButtonOption> var6, @Nullable Runnable var7) {
      super(var4);
      this.backgroundScreen = var1;
      this.image = var3;
      this.message = var5;
      this.buttons = var6;
      this.onClose = var7;
      this.contentWidth = var2 - 36;
   }

   public void added() {
      super.added();
      this.backgroundScreen.clearFocus();
   }

   protected void init() {
      this.backgroundScreen.init(this.minecraft, this.width, this.height);
      this.layout.spacing(12).defaultCellSetting().alignHorizontallyCenter();
      this.layout.addChild((new MultiLineTextWidget(this.title.copy().withStyle(ChatFormatting.BOLD), this.font)).setMaxWidth(this.contentWidth).setCentered(true));
      if (this.image != null) {
         this.layout.addChild(ImageWidget.texture(130, 64, this.image, 130, 64));
      }

      this.layout.addChild((new MultiLineTextWidget(this.message, this.font)).setMaxWidth(this.contentWidth).setCentered(true));
      this.layout.addChild(this.buildButtonRow());
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   private LinearLayout buildButtonRow() {
      int var1 = 6 * (this.buttons.size() - 1);
      int var2 = Math.min((this.contentWidth - var1) / this.buttons.size(), 150);
      LinearLayout var3 = LinearLayout.horizontal();
      var3.spacing(6);
      Iterator var4 = this.buttons.iterator();

      while(var4.hasNext()) {
         ButtonOption var5 = (ButtonOption)var4.next();
         var3.addChild(Button.builder(var5.message(), (var2x) -> {
            var5.action().accept(this);
         }).width(var2).build());
      }

      return var3;
   }

   protected void repositionElements() {
      this.backgroundScreen.resize(this.minecraft, this.width, this.height);
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.backgroundScreen.render(var1, -1, -1, var4);
      var1.flush();
      RenderSystem.clear(256, Minecraft.ON_OSX);
      this.renderTransparentBackground(var1);
      var1.blitSprite(BACKGROUND_SPRITE, this.layout.getX() - 18, this.layout.getY() - 18, this.layout.getWidth() + 36, this.layout.getHeight() + 36);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.title, this.message);
   }

   public void onClose() {
      if (this.onClose != null) {
         this.onClose.run();
      }

      this.minecraft.setScreen(this.backgroundScreen);
   }

   static record ButtonOption(Component message, Consumer<PopupScreen> action) {
      ButtonOption(Component var1, Consumer<PopupScreen> var2) {
         super();
         this.message = var1;
         this.action = var2;
      }

      public Component message() {
         return this.message;
      }

      public Consumer<PopupScreen> action() {
         return this.action;
      }
   }

   public static class Builder {
      private final Screen backgroundScreen;
      private final Component title;
      private Component message;
      private int width;
      @Nullable
      private ResourceLocation image;
      private final List<ButtonOption> buttons;
      @Nullable
      private Runnable onClose;

      public Builder(Screen var1, Component var2) {
         super();
         this.message = CommonComponents.EMPTY;
         this.width = 250;
         this.buttons = new ArrayList();
         this.onClose = null;
         this.backgroundScreen = var1;
         this.title = var2;
      }

      public Builder setWidth(int var1) {
         this.width = var1;
         return this;
      }

      public Builder setImage(ResourceLocation var1) {
         this.image = var1;
         return this;
      }

      public Builder setMessage(Component var1) {
         this.message = var1;
         return this;
      }

      public Builder addButton(Component var1, Consumer<PopupScreen> var2) {
         this.buttons.add(new ButtonOption(var1, var2));
         return this;
      }

      public Builder onClose(Runnable var1) {
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
}
