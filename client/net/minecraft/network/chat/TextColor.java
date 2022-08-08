package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;

public final class TextColor {
   private static final String CUSTOM_COLOR_PREFIX = "#";
   public static final Codec<TextColor> CODEC;
   private static final Map<ChatFormatting, TextColor> LEGACY_FORMAT_TO_COLOR;
   private static final Map<String, TextColor> NAMED_COLORS;
   private final int value;
   @Nullable
   private final String name;

   private TextColor(int var1, String var2) {
      super();
      this.value = var1;
      this.name = var2;
   }

   private TextColor(int var1) {
      super();
      this.value = var1;
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         TextColor var2 = (TextColor)var1;
         return this.value == var2.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.value, this.name});
   }

   public String toString() {
      return this.name != null ? this.name : this.formatValue();
   }

   @Nullable
   public static TextColor fromLegacyFormat(ChatFormatting var0) {
      return (TextColor)LEGACY_FORMAT_TO_COLOR.get(var0);
   }

   public static TextColor fromRgb(int var0) {
      return new TextColor(var0);
   }

   @Nullable
   public static TextColor parseColor(String var0) {
      if (var0.startsWith("#")) {
         try {
            int var1 = Integer.parseInt(var0.substring(1), 16);
            return fromRgb(var1);
         } catch (NumberFormatException var2) {
            return null;
         }
      } else {
         return (TextColor)NAMED_COLORS.get(var0);
      }
   }

   static {
      CODEC = Codec.STRING.comapFlatMap((var0) -> {
         TextColor var1 = parseColor(var0);
         return var1 != null ? DataResult.success(var1) : DataResult.error("String is not a valid color name or hex color code");
      }, TextColor::serialize);
      LEGACY_FORMAT_TO_COLOR = (Map)Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), (var0) -> {
         return new TextColor(var0.getColor(), var0.getName());
      }));
      NAMED_COLORS = (Map)LEGACY_FORMAT_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0.name;
      }, Function.identity()));
   }
}
