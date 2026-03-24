package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.Nullable;

public final class TextColor {
   private static final String CUSTOM_COLOR_PREFIX = "#";
   public static final Codec<TextColor> CODEC;
   private static final Map<ChatFormatting, TextColor> LEGACY_FORMAT_TO_COLOR;
   private static final Map<String, TextColor> NAMED_COLORS;
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
      return (TextColor)LEGACY_FORMAT_TO_COLOR.get(format);
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
      LEGACY_FORMAT_TO_COLOR = (Map)Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), (f) -> new TextColor(f.getColor(), f.getName())));
      NAMED_COLORS = (Map)LEGACY_FORMAT_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap((e) -> e.name, Function.identity()));
   }
}
