package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.HexFormat;

public record ColorRGBA(int rgba) {
   public static final Codec<ColorRGBA> CODEC;

   public ColorRGBA {
      super();
   }

   public String toString() {
      return HexFormat.of().toHexDigits((long)this.rgba, 8);
   }

   static {
      CODEC = ExtraCodecs.STRING_ARGB_COLOR.xmap(ColorRGBA::new, ColorRGBA::rgba);
   }
}
