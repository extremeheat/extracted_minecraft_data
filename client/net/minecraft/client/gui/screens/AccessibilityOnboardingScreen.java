package net.minecraft.client.gui.screens;

import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
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
   private final Runnable onClose;
   @Nullable
   private FocusableTextWidget textWidget;

   public AccessibilityOnboardingScreen(Options var1, Runnable var2) {
      super(Component.translatable("accessibility.onboarding.screen.title"));
      this.options = var1;
      this.onClose = var2;
      this.logoRenderer = new LogoRenderer(true);
      this.narratorAvailable = Minecraft.getInstance().getNarrator().isActive();
   }

   @Override
   public void init() {
      int var1 = this.initTitleYPos();
      FrameLayout var2 = new FrameLayout(this.width, this.height - var1);
      var2.defaultChildLayoutSetting().alignVerticallyTop().padding(4);
      LinearLayout var3 = var2.addChild(LinearLayout.vertical());
      var3.defaultCellSetting().alignHorizontallyCenter().padding(2);
      this.textWidget = new FocusableTextWidget(this.width - 16, this.title, this.font);
      var3.addChild(this.textWidget, var0 -> var0.paddingBottom(16));
      AbstractWidget var4 = this.options.narrator().createButton(this.options, 0, 0, 150);
      var4.active = this.narratorAvailable;
      var3.addChild(var4);
      if (this.narratorAvailable) {
         this.setInitialFocus(var4);
      }

      var3.addChild(CommonButtons.accessibility(150, var1x -> this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), false));
      var3.addChild(
         CommonButtons.language(
            150, var1x -> this.closeAndSetScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), false
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
      this.close(this.onClose);
   }

   private void closeAndSetScreen(Screen var1) {
      this.close(() -> this.minecraft.setScreen(var1));
   }

   private void close(Runnable var1) {
      this.options.onboardAccessibility = false;
      this.options.save();
      Narrator.getNarrator().clear();
      var1.run();
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.handleInitialNarrationDelay();
      this.logoRenderer.renderLogo(var1, this.width, 1.0F);
      if (this.textWidget != null) {
         this.textWidget.render(var1, var2, var3, var4);
      }
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.panorama.render(0.0F, 1.0F);
      var1.fill(0, 0, this.width, this.height, -1877995504);
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
