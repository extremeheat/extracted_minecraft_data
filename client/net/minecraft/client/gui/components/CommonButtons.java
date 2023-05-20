package net.minecraft.client.gui.components;

import net.minecraft.network.chat.Component;

public class CommonButtons {
   public CommonButtons() {
      super();
   }

   public static TextAndImageButton languageTextAndImage(Button.OnPress var0) {
      return TextAndImageButton.builder(Component.translatable("options.language"), Button.WIDGETS_LOCATION, var0)
         .texStart(3, 109)
         .offset(65, 3)
         .yDiffTex(20)
         .usedTextureSize(14, 14)
         .textureSize(256, 256)
         .build();
   }

   public static TextAndImageButton accessibilityTextAndImage(Button.OnPress var0) {
      return TextAndImageButton.builder(Component.translatable("options.accessibility.title"), Button.ACCESSIBILITY_TEXTURE, var0)
         .texStart(3, 2)
         .offset(65, 2)
         .yDiffTex(20)
         .usedTextureSize(14, 16)
         .textureSize(32, 64)
         .build();
   }
}
