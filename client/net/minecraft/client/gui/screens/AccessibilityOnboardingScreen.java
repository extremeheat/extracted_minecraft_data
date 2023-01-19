package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AccessibilityOnboardingTextWidget;
import net.minecraft.client.gui.components.Button;
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
   private boolean hasNarrated;
   private float timer;
   @Nullable
   private AccessibilityOnboardingTextWidget textWidget;

   public AccessibilityOnboardingScreen(Options var1) {
      super(Component.translatable("accessibility.onboarding.screen.title"));
      this.options = var1;
      this.logoRenderer = new LogoRenderer(true);
   }

   @Override
   public void init() {
      FrameLayout var1 = new FrameLayout();
      var1.defaultChildLayoutSetting().alignVerticallyTop().padding(4);
      var1.setMinDimensions(this.width, this.height - this.initTitleYPos());
      GridLayout var2 = var1.addChild(new GridLayout());
      var2.defaultCellSetting().alignHorizontallyCenter().padding(4);
      GridLayout.RowHelper var3 = var2.createRowHelper(1);
      this.textWidget = new AccessibilityOnboardingTextWidget(this.font, this.title, this.width);
      var3.addChild(this.textWidget, var3.newCellSettings().padding(16));
      AbstractWidget var4 = this.options.narrator().createButton(this.options, 0, 0, 150);
      var3.addChild(var4);
      this.setInitialFocus(var4);
      var3.addChild(
         Button.builder(
               Component.translatable("options.accessibility.title"),
               var1x -> this.minecraft.setScreen(new AccessibilityOptionsScreen(new TitleScreen(true), this.minecraft.options))
            )
            .build()
      );
      var1.addChild(
         Button.builder(CommonComponents.GUI_CONTINUE, var1x -> this.minecraft.setScreen(new TitleScreen(true, this.logoRenderer))).build(),
         var1.newChildLayoutSettings().alignVerticallyBottom().padding(8)
      );
      var1.arrangeElements();
      FrameLayout.alignInRectangle(var1, 0, this.initTitleYPos(), this.width, this.height, 0.5F, 0.0F);
      var1.visitWidgets(this::addRenderableWidget);
   }

   private int initTitleYPos() {
      return 90;
   }

   @Override
   public void onClose() {
      this.minecraft.getNarrator().clear();
      this.minecraft.setScreen(new TitleScreen(true, this.logoRenderer));
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
      if (!this.hasNarrated) {
         if (this.timer < 40.0F) {
            ++this.timer;
         } else {
            Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true);
            this.hasNarrated = true;
         }
      }
   }
}
