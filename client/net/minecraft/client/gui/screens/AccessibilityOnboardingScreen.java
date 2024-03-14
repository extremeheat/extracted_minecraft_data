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
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AccessibilityOnboardingScreen extends Screen {
   private static final Component TITLE = Component.translatable("accessibility.onboarding.screen.title");
   private static final Component ONBOARDING_NARRATOR_MESSAGE = Component.translatable("accessibility.onboarding.screen.narrator");
   private static final int PADDING = 4;
   private static final int TITLE_PADDING = 16;
   private final LogoRenderer logoRenderer;
   private final Options options;
   private final boolean narratorAvailable;
   private boolean hasNarrated;
   private float timer;
   private final Runnable onClose;
   @Nullable
   private FocusableTextWidget textWidget;
   @Nullable
   private AbstractWidget narrationButton;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, this.initTitleYPos(), 33);

   public AccessibilityOnboardingScreen(Options var1, Runnable var2) {
      super(TITLE);
      this.options = var1;
      this.onClose = var2;
      this.logoRenderer = new LogoRenderer(true);
      this.narratorAvailable = Minecraft.getInstance().getNarrator().isActive();
   }

   @Override
   public void init() {
      LinearLayout var1 = this.layout.addToContents(LinearLayout.vertical());
      var1.defaultCellSetting().alignHorizontallyCenter().padding(4);
      this.textWidget = var1.addChild(new FocusableTextWidget(this.width, this.title, this.font), var0 -> var0.padding(8));
      this.narrationButton = this.options.narrator().createButton(this.options);
      this.narrationButton.active = this.narratorAvailable;
      var1.addChild(this.narrationButton);
      var1.addChild(CommonButtons.accessibility(150, var1x -> this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), false));
      var1.addChild(
         CommonButtons.language(
            150, var1x -> this.closeAndSetScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), false
         )
      );
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_CONTINUE, var1x -> this.onClose()).build());
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      if (this.textWidget != null) {
         this.textWidget.containWithin(this.width);
      }

      this.layout.arrangeElements();
   }

   @Override
   protected void setInitialFocus() {
      if (this.narratorAvailable && this.narrationButton != null) {
         this.setInitialFocus(this.narrationButton);
      } else {
         super.setInitialFocus();
      }
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
   }

   @Override
   protected void renderPanorama(GuiGraphics var1, float var2) {
      PANORAMA.render(var1, this.width, this.height, 1.0F, 0.0F);
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
