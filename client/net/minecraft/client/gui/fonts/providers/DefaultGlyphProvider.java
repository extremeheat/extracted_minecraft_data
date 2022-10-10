package net.minecraft.client.gui.fonts.providers;

import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.DefaultGlyph;
import net.minecraft.client.gui.fonts.IGlyphInfo;

public class DefaultGlyphProvider implements IGlyphProvider {
   public DefaultGlyphProvider() {
      super();
   }

   @Nullable
   public IGlyphInfo func_212248_a(char var1) {
      return DefaultGlyph.INSTANCE;
   }
}
