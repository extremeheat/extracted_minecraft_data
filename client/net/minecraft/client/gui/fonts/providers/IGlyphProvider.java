package net.minecraft.client.gui.fonts.providers;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;

public interface IGlyphProvider extends Closeable {
   default void close() {
   }

   @Nullable
   default IGlyphInfo func_212248_a(char var1) {
      return null;
   }
}
