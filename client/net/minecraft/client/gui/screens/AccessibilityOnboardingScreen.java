package net.minecraft.client.gui.screens;

import com.mojang.text2speech.Narrator;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AccessibilityOnboardingScreen extends Screen {
   private static final Component TITLE = Component.translatable("accessibility.onboarding.screen.title");
   private static final Component ONBOARDING_NARRATOR_MESSAGE = Component.translatable("accessibility.onboarding.screen.narrator");
   private static final int PADDING = 4;
   private static final int TITLE_PADDING = 16;
   private static final float FADE_OUT_TIME = 1000.0F;
   private final LogoRenderer logoRenderer;
   private final Options options;
   private final boolean narratorAvailable;
   private boolean hasNarrated;
   private float timer;
   private final Runnable onClose;
   @Nullable
   private FocusableTextWidget textWidget;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, this.initTitleYPos(), 33);
   private float fadeInStart;
   private boolean fadingIn = true;
   private float fadeOutStart;

   public AccessibilityOnboardingScreen(Options var1, Runnable var2) {
      super(TITLE);
      this.options = var1;
      this.onClose = var2;
      this.logoRenderer = new LogoRenderer(true);
      this.narratorAvailable = Minecraft.getInstance().getNarrator().isActive();
   }

   public void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToContents(LinearLayout.vertical());
      var1.defaultCellSetting().alignHorizontallyCenter().padding(4);
      this.textWidget = (FocusableTextWidget)var1.addChild(new FocusableTextWidget(this.width, this.title, this.font), (Consumer)((var0) -> var0.padding(8)));
      AbstractWidget var3 = this.options.narrator().createButton(this.options);
      if (var3 instanceof CycleButton var2) {
         this.narratorButton = var2;
         this.narratorButton.active = this.narratorAvailable;
         var1.addChild(this.narratorButton);
      }

      var1.addChild(CommonButtons.accessibility(150, (var1x) -> this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), false));
      var1.addChild(CommonButtons.language(150, (var1x) -> this.closeAndSetScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), false));
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_CONTINUE, (var1x) -> this.onClose()).build());
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.textWidget != null) {
         this.textWidget.containWithin(this.width);
      }

      this.layout.arrangeElements();
   }

   protected void setInitialFocus() {
      if (this.narratorAvailable && this.narratorButton != null) {
         this.setInitialFocus(this.narratorButton);
      } else {
         super.setInitialFocus();
      }

   }

   private int initTitleYPos() {
      return 90;
   }

   public void onClose() {
      this.fadeOutStart = (float)Util.getMillis();
   }

   private void closeAndSetScreen(Screen var1) {
      this.close(false, () -> this.minecraft.setScreen(var1));
   }

   private void close(boolean var1, Runnable var2) {
      if (var1) {
         this.options.onboardingAccessibilityFinished();
      }

      Narrator.getNarrator().clear();
      var2.run();
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.handleInitialNarrationDelay();
      if (this.fadeInStart == 0.0F && this.fadingIn) {
         this.fadeInStart = (float)Util.getMillis();
      }

      if (this.fadeInStart > 0.0F) {
         float var5 = ((float)Util.getMillis() - this.fadeInStart) / 2000.0F;
         float var6 = 1.0F;
         if (var5 >= 1.0F) {
            this.fadingIn = false;
            this.fadeInStart = 0.0F;
         } else {
            var5 = Mth.clamp(var5, 0.0F, 1.0F);
            var6 = Mth.clampedMap(var5, 0.5F, 1.0F, 0.0F, 1.0F);
         }

         this.fadeWidgets(var6);
      }

      if (this.fadeOutStart > 0.0F) {
         float var8 = 1.0F - ((float)Util.getMillis() - this.fadeOutStart) / 1000.0F;
         float var10 = 0.0F;
         if (var8 <= 0.0F) {
            this.fadeOutStart = 0.0F;
            this.close(true, this.onClose);
         } else {
            var8 = Mth.clamp(var8, 0.0F, 1.0F);
            var10 = Mth.clampedMap(var8, 0.5F, 1.0F, 0.0F, 1.0F);
         }

         this.fadeWidgets(var10);
      }

      this.logoRenderer.renderLogo(var1, this.width, 1.0F);
   }

   protected void renderPanorama(GuiGraphics var1, float var2) {
      PANORAMA.render(var1, this.width, this.height, false);
   }

   private void handleInitialNarrationDelay() {
      if (!this.hasNarrated && this.narratorAvailable) {
         if (this.timer < 40.0F) {
            ++this.timer;
         } else if (this.minecraft.isWindowActive()) {
            Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true, 1.0F);
            this.hasNarrated = true;
         }
      }

   }
}
