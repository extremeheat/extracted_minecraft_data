package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.Nullable;

public final class TextColor {
   private static final String CUSTOM_COLOR_PREFIX = "#";
   public static final Codec<TextColor> CODEC;
   private static final Map<String, TextColor> NAMED_COLORS;
   public static final TextColor BLACK;
   public static final TextColor DARK_BLUE;
   public static final TextColor DARK_GREEN;
   public static final TextColor DARK_AQUA;
   public static final TextColor DARK_RED;
   public static final TextColor DARK_PURPLE;
   public static final TextColor GOLD;
   public static final TextColor GRAY;
   public static final TextColor DARK_GRAY;
   public static final TextColor BLUE;
   public static final TextColor GREEN;
   public static final TextColor AQUA;
   public static final TextColor RED;
   public static final TextColor LIGHT_PURPLE;
   public static final TextColor YELLOW;
   public static final TextColor WHITE;
   private final int value;
   private final @Nullable String name;

   private TextColor(final int value, final String name) {
      super();
      this.value = value & 16777215;
      this.name = name;
   }

   private TextColor(final int value) {
      super();
      this.value = value & 16777215;
      this.name = null;
   }

   private static TextColor named(final String name, final int rgb) {
      TextColor result = new TextColor(rgb, name);
      NAMED_COLORS.put(name, result);
      return result;
   }

   public int getValue() {
      return this.value;
   }

   public String serialize() {
      return this.name != null ? this.name : this.formatValue();
   }

   private String formatValue() {
      return String.format(Locale.ROOT, "#%06X", this.value);
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TextColor other = (TextColor)o;
         return this.value == other.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.value, this.name});
   }

   public String toString() {
      return this.serialize();
   }

   public static @Nullable TextColor fromLegacyFormat(final ChatFormatting format) {
      TextColor var10000;
      switch (format) {
         case BLACK -> var10000 = BLACK;
         case DARK_BLUE -> var10000 = DARK_BLUE;
         case DARK_GREEN -> var10000 = DARK_GREEN;
         case DARK_AQUA -> var10000 = DARK_AQUA;
         case DARK_RED -> var10000 = DARK_RED;
         case DARK_PURPLE -> var10000 = DARK_PURPLE;
         case GOLD -> var10000 = GOLD;
         case GRAY -> var10000 = GRAY;
         case DARK_GRAY -> var10000 = DARK_GRAY;
         case BLUE -> var10000 = BLUE;
         case GREEN -> var10000 = GREEN;
         case AQUA -> var10000 = AQUA;
         case RED -> var10000 = RED;
         case LIGHT_PURPLE -> var10000 = LIGHT_PURPLE;
         case YELLOW -> var10000 = YELLOW;
         case WHITE -> var10000 = WHITE;
         default -> var10000 = null;
      }

      return var10000;
   }

   public static TextColor fromRgb(final int rgb) {
      return new TextColor(rgb);
   }

   public static DataResult<TextColor> parseColor(final String color) {
      if (color.startsWith("#")) {
         try {
            int value = Integer.parseInt(color.substring(1), 16);
            return value >= 0 && value <= 16777215 ? DataResult.success(fromRgb(value), Lifecycle.stable()) : DataResult.error(() -> "Color value out of range: " + color);
         } catch (NumberFormatException var2) {
            return DataResult.error(() -> "Invalid color value: " + color);
         }
      } else {
         TextColor predefinedColor = (TextColor)NAMED_COLORS.get(color);
         return predefinedColor == null ? DataResult.error(() -> "Invalid color name: " + color) : DataResult.success(predefinedColor, Lifecycle.stable());
      }
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(TextColor::parseColor, TextColor::serialize);
      NAMED_COLORS = new HashMap();
      BLACK = named("black", 0);
      DARK_BLUE = named("dark_blue", 170);
      DARK_GREEN = named("dark_green", 43520);
      DARK_AQUA = named("dark_aqua", 43690);
      DARK_RED = named("dark_red", 11141120);
      DARK_PURPLE = named("dark_purple", 11141290);
      GOLD = named("gold", 16755200);
      GRAY = named("gray", 11184810);
      DARK_GRAY = named("dark_gray", 5592405);
      BLUE = named("blue", 5592575);
      GREEN = named("green", 5635925);
      AQUA = named("aqua", 5636095);
      RED = named("red", 16733525);
      LIGHT_PURPLE = named("light_purple", 16733695);
      YELLOW = named("yellow", 16777045);
      WHITE = named("white", 16777215);
   }
}
