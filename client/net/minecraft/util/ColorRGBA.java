package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;

public record ColorRGBA(int rgba) {
   private static final String CUSTOM_COLOR_PREFIX = "#";
   public static final Codec<ColorRGBA> CODEC;

   public ColorRGBA(int var1) {
      super();
      this.rgba = var1;
   }

   private String formatValue() {
      return String.format(Locale.ROOT, "#%08X", this.rgba);
   }

   public String toString() {
      return this.formatValue();
   }

   public int rgba() {
      return this.rgba;
   }

   static {
      CODEC = Codec.STRING.comapFlatMap((var0) -> {
         if (!var0.startsWith("#")) {
            return DataResult.error(() -> {
               return "Not a color code: " + var0;
            });
         } else {
            try {
               int var1 = (int)Long.parseLong(var0.substring(1), 16);
               return DataResult.success(new ColorRGBA(var1));
            } catch (NumberFormatException var2) {
               return DataResult.error(() -> {
                  return "Exception parsing color code: " + var2.getMessage();
               });
            }
         }
      }, ColorRGBA::formatValue);
   }
}
