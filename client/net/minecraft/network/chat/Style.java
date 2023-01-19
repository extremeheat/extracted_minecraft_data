package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Style {
   public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);
   public static final Codec<Style> FORMATTING_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               TextColor.CODEC.optionalFieldOf("color").forGetter(var0x -> Optional.ofNullable(var0x.color)),
               Codec.BOOL.optionalFieldOf("bold").forGetter(var0x -> Optional.ofNullable(var0x.bold)),
               Codec.BOOL.optionalFieldOf("italic").forGetter(var0x -> Optional.ofNullable(var0x.italic)),
               Codec.BOOL.optionalFieldOf("underlined").forGetter(var0x -> Optional.ofNullable(var0x.underlined)),
               Codec.BOOL.optionalFieldOf("strikethrough").forGetter(var0x -> Optional.ofNullable(var0x.strikethrough)),
               Codec.BOOL.optionalFieldOf("obfuscated").forGetter(var0x -> Optional.ofNullable(var0x.obfuscated)),
               Codec.STRING.optionalFieldOf("insertion").forGetter(var0x -> Optional.ofNullable(var0x.insertion)),
               ResourceLocation.CODEC.optionalFieldOf("font").forGetter(var0x -> Optional.ofNullable(var0x.font))
            )
            .apply(var0, Style::create)
   );
   public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
   @Nullable
   final TextColor color;
   @Nullable
   final Boolean bold;
   @Nullable
   final Boolean italic;
   @Nullable
   final Boolean underlined;
   @Nullable
   final Boolean strikethrough;
   @Nullable
   final Boolean obfuscated;
   @Nullable
   final ClickEvent clickEvent;
   @Nullable
   final HoverEvent hoverEvent;
   @Nullable
   final String insertion;
   @Nullable
   final ResourceLocation font;

   private static Style create(
      Optional<TextColor> var0,
      Optional<Boolean> var1,
      Optional<Boolean> var2,
      Optional<Boolean> var3,
      Optional<Boolean> var4,
      Optional<Boolean> var5,
      Optional<String> var6,
      Optional<ResourceLocation> var7
   ) {
      return new Style(
         (TextColor)var0.orElse(null),
         (Boolean)var1.orElse(null),
         (Boolean)var2.orElse(null),
         (Boolean)var3.orElse(null),
         (Boolean)var4.orElse(null),
         (Boolean)var5.orElse(null),
         null,
         null,
         (String)var6.orElse(null),
         (ResourceLocation)var7.orElse(null)
      );
   }

   Style(
      @Nullable TextColor var1,
      @Nullable Boolean var2,
      @Nullable Boolean var3,
      @Nullable Boolean var4,
      @Nullable Boolean var5,
      @Nullable Boolean var6,
      @Nullable ClickEvent var7,
      @Nullable HoverEvent var8,
      @Nullable String var9,
      @Nullable ResourceLocation var10
   ) {
      super();
      this.color = var1;
      this.bold = var2;
      this.italic = var3;
      this.underlined = var4;
      this.strikethrough = var5;
      this.obfuscated = var6;
      this.clickEvent = var7;
      this.hoverEvent = var8;
      this.insertion = var9;
      this.font = var10;
   }

   @Nullable
   public TextColor getColor() {
      return this.color;
   }

   public boolean isBold() {
      return this.bold == Boolean.TRUE;
   }

   public boolean isItalic() {
      return this.italic == Boolean.TRUE;
   }

   public boolean isStrikethrough() {
      return this.strikethrough == Boolean.TRUE;
   }

   public boolean isUnderlined() {
      return this.underlined == Boolean.TRUE;
   }

   public boolean isObfuscated() {
      return this.obfuscated == Boolean.TRUE;
   }

   public boolean isEmpty() {
      return this == EMPTY;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion;
   }

   public ResourceLocation getFont() {
      return this.font != null ? this.font : DEFAULT_FONT;
   }

   public Style withColor(@Nullable TextColor var1) {
      return new Style(
         var1, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
      );
   }

   public Style withColor(@Nullable ChatFormatting var1) {
      return this.withColor(var1 != null ? TextColor.fromLegacyFormat(var1) : null);
   }

   public Style withColor(int var1) {
      return this.withColor(TextColor.fromRgb(var1));
   }

   public Style withBold(@Nullable Boolean var1) {
      return new Style(
         this.color, var1, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
      );
   }

   public Style withItalic(@Nullable Boolean var1) {
      return new Style(
         this.color, this.bold, var1, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
      );
   }

   public Style withUnderlined(@Nullable Boolean var1) {
      return new Style(
         this.color, this.bold, this.italic, var1, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
      );
   }

   public Style withStrikethrough(@Nullable Boolean var1) {
      return new Style(this.color, this.bold, this.italic, this.underlined, var1, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withObfuscated(@Nullable Boolean var1) {
      return new Style(
         this.color, this.bold, this.italic, this.underlined, this.strikethrough, var1, this.clickEvent, this.hoverEvent, this.insertion, this.font
      );
   }

   public Style withClickEvent(@Nullable ClickEvent var1) {
      return new Style(
         this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, var1, this.hoverEvent, this.insertion, this.font
      );
   }

   public Style withHoverEvent(@Nullable HoverEvent var1) {
      return new Style(
         this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, var1, this.insertion, this.font
      );
   }

   public Style withInsertion(@Nullable String var1) {
      return new Style(
         this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, var1, this.font
      );
   }

   public Style withFont(@Nullable ResourceLocation var1) {
      return new Style(
         this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, var1
      );
   }

   public Style applyFormat(ChatFormatting var1) {
      TextColor var2 = this.color;
      Boolean var3 = this.bold;
      Boolean var4 = this.italic;
      Boolean var5 = this.strikethrough;
      Boolean var6 = this.underlined;
      Boolean var7 = this.obfuscated;
      switch(var1) {
         case OBFUSCATED:
            var7 = true;
            break;
         case BOLD:
            var3 = true;
            break;
         case STRIKETHROUGH:
            var5 = true;
            break;
         case UNDERLINE:
            var6 = true;
            break;
         case ITALIC:
            var4 = true;
            break;
         case RESET:
            return EMPTY;
         default:
            var2 = TextColor.fromLegacyFormat(var1);
      }

      return new Style(var2, var3, var4, var6, var5, var7, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyLegacyFormat(ChatFormatting var1) {
      TextColor var2 = this.color;
      Boolean var3 = this.bold;
      Boolean var4 = this.italic;
      Boolean var5 = this.strikethrough;
      Boolean var6 = this.underlined;
      Boolean var7 = this.obfuscated;
      switch(var1) {
         case OBFUSCATED:
            var7 = true;
            break;
         case BOLD:
            var3 = true;
            break;
         case STRIKETHROUGH:
            var5 = true;
            break;
         case UNDERLINE:
            var6 = true;
            break;
         case ITALIC:
            var4 = true;
            break;
         case RESET:
            return EMPTY;
         default:
            var7 = false;
            var3 = false;
            var5 = false;
            var6 = false;
            var4 = false;
            var2 = TextColor.fromLegacyFormat(var1);
      }

      return new Style(var2, var3, var4, var6, var5, var7, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyFormats(ChatFormatting... var1) {
      TextColor var2 = this.color;
      Boolean var3 = this.bold;
      Boolean var4 = this.italic;
      Boolean var5 = this.strikethrough;
      Boolean var6 = this.underlined;
      Boolean var7 = this.obfuscated;

      for(ChatFormatting var11 : var1) {
         switch(var11) {
            case OBFUSCATED:
               var7 = true;
               break;
            case BOLD:
               var3 = true;
               break;
            case STRIKETHROUGH:
               var5 = true;
               break;
            case UNDERLINE:
               var6 = true;
               break;
            case ITALIC:
               var4 = true;
               break;
            case RESET:
               return EMPTY;
            default:
               var2 = TextColor.fromLegacyFormat(var11);
         }
      }

      return new Style(var2, var3, var4, var6, var5, var7, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyTo(Style var1) {
      if (this == EMPTY) {
         return var1;
      } else {
         return var1 == EMPTY
            ? this
            : new Style(
               this.color != null ? this.color : var1.color,
               this.bold != null ? this.bold : var1.bold,
               this.italic != null ? this.italic : var1.italic,
               this.underlined != null ? this.underlined : var1.underlined,
               this.strikethrough != null ? this.strikethrough : var1.strikethrough,
               this.obfuscated != null ? this.obfuscated : var1.obfuscated,
               this.clickEvent != null ? this.clickEvent : var1.clickEvent,
               this.hoverEvent != null ? this.hoverEvent : var1.hoverEvent,
               this.insertion != null ? this.insertion : var1.insertion,
               this.font != null ? this.font : var1.font
            );
      }
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");

      class 1Collector {
         private boolean isNotFirst;

         _Collector/* $QF was: 1Collector*/() {
            super();
         }

         private void prependSeparator() {
            if (this.isNotFirst) {
               var1.append(',');
            }

            this.isNotFirst = true;
         }

         void addFlagString(String var1x, @Nullable Boolean var2) {
            if (var2 != null) {
               this.prependSeparator();
               if (!var2) {
                  var1.append('!');
               }

               var1.append(var1x);
            }
         }

         void addValueString(String var1x, @Nullable Object var2) {
            if (var2 != null) {
               this.prependSeparator();
               var1.append(var1x);
               var1.append('=');
               var1.append(var2);
            }
         }
      }

      1Collector var2 = new 1Collector();
      var2.addValueString("color", this.color);
      var2.addFlagString("bold", this.bold);
      var2.addFlagString("italic", this.italic);
      var2.addFlagString("underlined", this.underlined);
      var2.addFlagString("strikethrough", this.strikethrough);
      var2.addFlagString("obfuscated", this.obfuscated);
      var2.addValueString("clickEvent", this.clickEvent);
      var2.addValueString("hoverEvent", this.hoverEvent);
      var2.addValueString("insertion", this.insertion);
      var2.addValueString("font", this.font);
      var1.append("}");
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Style)) {
         return false;
      } else {
         Style var2 = (Style)var1;
         return this.isBold() == var2.isBold()
            && Objects.equals(this.getColor(), var2.getColor())
            && this.isItalic() == var2.isItalic()
            && this.isObfuscated() == var2.isObfuscated()
            && this.isStrikethrough() == var2.isStrikethrough()
            && this.isUnderlined() == var2.isUnderlined()
            && Objects.equals(this.getClickEvent(), var2.getClickEvent())
            && Objects.equals(this.getHoverEvent(), var2.getHoverEvent())
            && Objects.equals(this.getInsertion(), var2.getInsertion())
            && Objects.equals(this.getFont(), var2.getFont());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion
      );
   }

   public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
      public Serializer() {
         super();
      }

      @Nullable
      public Style deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            JsonObject var4 = var1.getAsJsonObject();
            if (var4 == null) {
               return null;
            } else {
               Boolean var5 = getOptionalFlag(var4, "bold");
               Boolean var6 = getOptionalFlag(var4, "italic");
               Boolean var7 = getOptionalFlag(var4, "underlined");
               Boolean var8 = getOptionalFlag(var4, "strikethrough");
               Boolean var9 = getOptionalFlag(var4, "obfuscated");
               TextColor var10 = getTextColor(var4);
               String var11 = getInsertion(var4);
               ClickEvent var12 = getClickEvent(var4);
               HoverEvent var13 = getHoverEvent(var4);
               ResourceLocation var14 = getFont(var4);
               return new Style(var10, var5, var6, var7, var8, var9, var12, var13, var11, var14);
            }
         } else {
            return null;
         }
      }

      @Nullable
      private static ResourceLocation getFont(JsonObject var0) {
         if (var0.has("font")) {
            String var1 = GsonHelper.getAsString(var0, "font");

            try {
               return new ResourceLocation(var1);
            } catch (ResourceLocationException var3) {
               throw new JsonSyntaxException("Invalid font name: " + var1);
            }
         } else {
            return null;
         }
      }

      @Nullable
      private static HoverEvent getHoverEvent(JsonObject var0) {
         if (var0.has("hoverEvent")) {
            JsonObject var1 = GsonHelper.getAsJsonObject(var0, "hoverEvent");
            HoverEvent var2 = HoverEvent.deserialize(var1);
            if (var2 != null && var2.getAction().isAllowedFromServer()) {
               return var2;
            }
         }

         return null;
      }

      @Nullable
      private static ClickEvent getClickEvent(JsonObject var0) {
         if (var0.has("clickEvent")) {
            JsonObject var1 = GsonHelper.getAsJsonObject(var0, "clickEvent");
            String var2 = GsonHelper.getAsString(var1, "action", null);
            ClickEvent.Action var3 = var2 == null ? null : ClickEvent.Action.getByName(var2);
            String var4 = GsonHelper.getAsString(var1, "value", null);
            if (var3 != null && var4 != null && var3.isAllowedFromServer()) {
               return new ClickEvent(var3, var4);
            }
         }

         return null;
      }

      @Nullable
      private static String getInsertion(JsonObject var0) {
         return GsonHelper.getAsString(var0, "insertion", null);
      }

      @Nullable
      private static TextColor getTextColor(JsonObject var0) {
         if (var0.has("color")) {
            String var1 = GsonHelper.getAsString(var0, "color");
            return TextColor.parseColor(var1);
         } else {
            return null;
         }
      }

      @Nullable
      private static Boolean getOptionalFlag(JsonObject var0, String var1) {
         return var0.has(var1) ? var0.get(var1).getAsBoolean() : null;
      }

      @Nullable
      public JsonElement serialize(Style var1, Type var2, JsonSerializationContext var3) {
         if (var1.isEmpty()) {
            return null;
         } else {
            JsonObject var4 = new JsonObject();
            if (var1.bold != null) {
               var4.addProperty("bold", var1.bold);
            }

            if (var1.italic != null) {
               var4.addProperty("italic", var1.italic);
            }

            if (var1.underlined != null) {
               var4.addProperty("underlined", var1.underlined);
            }

            if (var1.strikethrough != null) {
               var4.addProperty("strikethrough", var1.strikethrough);
            }

            if (var1.obfuscated != null) {
               var4.addProperty("obfuscated", var1.obfuscated);
            }

            if (var1.color != null) {
               var4.addProperty("color", var1.color.serialize());
            }

            if (var1.insertion != null) {
               var4.add("insertion", var3.serialize(var1.insertion));
            }

            if (var1.clickEvent != null) {
               JsonObject var5 = new JsonObject();
               var5.addProperty("action", var1.clickEvent.getAction().getName());
               var5.addProperty("value", var1.clickEvent.getValue());
               var4.add("clickEvent", var5);
            }

            if (var1.hoverEvent != null) {
               var4.add("hoverEvent", var1.hoverEvent.serialize());
            }

            if (var1.font != null) {
               var4.addProperty("font", var1.font.toString());
            }

            return var4;
         }
      }
   }
}
