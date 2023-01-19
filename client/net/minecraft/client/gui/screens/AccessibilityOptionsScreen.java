package net.minecraft.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AccessibilityOptionsScreen extends SimpleOptionsSubScreen {
   private static final String GUIDE_LINK = "https://aka.ms/MinecraftJavaAccessibility";

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{
         var0.narrator(),
         var0.showSubtitles(),
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
         var0.hideLightningFlash(),
         var0.autoJump(),
         var0.panoramaSpeed(),
         var0.darkMojangStudiosBackground()
      };
   }

   public AccessibilityOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.accessibility.title"), options(var2));
   }

   @Override
   protected void createFooter() {
      this.addRenderableWidget(
         Button.builder(Component.translatable("options.accessibility.link"), var1 -> this.minecraft.setScreen(new ConfirmLinkScreen(var1x -> {
               if (var1x) {
                  Util.getPlatform().openUri("https://aka.ms/MinecraftJavaAccessibility");
               }
   
               this.minecraft.setScreen(this);
            }, "https://aka.ms/MinecraftJavaAccessibility", true))).bounds(this.width / 2 - 155, this.height - 27, 150, 20).build()
      );
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, var1 -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 + 5, this.height - 27, 150, 20)
            .build()
      );
   }
}
