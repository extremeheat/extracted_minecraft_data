package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import java.net.URI;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.CommonLinks;

public class AddRealmPopupScreen extends RealmsScreen {
   private static final Component POPUP_TEXT = Component.translatable("mco.selectServer.popup");
   private static final Component CLOSE_TEXT = Component.translatable("mco.selectServer.close");
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("popup/background");
   private static final ResourceLocation TRIAL_AVAILABLE_SPRITE = ResourceLocation.withDefaultNamespace("icon/trial_available");
   private static final WidgetSprites CROSS_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/cross_button"), ResourceLocation.withDefaultNamespace("widget/cross_button_highlighted"));
   private static final int BG_TEXTURE_WIDTH = 236;
   private static final int BG_TEXTURE_HEIGHT = 34;
   private static final int BG_BORDER_SIZE = 6;
   private static final int IMAGE_WIDTH = 195;
   private static final int IMAGE_HEIGHT = 152;
   private static final int BUTTON_SPACING = 4;
   private static final int PADDING = 10;
   private static final int WIDTH = 320;
   private static final int HEIGHT = 172;
   private static final int TEXT_WIDTH = 100;
   private static final int BUTTON_WIDTH = 99;
   private static final int CAROUSEL_SWITCH_INTERVAL = 100;
   private static List<ResourceLocation> carouselImages = List.of();
   private final Screen backgroundScreen;
   private final boolean trialAvailable;
   @Nullable
   private Button createTrialButton;
   private int carouselIndex;
   private int carouselTick;

   public AddRealmPopupScreen(Screen var1, boolean var2) {
      super(POPUP_TEXT);
      this.backgroundScreen = var1;
      this.trialAvailable = var2;
   }

   public static void updateCarouselImages(ResourceManager var0) {
      Set var1 = var0.listResources("textures/gui/images", (var0x) -> {
         return var0x.getPath().endsWith(".png");
      }).keySet();
      carouselImages = var1.stream().filter((var0x) -> {
         return var0x.getNamespace().equals("realms");
      }).toList();
   }

   protected void init() {
      this.backgroundScreen.resize(this.minecraft, this.width, this.height);
      if (this.trialAvailable) {
         this.createTrialButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.trial"), ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.START_REALMS_TRIAL)).bounds(this.right() - 10 - 99, this.bottom() - 10 - 4 - 40, 99, 20).build());
      }

      this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.buy"), ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.BUY_REALMS)).bounds(this.right() - 10 - 99, this.bottom() - 10 - 20, 99, 20).build());
      ImageButton var1 = (ImageButton)this.addRenderableWidget(new ImageButton(this.left() + 4, this.top() + 4, 14, 14, CROSS_BUTTON_SPRITES, (var1x) -> {
         this.onClose();
      }, CLOSE_TEXT));
      var1.setTooltip(Tooltip.create(CLOSE_TEXT));
      int var2 = 142 - (this.trialAvailable ? 40 : 20);
      FittingMultiLineTextWidget var3 = new FittingMultiLineTextWidget(this.right() - 10 - 100, this.top() + 10, 100, var2, POPUP_TEXT, this.font);
      if (var3.showingScrollBar()) {
         var3.setWidth(100 - var3.scrollbarWidth());
      }

      this.addRenderableWidget(var3);
   }

   public void tick() {
      super.tick();
      if (++this.carouselTick > 100) {
         this.carouselTick = 0;
         this.carouselIndex = (this.carouselIndex + 1) % carouselImages.size();
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.createTrialButton != null) {
         renderDiamond(var1, this.createTrialButton);
      }

   }

   public static void renderDiamond(GuiGraphics var0, Button var1) {
      boolean var2 = true;
      var0.pose().pushPose();
      var0.pose().translate(0.0F, 0.0F, 110.0F);
      var0.blitSprite(TRIAL_AVAILABLE_SPRITE, var1.getX() + var1.getWidth() - 8 - 4, var1.getY() + var1.getHeight() / 2 - 4, 8, 8);
      var0.pose().popPose();
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.backgroundScreen.render(var1, -1, -1, var4);
      var1.flush();
      RenderSystem.clear(256, Minecraft.ON_OSX);
      this.clearTooltipForNextRenderPass();
      this.renderTransparentBackground(var1);
      var1.blitSprite(BACKGROUND_SPRITE, this.left(), this.top(), 320, 172);
      if (!carouselImages.isEmpty()) {
         var1.blit((ResourceLocation)carouselImages.get(this.carouselIndex), this.left() + 10, this.top() + 10, 0, 0.0F, 0.0F, 195, 152, 195, 152);
      }

   }

   private int left() {
      return (this.width - 320) / 2;
   }

   private int top() {
      return (this.height - 172) / 2;
   }

   private int right() {
      return this.left() + 320;
   }

   private int bottom() {
      return this.top() + 172;
   }

   public void onClose() {
      this.minecraft.setScreen(this.backgroundScreen);
   }
}
