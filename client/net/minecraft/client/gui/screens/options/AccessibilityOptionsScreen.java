package net.minecraft.client.gui.screens.options;

import java.net.URI;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

public class AccessibilityOptionsScreen extends OptionsSubScreen {
   public static final Component TITLE = Component.translatable("options.accessibility.title");

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{var0.narrator(), var0.showSubtitles(), var0.highContrast(), var0.autoJump(), var0.menuBackgroundBlurriness(), var0.textBackgroundOpacity(), var0.backgroundForChatOnly(), var0.chatOpacity(), var0.chatLineSpacing(), var0.chatDelay(), var0.notificationDisplayTime(), var0.bobView(), var0.toggleCrouch(), var0.toggleSprint(), var0.screenEffectScale(), var0.fovEffectScale(), var0.darknessEffectScale(), var0.damageTiltStrength(), var0.glintSpeed(), var0.glintStrength(), var0.hideLightningFlash(), var0.darkMojangStudiosBackground(), var0.panoramaSpeed(), var0.hideSplashTexts(), var0.narratorHotkey()};
   }

   public AccessibilityOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   protected void init() {
      super.init();
      AbstractWidget var1 = this.list.findOption(this.options.highContrast());
      if (var1 != null && !this.minecraft.getResourcePackRepository().getAvailableIds().contains("high_contrast")) {
         var1.active = false;
         var1.setTooltip(Tooltip.create(Component.translatable("options.accessibility.high_contrast.error.tooltip")));
      }

   }

   protected void addOptions() {
      this.list.addSmall(options(this.options));
   }

   protected void addFooter() {
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var1.addChild(Button.builder(Component.translatable("options.accessibility.link"), ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.ACCESSIBILITY_HELP)).build());
      var1.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).build());
   }
}
