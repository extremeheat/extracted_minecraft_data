package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

public class FontOptionsScreen extends SimpleOptionsSubScreen {
   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{var0.forceUnicodeFont(), var0.japaneseGlyphVariants()};
   }

   public FontOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.font.title"), options(var2));
   }
}
