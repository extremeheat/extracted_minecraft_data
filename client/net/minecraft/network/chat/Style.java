package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Style {
   public static final Style EMPTY = new Style((TextColor)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (ClickEvent)null, (HoverEvent)null, (String)null, (ResourceLocation)null);
   public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
   @Nullable
   private final TextColor color;
   @Nullable
   private final Boolean bold;
   @Nullable
   private final Boolean italic;
   @Nullable
   private final Boolean underlined;
   @Nullable
   private final Boolean strikethrough;
   @Nullable
   private final Boolean obfuscated;
   @Nullable
   private final ClickEvent clickEvent;
   @Nullable
   private final HoverEvent hoverEvent;
   @Nullable
   private final String insertion;
   @Nullable
   private final ResourceLocation font;

   private Style(@Nullable TextColor var1, @Nullable Boolean var2, @Nullable Boolean var3, @Nullable Boolean var4, @Nullable Boolean var5, @Nullable Boolean var6, @Nullable ClickEvent var7, @Nullable HoverEvent var8, @Nullable String var9, @Nullable ResourceLocation var10) {
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
      return new Style(var1, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withColor(@Nullable ChatFormatting var1) {
      return this.withColor(var1 != null ? TextColor.fromLegacyFormat(var1) : null);
   }

   public Style withBold(@Nullable Boolean var1) {
      return new Style(this.color, var1, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withItalic(@Nullable Boolean var1) {
      return new Style(this.color, this.bold, var1, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withUnderlined(@Nullable Boolean var1) {
      return new Style(this.color, this.bold, this.italic, var1, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withClickEvent(@Nullable ClickEvent var1) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, var1, this.hoverEvent, this.insertion, this.font);
   }

   public Style withHoverEvent(@Nullable HoverEvent var1) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, var1, this.insertion, this.font);
   }

   public Style withInsertion(@Nullable String var1) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, var1, this.font);
   }

   public Style withFont(@Nullable ResourceLocation var1) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, var1);
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
      ChatFormatting[] var8 = var1;
      int var9 = var1.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ChatFormatting var11 = var8[var10];
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
         return var1 == EMPTY ? this : new Style(this.color != null ? this.color : var1.color, this.bold != null ? this.bold : var1.bold, this.italic != null ? this.italic : var1.italic, this.underlined != null ? this.underlined : var1.underlined, this.strikethrough != null ? this.strikethrough : var1.strikethrough, this.obfuscated != null ? this.obfuscated : var1.obfuscated, this.clickEvent != null ? this.clickEvent : var1.clickEvent, this.hoverEvent != null ? this.hoverEvent : var1.hoverEvent, this.insertion != null ? this.insertion : var1.insertion, this.font != null ? this.font : var1.font);
      }
   }

   public String toString() {
      return "Style{ color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + ", font=" + this.getFont() + '}';
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Style)) {
         return false;
      } else {
         Style var2 = (Style)var1;
         return this.isBold() == var2.isBold() && Objects.equals(this.getColor(), var2.getColor()) && this.isItalic() == var2.isItalic() && this.isObfuscated() == var2.isObfuscated() && this.isStrikethrough() == var2.isStrikethrough() && this.isUnderlined() == var2.isUnderlined() && Objects.equals(this.getClickEvent(), var2.getClickEvent()) && Objects.equals(this.getHoverEvent(), var2.getHoverEvent()) && Objects.equals(this.getInsertion(), var2.getInsertion()) && Objects.equals(this.getFont(), var2.getFont());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
   }

   // $FF: synthetic method
   Style(TextColor var1, Boolean var2, Boolean var3, Boolean var4, Boolean var5, Boolean var6, ClickEvent var7, HoverEvent var8, String var9, ResourceLocation var10, Object var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
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
            String var2 = GsonHelper.getAsString(var1, "action", (String)null);
            ClickEvent.Action var3 = var2 == null ? null : ClickEvent.Action.getByName(var2);
            String var4 = GsonHelper.getAsString(var1, "value", (String)null);
            if (var3 != null && var4 != null && var3.isAllowedFromServer()) {
               return new ClickEvent(var3, var4);
            }
         }

         return null;
      }

      @Nullable
      private static String getInsertion(JsonObject var0) {
         return GsonHelper.getAsString(var0, "insertion", (String)null);
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

      // $FF: synthetic method
      @Nullable
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((Style)var1, var2, var3);
      }

      // $FF: synthetic method
      @Nullable
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
