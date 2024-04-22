package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AccessibilityOptionsScreen extends SimpleOptionsSubScreen {
   public static final Component TITLE = Component.translatable("options.accessibility.title");

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{
         var0.narrator(),
         var0.showSubtitles(),
         var0.highContrast(),
         var0.autoJump(),
         var0.menuBackgroundBlurriness(),
         var0.textBackgroundOpacity(),
         var0.backgroundForChatOnly(),
         var0.chatOpacity(),
         var0.chatLineSpacing(),
         var0.chatDelay(),
         var0.notificationDisplayTime(),
         var0.toggleCrouch(),
         var0.toggleSprint(),
         var0.screenEffectScale(),
         var0.fovEffectScale(),
         var0.darknessEffectScale(),
         var0.damageTiltStrength(),
         var0.glintSpeed(),
         var0.glintStrength(),
         var0.hideLightningFlash(),
         var0.darkMojangStudiosBackground(),
         var0.panoramaSpeed(),
         var0.hideSplashTexts(),
         var0.narratorHotkey()
      };
   }

   public AccessibilityOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE, options(var2));
   }

   @Override
   protected void init() {
      super.init();
      AbstractWidget var1 = this.list.findOption(this.options.highContrast());
      if (var1 != null && !this.minecraft.getResourcePackRepository().getAvailableIds().contains("high_contrast")) {
         var1.active = false;
         var1.setTooltip(Tooltip.create(Component.translatable("options.accessibility.high_contrast.error.tooltip")));
      }
   }

   @Override
   protected void addFooter() {
      LinearLayout var1 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var1.addChild(
         Button.builder(Component.translatable("options.accessibility.link"), ConfirmLinkScreen.confirmLink(this, "https://aka.ms/MinecraftJavaAccessibility"))
            .build()
      );
      var1.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> this.minecraft.setScreen(this.lastScreen)).build());
   }
}