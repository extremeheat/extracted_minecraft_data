package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AccessibilityOnboardingTextWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AccessibilityOnboardingScreen extends Screen {
   private static final Component ONBOARDING_NARRATOR_MESSAGE = Component.translatable("accessibility.onboarding.screen.narrator");
   private static final int PADDING = 4;
   private static final int TITLE_PADDING = 16;
   private final PanoramaRenderer panorama = new PanoramaRenderer(TitleScreen.CUBE_MAP);
   private final LogoRenderer logoRenderer;
   private final Options options;
   private final boolean narratorAvailable;
   private boolean hasNarrated;
   private float timer;
   @Nullable
   private AccessibilityOnboardingTextWidget textWidget;

   public AccessibilityOnboardingScreen(Options var1) {
      super(Component.translatable("accessibility.onboarding.screen.title"));
      this.options = var1;
      this.logoRenderer = new LogoRenderer(true);
      this.narratorAvailable = Minecraft.getInstance().getNarrator().isActive();
   }

   @Override
   public void init() {
      int var1 = this.initTitleYPos();
      FrameLayout var2 = new FrameLayout(this.width, this.height - var1);
      var2.defaultChildLayoutSetting().alignVerticallyTop().padding(4);
      GridLayout var3 = var2.addChild(new GridLayout());
      var3.defaultCellSetting().alignHorizontallyCenter().padding(4);
      GridLayout.RowHelper var4 = var3.createRowHelper(1);
      var4.defaultCellSetting().padding(2);
      this.textWidget = new AccessibilityOnboardingTextWidget(this.font, this.title, this.width);
      var4.addChild(this.textWidget, var4.newCellSettings().paddingBottom(16));
      AbstractWidget var5 = this.options.narrator().createButton(this.options, 0, 0, 150);
      var5.active = this.narratorAvailable;
      var4.addChild(var5);
      if (this.narratorAvailable) {
         this.setInitialFocus(var5);
      }

      var4.addChild(CommonButtons.accessibilityTextAndImage(var1x -> this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options))));
      var4.addChild(
         CommonButtons.languageTextAndImage(
            var1x -> this.closeAndSetScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()))
         )
      );
      var2.addChild(
         Button.builder(CommonComponents.GUI_CONTINUE, var1x -> this.onClose()).build(), var2.newChildLayoutSettings().alignVerticallyBottom().padding(8)
      );
      var2.arrangeElements();
      FrameLayout.alignInRectangle(var2, 0, var1, this.width, this.height, 0.5F, 0.0F);
      var2.visitWidgets(this::addRenderableWidget);
   }

   private int initTitleYPos() {
      return 90;
   }

   @Override
   public void onClose() {
      this.closeAndSetScreen(new TitleScreen(true, this.logoRenderer));
   }

   private void closeAndSetScreen(Screen var1) {
      this.options.onboardAccessibility = false;
      this.options.save();
      Narrator.getNarrator().clear();
      this.minecraft.setScreen(var1);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.handleInitialNarrationDelay();
      this.panorama.render(0.0F, 1.0F);
      fill(var1, 0, 0, this.width, this.height, -1877995504);
      this.logoRenderer.renderLogo(var1, this.width, 1.0F);
      if (this.textWidget != null) {
         this.textWidget.render(var1, var2, var3, var4);
      }

      super.render(var1, var2, var3, var4);
   }

   private void handleInitialNarrationDelay() {
      if (!this.hasNarrated && this.narratorAvailable) {
         if (this.timer < 40.0F) {
            ++this.timer;
         } else if (this.minecraft.isWindowActive()) {
            Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true);
            this.hasNarrated = true;
         }
      }
   }
}
