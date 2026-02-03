package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public enum ChatFormatting implements StringRepresentable {
   BLACK("BLACK", '0', 0, 0),
   DARK_BLUE("DARK_BLUE", '1', 1, 170),
   DARK_GREEN("DARK_GREEN", '2', 2, 43520),
   DARK_AQUA("DARK_AQUA", '3', 3, 43690),
   DARK_RED("DARK_RED", '4', 4, 11141120),
   DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
   GOLD("GOLD", '6', 6, 16755200),
   GRAY("GRAY", '7', 7, 11184810),
   DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
   BLUE("BLUE", '9', 9, 5592575),
   GREEN("GREEN", 'a', 10, 5635925),
   AQUA("AQUA", 'b', 11, 5636095),
   RED("RED", 'c', 12, 16733525),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
   YELLOW("YELLOW", 'e', 14, 16777045),
   WHITE("WHITE", 'f', 15, 16777215),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1, (Integer)null);

   public static final Codec<ChatFormatting> CODEC = StringRepresentable.<ChatFormatting>fromEnum(ChatFormatting::values);
   public static final Codec<ChatFormatting> COLOR_CODEC = CODEC.validate((color) -> color.isFormat() ? DataResult.error(() -> "Formatting was not a valid color: " + String.valueOf(color)) : DataResult.success(color));
   public static final char PREFIX_CODE = '\u00a7';
   private static final Map<String, ChatFormatting> FORMATTING_BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((format) -> cleanName(format.name), (f) -> f));
   private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
   private final String name;
   private final char code;
   private final boolean isFormat;
   private final String toString;
   private final int id;
   private final @Nullable Integer color;

   private static String cleanName(final String name) {
      return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private ChatFormatting(final String name, final @Nullable char code, final int id, final Integer color) {
      this(name, code, false, id, color);
   }

   private ChatFormatting(final String name, final char code, final boolean isFormat) {
      this(name, code, isFormat, -1, (Integer)null);
   }

   private ChatFormatting(final String name, final char code, final @Nullable boolean isFormat, final int id, final Integer color) {
      this.name = name;
      this.code = code;
      this.isFormat = isFormat;
      this.id = id;
      this.color = color;
      this.toString = "\u00a7" + String.valueOf(code);
   }

   public char getChar() {
      return this.code;
   }

   public int getId() {
      return this.id;
   }

   public boolean isFormat() {
      return this.isFormat;
   }

   public boolean isColor() {
      return !this.isFormat && this != RESET;
   }

   public @Nullable Integer getColor() {
      return this.color;
   }

   public String getName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.toString;
   }

   @Contract("!null->!null;_->_")
   public static @Nullable String stripFormatting(final @Nullable String input) {
      return input == null ? null : STRIP_FORMATTING_PATTERN.matcher(input).replaceAll("");
   }

   public static @Nullable ChatFormatting getByName(final @Nullable String name) {
      return name == null ? null : (ChatFormatting)FORMATTING_BY_NAME.get(cleanName(name));
   }

   public static @Nullable ChatFormatting getById(final int id) {
      if (id < 0) {
         return RESET;
      } else {
         for(ChatFormatting format : values()) {
            if (format.getId() == id) {
               return format;
            }
         }

         return null;
      }
   }

   public static @Nullable ChatFormatting getByCode(final char code) {
      char sanitized = Character.toLowerCase(code);

      for(ChatFormatting format : values()) {
         if (format.code == sanitized) {
            return format;
         }
      }

      return null;
   }

   public static Collection<String> getNames(final boolean getColors, final boolean getFormats) {
      List<String> result = Lists.newArrayList();

      for(ChatFormatting format : values()) {
         if ((!format.isColor() || getColors) && (!format.isFormat() || getFormats)) {
            result.add(format.getName());
         }
      }

      return result;
   }

   public String getSerializedName() {
      return this.getName();
   }

   // $FF: synthetic method
   private static ChatFormatting[] $values() {
      return new ChatFormatting[]{BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET};
   }
}
